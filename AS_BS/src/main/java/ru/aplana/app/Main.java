package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getConsumer;
import static tools.PropCheck.asBs;
import static tools.PropCheck.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import listeners.ESBListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.CheckConn;
import tools.MQConn;
import tools.PropCheck;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Main - enter point.
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class Main implements Runnable {

	public static AtomicBoolean flagRequest = new AtomicBoolean(false);

	public static ExecutorService executor = null;

	public static ScheduledExecutorService sc = null;

	private static final Logger logger = LogManager
			.getFormatterLogger(Main.class.getName());

	private static int countThread;

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private boolean flagReconnect = false;

	public Main() {

		try {

			this.factory = MQConn.getFactory();

			this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		} catch (NumberFormatException | JMSException e) {

			logger.error(e.getMessage(), e);
		}

	}

	@Override
	public void run() {

		try {

			this.connection = getConnection(this.factory, null, null);

			PropCheck.connections.add(this.connection);

			this.connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException e) {

					String errorCode = e.getErrorCode();

					logger.error("Error code: %s", errorCode);

					// JMSWMQ1107 - connection closed
					if (errorCode.equalsIgnoreCase("JMSWMQ1107")) {

						logger.error("Error msg: %s", e.getMessage(), e);

						flagReconnect = true;

						flagRequest.set(false);

						logger.info("Connection to MQ closed.");

						long delay = Long.parseLong(common
								.getChildText("delayReconnect")) * 1000;

						while (flagReconnect) {

							try {

								if (CheckConn.checkConn()) {

									run();

								}

							} catch (Exception e1) {

								logger.error("Can't reconecting to MQ: %s",
										e1.getMessage(), e1);
							}

							// Delay between retry
							try {

								long start = System.currentTimeMillis();

								Thread.sleep(delay);

								long stop = System.currentTimeMillis();

								long diff = stop - start;

								logger.info("Time wait: %s", diff);

								// ensure for early wake up thread
								if (diff < delay) {

									logger.info("Wake up early! Wait: %s",
											(delay - diff));

									Thread.sleep(delay - diff);
								}

							} catch (InterruptedException e1) {

								logger.error(e1.getMessage(), e1);
							}

						}
					}
				}
			});

			this.connection.start();

			flagReconnect = false;

			flagRequest.set(true);

			logger.debug(
					"Thread is connected to MQ server with parameters: HostName: %s; Port: %s; QueueManager: %s; Channel: %s",
					this.factory.getHostName(), this.factory.getPort(),
					this.factory.getQueueManager(), this.factory.getChannel());

			String queue = asBs.getChildText("queueFrom");

			MessageConsumer consumer = getConsumer(this.connection, queue);

			consumer.setMessageListener(new ESBListener(this.connection));

			logger.info("Listener is set to queue %s", queue);

		} catch (JMSException e) {

			logger.error("Can't set listener %s", e.getMessage(), e);

			logger.info("Flag reconnect: %s", flagReconnect);

			if (!flagReconnect) {

				executor.shutdownNow();

				sc.shutdownNow();

			}
		}

	}

	/**
	 * @param args
	 * @throws JMSException
	 */
	public static void main(String[] args) throws JMSException {

		sc = Executors.newSingleThreadScheduledExecutor();

		sc.scheduleAtFixedRate(new PropCheck(), 0, 10, TimeUnit.SECONDS);

		countThread = Integer.parseInt(asBs.getChildText("threads"));

		executor = Executors.newFixedThreadPool(countThread);

		for (int i = 0; i < countThread; i++) {

			executor.execute(new Main());

		}

	}

}
