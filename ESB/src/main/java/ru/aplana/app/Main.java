package ru.aplana.app;

import static ru.aplana.tools.MQTools.getConnection;
import static ru.aplana.tools.MQTools.getConsumer;

import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import listeners.ASYNCListener;
import listeners.AsBsListener;
import listeners.CRMListener;
import listeners.ERIBListener;
import listeners.ETSMListener;
import listeners.EsopssListener;
import listeners.EtsmAsBsListener;
import listeners.FSBListener;
import listeners.MDMListener;
import listeners.SAPListener;
import listeners.ServicesListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.CheckConn;
import tools.MQConn;
import tools.PropsChecker;
import tools.Queues;

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
public class Main implements Runnable {

	public static int countThreads;

	public static ExecutorService executor = null;

	public static ScheduledExecutorService sc = null;

	public static AtomicInteger countListeners = new AtomicInteger(0);

	private static int countThreadListeners;

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private boolean flagReconnect = false;

	private static final Logger logger = LogManager
			.getFormatterLogger(Main.class.getName());

	public Main() {

		// Create factory
		try {

			this.factory = MQConn.getFactory();

			this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		} catch (NumberFormatException | JMSException e) {

			logger.error("Can't create factory: %s", e.getMessage(), e);

		}

	}

	public void run() {

		try {

			// Create connection to MQ
			this.connection = getConnection(this.factory, null, null);

			// ServicesListener for catch MQ exceptions
			this.connection.setExceptionListener(new ExceptionListener() {

				public void onException(JMSException e) {

					String errorCode = e.getErrorCode();

					logger.error("Error code: %s", errorCode);

					if (errorCode.equalsIgnoreCase("JMSCC0037")) {

						logger.error("Error msg: %s", e.getMessage(), e);
					}

					// JMSWMQ1107 - connection error
					if (errorCode.equalsIgnoreCase("JMSWMQ1107")) {

						logger.error("Error msg: %s", e.getMessage(), e);

						flagReconnect = true;

						logger.info("Connection to MQ closed");

						long delay = Long.parseLong(PropsChecker.common
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

			logger.debug(
					"Thread is connected to MQ server with parameters: HostName: %s; Port: %s; QueueManager: %s; Channel: %s",
					this.factory.getHostName(), this.factory.getPort(),
					this.factory.getQueueManager(), this.factory.getChannel());

			// Set listeners and consumers
			MessageConsumer consumerERIB = getConsumer(this.connection,
					Queues.ERIB_IN);

			consumerERIB.setMessageListener(new ERIBListener(this.connection));

			MessageConsumer consumerMDM = getConsumer(this.connection,
					Queues.MDM_IN);

			consumerMDM.setMessageListener(new MDMListener(this.connection));

			MessageConsumer consumerEtsmAsBs = getConsumer(this.connection,
					Queues.BS_IN);

			consumerEtsmAsBs.setMessageListener(new EtsmAsBsListener(
					this.connection));

			MessageConsumer consumerAsBs = getConsumer(this.connection,
					Queues.STUB_BS_OUT);

			consumerAsBs.setMessageListener(new AsBsListener(this.connection));

			MessageConsumer consumerEsopss = getConsumer(this.connection,
					Queues.ESOPSS_IN);

			consumerEsopss.setMessageListener(new EsopssListener(
					this.connection));

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

			logger.info("Listener is set to queue %s", Queues.ERIB_IN);

			logger.info("Listener is set to queue %s", Queues.ETSM_IN);

			logger.info("Listener is set to queue %s", Queues.SAP_HR_IN);

			logger.info("Listener is set to queue %s", Queues.ESB_CRM_IN);

			logger.info("Listener is set to queue %s", Queues.ASYNC_IN);

			countListeners.getAndIncrement();

			if (countListeners.get() == countThreads) {

				switch (PropsChecker.mode) {

				case "least":

					countListeners.set(0);

					// count listener for services queue

					while (countListeners.get() < countThreadListeners) {

						MessageConsumer consumerServices = getConsumer(
								this.connection, Queues.SERVICES_IN);

						consumerServices
								.setMessageListener(new ServicesListener(
										this.connection));

						countListeners.getAndIncrement();

					}

					countListeners.set(0);

					logger.info("%s Listener's is set to queue %s",
							countThreadListeners, Queues.SERVICES_IN);

					break;

				case "round":

					countListeners.set(0);

					// count listener for services queue

					while (countListeners.get() < countThreadListeners) {

						MessageConsumer consumerServices = getConsumer(
								this.connection, Queues.SERVICES_IN);

						consumerServices
								.setMessageListener(new ServicesListener(
										this.connection));

						countListeners.getAndIncrement();

					}

					countListeners.set(0);

					logger.info("%s Listener's is set to queue %s",
							countThreadListeners, Queues.SERVICES_IN);

					break;

				case "bind":

					for (Entry<String, Integer> entry : PropsChecker.urlBind
							.entrySet()) {

						String url = entry.getKey();

						int countListeners = entry.getValue();

						for (int i = 0; i < countListeners; i++) {

							MessageConsumer consumerServices = getConsumer(
									this.connection, Queues.SERVICES_IN);

							consumerServices
									.setMessageListener(new ServicesListener(
											this.connection, url));

						}

						logger.info(
								"%s Listener's is set to queue %s for UG: %s",
								countListeners, Queues.SERVICES_IN, url);

					}

					break;

				default:
					break;
				}

			}

		} catch (JMSException e) {

			logger.error("Can't set listener %s", e.getMessage(), e);

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

			executor.execute(new Main());

		}

	}

}
