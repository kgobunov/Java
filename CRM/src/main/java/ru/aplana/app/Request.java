package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropCheck.common;
import static tools.PropCheck.crm;
import static tools.PropCheck.debug;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import requests.RqToESB;
import tools.MQConn;


import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;

/**
 * Send request to ESB (Enterprise System Bus)
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class Request implements Runnable {

	private MQQueueSession session;

	private MQQueueConnection connection;

	private MQQueueConnectionFactory factory;

	private static AtomicInteger countSendMess = new AtomicInteger(0);

	public static volatile long delay;

	private String testType;

	// increment app on step
	private int countAppStart;

	// setting for step test
	private String settings;

	public static AtomicInteger currentStep = new AtomicInteger(1);

	public static volatile boolean flagNewStep = false;

	// scenario step test
	public static HashMap<Integer, String> scenario = new HashMap<Integer, String>();

	public Request() throws JMSException {

		this.testType = common.getChildText("testType");

		this.countAppStart = Integer.parseInt(crm.getChildText("countStartApp"));
		
		if (this.countAppStart == 0) {
			
			loggerInfo.info("Count app zero! Set value by default 1");
			
			this.countAppStart = 1;
			
		}

		if (testType.equalsIgnoreCase("step")) {

			this.settings = crm.getChildText("step");

			String[] tempArray = this.settings.split(";");

			int i = 1;

			for (String string : tempArray) {

				scenario.put(i, string);

				i++;
			}

		}

		float temp_delay = ((float) 3600 / this.countAppStart) * 1000;

		delay = (long) temp_delay;

		if (debug) {

			loggerInfo.info("Test_type: " + this.testType);

			loggerInfo.info("Count_app_start: " + this.countAppStart);

			loggerInfo.info("Delay: " + delay);

			loggerInfo.info("Start time: " + CRMMqJms.startTime);

		}

		this.factory = MQConn.getFactory();

		this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);
	}

	@Override
	public void run() {

		try {

			this.connection = getConnection(this.factory);

			this.connection.start();

			if (debug) {

				loggerInfo
						.info("Thread is connected to MQ server with parameters: HostName: "
								+ this.factory.getHostName()
								+ "; Port: "
								+ this.factory.getPort()
								+ "; QueueManager: "
								+ this.factory.getQueueManager()
								+ "; Channel: " + this.factory.getChannel());
			}

			this.session = getSession(this.connection, false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			// for step test
			String settings = null;

			String settingsFuture = null;

			String[] settingsArray = null;

			String[] settingsFutureArray = null;

			if (this.testType.equalsIgnoreCase("step")) {

				// read first time
				settings = scenario.get(currentStep.get());

				int stepNext = currentStep.get() + 1;

				settingsFuture = scenario
						.get((stepNext > scenario.size()) ? scenario.size()
								: stepNext);

				settingsArray = settings.split(",");

				settingsFutureArray = settingsFuture.split(",");

				if (debug) {

					loggerInfo.info("Settings: " + settings);

				}

			}

			String queue = crm.getChildText("queueTo");

			// Send to ESB
			while (CRMMqJms.flagRequest) {

				String request = new RqToESB(settingsArray).getRq();

				TextMessage outputMsg = this.session.createTextMessage(request);

				// ESB.CRM.IN
				MQQueue queueSend = (MQQueue) this.session.createQueue(queue);

				MessageProducer producer = this.session
						.createProducer(queueSend);

				producer.send(outputMsg);

				producer.close();

				if (debug) {

					loggerInfo.info("Request to ESB success: " + request);

					loggerInfo.info("Connection closed!");

				}

				// if test type equals step - must be up count apps
				if (this.testType.equalsIgnoreCase("step")) {

					long now = System.currentTimeMillis();

					long diff = now - CRMMqJms.startTime;

					// sum enter with duration step
					long timeStep = (Long.parseLong(settingsArray[0]) + Long
							.parseLong(settingsArray[1])) * 60 * 1000;

					// new step
					if (diff > timeStep
							&& (currentStep.get() != scenario.size())) {

						loggerInfo.info("Prev start_time: "
								+ CRMMqJms.startTime);

						loggerInfo.info("Prev delay: " + delay);

						this.countAppStart = Integer
								.parseInt(settingsFutureArray[2]);
						
						if (this.countAppStart == 0) {
							
							loggerInfo.info("Count app zero! Set value by default 1");
							
							this.countAppStart = 1;
							
						}

						currentStep.getAndIncrement();

						float temp = ((float) 3600 / this.countAppStart) * 1000;

						delay = (long) temp;

						CRMMqJms.startTime = System.currentTimeMillis();

						loggerInfo.info("Start time reset. New startTime: "
								+ CRMMqJms.startTime);

						loggerInfo.info("New delay: " + delay);

						// re-read setting
						settings = scenario.get(currentStep.get());

						int stepNext = currentStep.get() + 1;

						settingsFuture = scenario.get((stepNext > scenario
								.size()) ? scenario.size() : stepNext);

						settingsArray = settings.split(",");

						settingsFutureArray = settingsFuture.split(",");

						if (debug) {

							loggerInfo.info("Re-read settings: " + settings);

							loggerInfo.info("Re-read future settings: "
									+ settingsFuture);

						}

						if (!(currentStep.get() != scenario.size())) {

							loggerInfo.info("Last step succeed!");
						}

						flagNewStep = true;

					}

				}

				try {

					Thread.sleep((long) delay);

				} catch (InterruptedException e) {

					loggerSevere.severe("Error: Interrupted thread: "
							+ e.getMessage());

					e.printStackTrace();
				}

				if (countSendMess.get() > 260) {

					Runtime r = Runtime.getRuntime();

					loggerInfo.info("Total memory: " + r.totalMemory());

					loggerInfo.info("Memory before gc " + r.freeMemory());

					r.gc();

					loggerInfo.info("Memory after gc " + r.freeMemory());

					countSendMess.set(0);

				}

			}

		} catch (JMSException e) {

			loggerSevere.severe("Error: Can't send request to ESB: "
					+ e.getMessage());

			e.printStackTrace();

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();

		} finally {

			try {

				if (null != this.session) {

					this.session.close();

				}

				if (null != this.connection) {

					this.connection.close();

				}

				loggerInfo.info("Session closed!");

				loggerInfo.info("Connection closed!");

			} catch (JMSException e1) {

				e1.printStackTrace();
			}
		}

	}
}
