package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getConsumer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import tools.MQConn;
import tools.PropsChecker;
import tools.Queues;

import listeners.ASYNCListener;
import listeners.CRMListener;
import listeners.ERIBListener;
import listeners.ETSMListener;
import listeners.FSBListener;
import listeners.SAPListener;
import listeners.ServicesListener;


import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Main class - enter point
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class EsbMqJms implements Runnable {

	public static boolean debug;

	public static int countThreads;

	public static ExecutorService executor = null;

	public static ScheduledExecutorService sc = null;

	public static AtomicInteger countListeners = new AtomicInteger(0);

	private static int countThreadListeners;

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private boolean flagReconnect = false;

	public EsbMqJms() throws JMSException {

		// Get value for debug properties
		debug = Boolean.parseBoolean(PropsChecker.esb.getChildText("debug"));

		// Create factory
		this.factory = MQConn.getFactory();

		// specific for tsm retail JMSC.MQJMS_TP_CLIENT_MQ_TCPIP
		this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

	}

	public void run() {

		try {

			// Create connection to MQ
			this.connection = getConnection(this.factory);

			// ServicesListener for catch MQ exceptions
			this.connection.setExceptionListener(new ExceptionListener() {

				public void onException(JMSException e) {

					String errorCode = e.getErrorCode();

					PropsChecker.loggerSevere
							.severe("Error code: " + errorCode);

					if (errorCode.equalsIgnoreCase("JMSCC0037")) {

						PropsChecker.loggerSevere.severe(e.getMessage());
					}

					// JMSWMQ1107 - connection error
					if (errorCode.equalsIgnoreCase("JMSWMQ1107")) {

						PropsChecker.loggerSevere.severe("Error trace: "
								+ e.getMessage());

						flagReconnect = true;

						PropsChecker.loggerInfo.info("Connection to MQ closed");

						long delay = Long.parseLong(PropsChecker.common
								.getChildText("delayReconnect")) * 1000;

						while (flagReconnect) {

							try {

								run();

							} catch (Exception e1) {

								PropsChecker.loggerSevere
										.severe("Cann't reconecting to MQ: "
												+ e1.getMessage());
							}

							// Delay between retry
							try {

								long start = System.currentTimeMillis();

								Thread.sleep(delay);

								long stop = System.currentTimeMillis();

								long diff = stop - start;

								PropsChecker.loggerInfo.info("Time wait: "
										+ diff);

								// ensure for early wake up thread
								if (diff < delay) {

									PropsChecker.loggerInfo
											.info("Wake up early! Wait: "
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

			if (debug) {

				PropsChecker.loggerInfo
						.info("Thread is connected to MQ server with parameters: HostName: "
								+ this.factory.getHostName()
								+ "; Port: "
								+ this.factory.getPort()
								+ "; QueueManager: "
								+ this.factory.getQueueManager()
								+ "; Channel: " + this.factory.getChannel());
			}

			// Set listeners and consumers
			MessageConsumer consumerERIB = getConsumer(this.connection,
					Queues.ERIB_IN);

			consumerERIB.setMessageListener(new ERIBListener(this.connection));

			MessageConsumer consumerETSM = getConsumer(this.connection,
					Queues.ETSM_IN);

			consumerETSM.setMessageListener(new ETSMListener(this.connection));

			MessageConsumer consumerSAP = getConsumer(this.connection,
					Queues.SAP_HR_IN);

			consumerSAP.setMessageListener(new SAPListener(this.connection));

			MessageConsumer consumerCRM = getConsumer(this.connection,
					Queues.ESB_CRM_IN);

			consumerCRM.setMessageListener(new CRMListener(this.connection));
			
			MessageConsumer consumerFSB = getConsumer(this.connection,
					Queues.FSB_IN);

			consumerFSB.setMessageListener(new FSBListener(this.connection));			

			MessageConsumer consumerASYNC = getConsumer(this.connection,
					Queues.ASYNC_IN);

			consumerASYNC
					.setMessageListener(new ASYNCListener(this.connection));

			PropsChecker.loggerInfo.info("Listener is set to queue "
					+ Queues.ERIB_IN);

			PropsChecker.loggerInfo.info("Listener is set to queue "
					+ Queues.ETSM_IN);

			PropsChecker.loggerInfo.info("Listener is set to queue "
					+ Queues.SAP_HR_IN);

			PropsChecker.loggerInfo.info("Listener is set to queue "
					+ Queues.ESB_CRM_IN);

			PropsChecker.loggerInfo.info("Listener is set to queue "
					+ Queues.ASYNC_IN);

			countListeners.getAndIncrement();

			if (countListeners.get() == countThreads) {

				countListeners.set(0);

				// count listener for services queue

				while (countListeners.get() < countThreadListeners) {

					MessageConsumer consumerServices = getConsumer(
							this.connection, Queues.SERVICES_IN);

					consumerServices.setMessageListener(new ServicesListener(
							this.connection));

					countListeners.getAndIncrement();

				}

				countListeners.set(0);

				PropsChecker.loggerInfo.info(countThreadListeners
						+ " Listener's is set to queue " + Queues.SERVICES_IN);

			}

		} catch (JMSException e) {

			PropsChecker.loggerSevere.severe("Error: Can't set listener "
					+ e.getMessage());

			e.printStackTrace();

			if (!flagReconnect) {

				executor.shutdownNow();

				sc.shutdownNow();

			}
		}
	}

	/**
	 * Enter point
	 * 
	 * @param args
	 * @throws JMSException
	 */
	public static void main(String[] args) throws JMSException {

		// Init dynamic properties - must be in same dirictory with .jar

		sc = Executors.newScheduledThreadPool(1);

		sc.scheduleAtFixedRate(new PropsChecker(), 0, 10, TimeUnit.SECONDS);

		// Count threads
		countThreads = Integer.parseInt(PropsChecker.esb
				.getChildText("threads"));

		// Count listeners for services
		countThreadListeners = Integer.parseInt(PropsChecker.esb
				.getChildText("countListenersServices"));

		executor = Executors.newFixedThreadPool(countThreads);

		for (int i = 0; i < countThreads; i++) {

			executor.submit(new EsbMqJms());

		}

	}

}
