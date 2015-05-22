package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropCheck.common;
import static tools.PropCheck.erib;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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

	private static AtomicInteger countSendMess = new AtomicInteger(0);

	private long delay;

	public static AtomicLong delayForStop = new AtomicLong();

	private static Lock lock = new ReentrantLock();

	private static final Logger logger = LogManager
			.getFormatterLogger(Request.class.getName());

	// test type
	private String testType;

	// start app count
	private int countAppStart;

	// setting for step test
	private String settings;

	public static AtomicInteger currentStep = new AtomicInteger(1);

	// scenario step test
	private static ConcurrentHashMap<Integer, String> scenario = new ConcurrentHashMap<Integer, String>();

	public Request() {

		this.testType = common.getChildText("testType");

		this.countAppStart = Integer.parseInt(erib
				.getChildText("countStartApp"));

		if (this.countAppStart == 0) {

			logger.info("Count app zero! Set value by default 1");

			this.countAppStart = 1;

		}

		if (testType.equalsIgnoreCase("step")) {

			this.settings = erib.getChildText("step");

			String[] tempArray = this.settings.split(";");

			int i = 1;

			for (String string : tempArray) {

				scenario.put(i, string);

				i++;
			}

		}

		float temp_delay = ((float) 3600 / this.countAppStart) * 1000;

		this.delay = (long) temp_delay;

		delayForStop.set(this.delay);

		logger.debug("Test_type: %s", this.testType);

		logger.debug("Count_app_start: %s", this.countAppStart);

		logger.debug("Delay: %s", this.delay);

		logger.debug("Start time: %s", SBOLMqJms.startTime);

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

		String settings = "";

		String settingsFuture = "";

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

			logger.debug("Settings: %s", settings);

			logger.debug("Future settings: %s", settingsFuture);

		}

		String queue = erib.getChildText("queueTo");

		// Send to ESB
		while (SBOLMqJms.flagRequest.get()) {

			long start = System.currentTimeMillis();

			countSendMess.getAndIncrement();

			String request = RqToESB.getInstance().getRequest();

			TextMessage outputMsg = null;

			MessageProducer producer = null;

			try {

				outputMsg = session.createTextMessage(request);

				MQQueue queueSend = (MQQueue) session.createQueue(queue);

				producer = session.createProducer(queueSend);

				producer.send(outputMsg);

				logger.debug("Request to ESB success: %s", request);

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

				lock.lock();

				try {

					long now = System.currentTimeMillis();

					long diff = now - SBOLMqJms.startTime.get();

					// sum enter with duration step
					long timeStep = (Long.parseLong(settingsArray[0]) + Long
							.parseLong(settingsArray[1])) * 60 * 1000;

					// new step
					if (diff > timeStep
							&& (currentStep.get() != scenario.size())) {

						logger.info("Prev start_time: %s", SBOLMqJms.startTime);

						logger.info("Prev delay: %s", this.delay);

						this.countAppStart = Integer
								.parseInt(settingsFutureArray[2]);

						if (this.countAppStart == 0) {

							logger.info(
									"[Step] %s; Count apps zero! Set default 1",
									currentStep.get());

							this.countAppStart = 1;

						}

						currentStep.getAndIncrement();

						float temp = ((float) 3600 / this.countAppStart) * 1000;

						this.delay = (long) temp;

						delayForStop.set(this.delay);

						SBOLMqJms.startTime.set(System.currentTimeMillis());

						logger.info("Start time reset. New startTime: %s",
								SBOLMqJms.startTime);

						logger.info("New delay: %s", this.delay);

						// re-read setting
						settings = scenario.get(currentStep.get());

						int stepNext = currentStep.get() + 1;

						settingsFuture = scenario.get((stepNext > scenario
								.size()) ? scenario.size() : stepNext);

						settingsArray = settings.split(",");

						settingsFutureArray = settingsFuture.split(",");

						logger.debug("Re-read settings: %s", settings);

						logger.debug("Re-read future settings: %s",
								settingsFuture);

						if (!(currentStep.get() != scenario.size())) {

							logger.info("Last step succeed!");
						}

					}

				} finally {

					lock.unlock();
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

			if (countSendMess.get() > 260) {

				Runtime r = Runtime.getRuntime();

				logger.info("Total memory: %s", r.totalMemory());

				logger.info("Memory before gc %s", r.freeMemory());

				r.gc();

				logger.info("Memory after gc %s", r.freeMemory());

				countSendMess.set(0);

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
