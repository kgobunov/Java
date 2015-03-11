package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getConsumer;
import static tools.PropCheck.common;
import static tools.PropCheck.debug;
import static tools.PropCheck.erib;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
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
 * Main - enter point.
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class SBOLMqJms implements Runnable {

	public static volatile boolean flagRequest = false;

	public static volatile long startTime;

	public static ExecutorService executor = null;

	public static ExecutorService ex = null;

	public static ScheduledExecutorService sc = null;

	private static int countThread;

	private static AtomicInteger countThreadStart = new AtomicInteger(0);

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private boolean flagReconnect = false;

	public SBOLMqJms() throws JMSException {

		this.factory = MQConn.getFactory();

		this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

	}

	@Override
	public void run() {

		try {

			this.connection = getConnection(this.factory);

			this.connection.setExceptionListener(new ExceptionListener() {

				@Override
				public void onException(JMSException e) {

					String errorCode = e.getErrorCode();

					loggerSevere.severe("Error code: " + errorCode);

					// JMSWMQ1107 - connection closed
					if (errorCode.equalsIgnoreCase("JMSWMQ1107")) {

						loggerSevere.severe("Error trace: " + e.getMessage());

						flagReconnect = true;

						flagRequest = false;

						loggerInfo.info("Connection to MQ closed.");

						long delay = Long.parseLong(common
								.getChildText("delayReconnect")) * 1000;

						while (flagReconnect) {

							try {

								Lock lock = new ReentrantLock();

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
										.severe("Cann't reconecting to MQ: "
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

			flagRequest = true;

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

			String queue = erib.getChildText("queueFrom");

			MessageConsumer consumerERIB = getConsumer(this.connection, queue);

			consumerERIB.setMessageListener(new ESBListener());

			loggerInfo.info("Listener is set to queue " + queue);

			if (!flagReconnect) {

				countThreadStart.getAndIncrement();

				// send request to esb
				try {

					ex.submit(new Request());

				} catch (JMSException e1) {

					loggerSevere.severe("Error start thread for request: "
							+ e1.getMessage());

					e1.printStackTrace();
				}

				loggerInfo.info("CountThreadStart: " + countThreadStart);

				if (countThreadStart.get() > 250) {

					Runtime r = Runtime.getRuntime();

					r.gc();

					countThreadStart.set(0);

				}
			}

		} catch (JMSException e) {

			loggerSevere.severe("Error: Can't set listener " + e.getMessage());

			e.printStackTrace();

			loggerSevere.severe("Flag reconnect: " + flagReconnect);

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

		if (common.getChildText("testType").equalsIgnoreCase("step")) {

			startTime = System.currentTimeMillis();

		}

		countThread = Integer.parseInt(erib.getChildText("threads"));

		executor = Executors.newFixedThreadPool(countThread);

		ex = Executors.newCachedThreadPool();

		for (int i = 0; i < countThread; i++) {

			executor.submit(new SBOLMqJms());

		}

	}

}