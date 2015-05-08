package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getConsumer;
import static tools.PropCheck.common;
import static tools.PropCheck.crm;
import static tools.PropCheck.debug;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import tools.CheckConn;
import tools.MQConn;
import tools.PropCheck;

import listeners.ESBListener;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Main Class - enter point. This bundle emulation CRM system.
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class CRMMqJms implements Runnable {

	public static AtomicBoolean flagRequest = new AtomicBoolean(false);

	public static AtomicLong startTime = new AtomicLong();

	public static boolean logTsmApp = false;

	public static ScheduledExecutorService sc = null;

	public static ExecutorService executor = null;

	public static ExecutorService ex = null;

	private static AtomicInteger countThreadStart = new AtomicInteger(0);

	private static int countThread;

	private static int countThreadListeners;

	private static AtomicInteger countListeners = new AtomicInteger(0);

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private boolean flagReconnect = false;

	private static Lock lock = new ReentrantLock();

	public CRMMqJms() {

		// Create factory
		try {

			this.factory = MQConn.getFactory();
			this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		} catch (NumberFormatException | JMSException e) {

			e.printStackTrace();
		}

		// Flag for logging status tsm applications
		logTsmApp = Boolean.parseBoolean(crm.getChildText("flagLogTsmApp"));

	}

	@Override
	public void run() {

		try {

			// Create connection to MQ
			this.connection = getConnection(this.factory, null, null);

			// Listener for catch MQ exceptions
			this.connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException e) {

					String errorCode = e.getErrorCode();

					loggerSevere.severe("Error code: " + errorCode);

					// JMSWMQ1107 - connection error
					if (errorCode.equalsIgnoreCase("JMSWMQ1107")) {

						loggerSevere.severe("Error trace: " + e.getMessage());

						flagReconnect = true;

						flagRequest.set(false);

						loggerInfo.info("Connection to MQ closed.");

						long delay = Long.parseLong(common
								.getChildText("delayReconnect")) * 1000;

						while (flagReconnect) {

							try {

								lock.lock();

								try {

									if (CheckConn.checkConn()) {

										run();
									}

								} finally {

									lock.unlock();

								}

							} catch (Exception e1) {

								loggerSevere
										.severe("Can't reconecting to MQ: "
												+ e1.getMessage());
							}

							// Delay between retry
							try {

								long start = System.currentTimeMillis();

								Thread.sleep(delay);

								long stop = System.currentTimeMillis();

								long diff = stop - start;

								loggerInfo.info("Time wait: " + diff);

								// ensure for early wake up thread
								if (diff < delay) {

									loggerInfo.info("Wake up early! Wait: "
											+ (delay - diff));

									Thread.sleep(delay - diff);
								}

							} catch (InterruptedException e1) {

								e1.printStackTrace();
							}

						}
					}
				}
			});

			this.connection.start();

			flagReconnect = false;

			flagRequest.set(true);

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

			String queue = crm.getChildText("queueFrom");

			// Set listeners
			while (countListeners.get() < countThreadListeners) {

				MessageConsumer consumerCRM = getConsumer(this.connection,
						queue);

				consumerCRM.setMessageListener(new ESBListener());

				countListeners.getAndIncrement();

			}

			countListeners.set(0);

			loggerInfo.info("Listener is set to queue " + queue);

			if (!flagReconnect) {

				countThreadStart.getAndIncrement();

				ex.execute(new Request());

				loggerInfo.info("CountThreadStart: " + countThreadStart.get());

				if (countThreadStart.get() > 250) {

					Runtime r = Runtime.getRuntime();

					r.gc();

					countThreadStart.set(0);

				}

			}

		} catch (JMSException e) {

			loggerSevere.severe("Error: Can't set listener " + e.getMessage());

			e.printStackTrace();

			if (!flagReconnect) {

				executor.shutdownNow();

				ex.shutdownNow();

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

		// Detecting type mode - step or confirm
		if (common.getChildText("testType").equalsIgnoreCase("step")) {

			startTime.set(System.currentTimeMillis());

		}

		// Count threads
		countThread = Integer.parseInt(crm.getChildText("threads"));

		// Count listeners
		countThreadListeners = Integer.parseInt(crm.getChildText("listeners"));

		executor = Executors.newFixedThreadPool(countThread);

		ex = Executors.newCachedThreadPool();

		for (int i = 0; i < countThread; i++) {

			executor.execute(new CRMMqJms());

		}

	}

}
