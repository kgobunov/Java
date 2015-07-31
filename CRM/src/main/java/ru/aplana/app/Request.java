package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropCheck.common;
import static tools.PropCheck.crm;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private MQQueueConnectionFactory factory;

	private static final Logger logger = LogManager
			.getFormatterLogger(Request.class.getName());

	private long delay;

	private String testType;

	// increment app on step
	private int countAppStart;

	// setting for step test
	private String settings;

	public static AtomicLong delayForStop = new AtomicLong();

	private int currentStep = 1;

	private boolean flagNewStep = false;

	// scenario step test
	private HashMap<Integer, String> scenario = new HashMap<Integer, String>();

	private long startTime;

	public Request() {

		this.testType = common.getChildText("testType");

		this.countAppStart = Integer
				.parseInt(crm.getChildText("countStartApp"));

		if (this.countAppStart == 0) {

			logger.info("Count app zero! Set value by default 1");

			this.countAppStart = 1;

		}

		if (this.testType.equalsIgnoreCase("step")) {

			this.startTime = System.currentTimeMillis();

			this.settings = crm.getChildText("step");

			String[] tempArray = this.settings.split(";");

			int i = 1;

			for (String string : tempArray) {

				this.scenario.put(i, string);

				i++;
			}

		}

		float temp = ((float) 3600 / this.countAppStart) * 1000;

		this.delay = (long) temp;

		delayForStop.set(this.delay);

		logger.debug("Test_type: %s", this.testType);

		logger.debug("Count_app_start: %s", this.countAppStart);

		logger.debug("Delay: %s", this.delay);

		logger.debug("Start time: %s", this.startTime);

		try {

			this.factory = MQConn.getFactory();

			this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		} catch (NumberFormatException | JMSException e) {

			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public void run() {

		MQQueueConnection connection = getConnection(this.factory, null, null);

		try {

			connection.start();

		} catch (JMSException e1) {

			logger.error(e1.getMessage(), e1);

		}

		logger.debug(
				"Thread is connected to MQ server with parameters: HostName: %s; Port: %s; QueueManager: %s; Channel: %s",
				this.factory.getHostName(), this.factory.getPort(),
				this.factory.getQueueManager(), this.factory.getChannel());

		MQQueueSession session = getSession(connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		// for step test
		String settings = null;

		String settingsFuture = null;

		String[] settingsArray = null;

		String[] settingsFutureArray = null;

		if (this.testType.equalsIgnoreCase("step")) {

			// read first time
			settings = this.scenario.get(this.currentStep);

			int stepNext = this.currentStep + 1;

			settingsFuture = this.scenario
					.get((stepNext > this.scenario.size()) ? this.scenario
							.size() : stepNext);

			settingsArray = settings.split(",");

			settingsFutureArray = settingsFuture.split(",");

			logger.debug("Settings: " + settings);

		}

		String queue = crm.getChildText("queueTo");

		// Send to ESB
		while (Main.flagRequest.get()) {

			long start = System.currentTimeMillis();

			String request = RqToESB.getInstance(settingsArray,
					this.flagNewStep).getRequest();

			TextMessage outputMsg = null;

			MessageProducer producer = null;

			try {

				outputMsg = session.createTextMessage(request);

				// ESB.CRM.IN
				MQQueue queueSend = (MQQueue) session.createQueue(queue);

				producer = session.createProducer(queueSend);

				producer.send(outputMsg);

				logger.debug("Request to ESB success: " + request);

			} catch (JMSException e1) {

				logger.error("Can't send request to ESB: %s", e1.getMessage(),
						e1);

			} finally {

				try {

					if (null != producer) {

						producer.close();

					}

				} catch (JMSException e) {

					logger.error(e.getMessage(), e);

				}
			}

			// if test type equals step - must be up count apps
			if (this.testType.equalsIgnoreCase("step")) {

				long now = System.currentTimeMillis();

				long diff = now - this.startTime;

				// sum enter with duration step
				long timeStep = (Long.parseLong(settingsArray[0]) + Long
						.parseLong(settingsArray[1])) * 60 * 1000;

				// new step
				if (diff > timeStep
						&& (this.currentStep != this.scenario.size())) {

					logger.info("Prev start_time: %s", this.startTime);

					logger.info("Prev delay: %s", this.delay);

					this.countAppStart = Integer
							.parseInt(settingsFutureArray[2]);

					if (this.countAppStart == 0) {

						logger.info("Count app zero! Set value by default 1");

						this.countAppStart = 1;

					}

					this.currentStep++;

					float temp = ((float) 3600 / this.countAppStart) * 1000;

					this.delay = (long) temp;

					delayForStop.set(this.delay);

					this.startTime = System.currentTimeMillis();

					logger.info("Start time reset. New startTime: %s",
							this.startTime);

					logger.info("New delay: %s", this.delay);

					// re-read setting
					settings = this.scenario.get(this.currentStep);

					int stepNext = this.currentStep + 1;

					settingsFuture = this.scenario
							.get((stepNext > this.scenario.size()) ? this.scenario
									.size() : stepNext);

					settingsArray = settings.split(",");

					settingsFutureArray = settingsFuture.split(",");

					logger.debug("Re-read settings: %s", settings);

					logger.debug("Re-read future settings: %s", settingsFuture);

					if (!(this.currentStep != this.scenario.size())) {

						logger.info("Last step succeed!");
					}

					this.flagNewStep = true;

				}

			}

			long end = System.currentTimeMillis();

			long diff = end - start;

			logger.debug("Run send: %s ms", diff);

			long delay = (diff > 0) ? this.delay - diff : 0;

			try {

				if (delay > 0) {

					Thread.sleep(delay);

				} else {

					logger.debug(
							"Out of delay between iterations! Default delay: %s; Real delay: %s",
							this.delay, diff);

				}

			} catch (InterruptedException e) {

				logger.error("Interrupted thread: %s", e.getMessage(), e);

			}

		}

		// if can't send requests to ESB
		try {

			if (null != connection) {

				connection.close();

			}

			if (null != session) {

				session.close();

			}

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		}

	}
}
