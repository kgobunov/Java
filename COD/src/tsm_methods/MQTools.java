package tsm_methods;

import static tsm_methods.WebAppContext.flagSend;
import static tsm_methods.WebAppContext.lostApp;

import java.util.concurrent.atomic.AtomicLong;

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;

/**
 * 
 * MQ tools
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class MQTools {

	private static final Logger logger = LogManager.getLogger(MQTools.class.getName());

	public static final String IP = WebAppContext.properties
			.getProperty("MQ_ip");

	public static final String PORT = WebAppContext.properties
			.getProperty("MQ_port");

	public static final String MANAGER = WebAppContext.properties
			.getProperty("MQ_manager");

	public static final String CHANNEL = WebAppContext.properties
			.getProperty("MQ_channel");

	public static MQQueueConnection connection = null;

	private static AtomicLong countLostMessage = new AtomicLong(0);

	static {

		setConnection();

	}

	/**
	 * Method creates factory for MQ connection
	 * 
	 * @return factory for connect to MQ
	 * @throws NumberFormatException
	 * @throws JMSException
	 * 
	 */
	private static final MQQueueConnectionFactory getFactory()
			throws NumberFormatException, JMSException {

		MQQueueConnectionFactory factory = new MQQueueConnectionFactory();

		factory.setHostName(IP);

		factory.setPort(Integer.parseInt(PORT));

		factory.setQueueManager(MANAGER);

		factory.setChannel(CHANNEL);

		return factory;

	}

	/**
	 * Method puts message to MQ queue for TSM
	 * 
	 * @param message
	 *            for sending
	 * @throws NumberFormatException
	 * @throws JMSException
	 */
	public static final boolean sendMessage(String message, String tsmId) {

		logger.debug("TSMId: " + tsmId + ". begin sendMessage, length = "
				+ message.length());

		boolean success = false;

		if (flagSend.get()) {

			MQQueueSession session = null;

			MessageProducer producer = null;

			try {

				session = (MQQueueSession) connection.createQueueSession(false,
						MQQueueSession.AUTO_ACKNOWLEDGE);

				MQQueue queueSend = (MQQueue) session
						.createQueue(WebAppContext.queue);

				TextMessage outputMsg = session.createTextMessage(message);

				producer = session.createProducer(queueSend);

				long start = System.currentTimeMillis();

				producer.send(outputMsg);

				long end = System.currentTimeMillis();

				logger.debug("Time send for tmsID: " + tsmId + " "
						+ (end - start) + " ms");

				success = true;

			} catch (Exception e) {

				logger.error(e.getMessage(), e);

			} finally {

				if (producer != null) {

					try {
						producer.close();
					} catch (JMSException e) {

						logger.error(e.getMessage(), e);

					}

				}

				if (session != null) {

					try {
						session.close();
					} catch (JMSException e) {

						logger.error(e.getMessage(), e);

					}
				}

			}

		} else {

			countLostMessage.getAndIncrement();

			logger.error("TSMId: " + tsmId + ". begin sendMessage, length = "
					+ message.length() + "; Message: " + message);

			logger.error("Count lost messages: " + countLostMessage.get());

			lostApp.put(tsmId, message);

		}

		return success;

	}

	public static final void setConnection() {

		if (connection == null || !flagSend.get()) {

			if (null != connection) {

				try {

					connection.close();

					connection = null;

					logger.debug("Connection closed successfully!");

				} catch (JMSException e) {

					logger.error(
							"Can't close connection! Error: " + e.getMessage(),
							e);

				}

			}

			MQQueueConnectionFactory factory = null;

			try {

				factory = getFactory();

				factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

				connection = (MQQueueConnection) factory.createConnection();

				connection.setExceptionListener(new ExceptionListener() {

					public void onException(JMSException e) {

						flagSend.set(false);

						String errorCode = e.getErrorCode();

						logger.error("Error code: " + errorCode
								+ "; Error message: " + e.getMessage(), e);

						setConnection();

					}
				});

				connection.start();

				flagSend.set(true);

			} catch (NumberFormatException e) {

				logger.error(e.getMessage(), e);

			} catch (JMSException e) {

				logger.error("Can't set connection to MQ! " + e.getMessage(), e);
			}

		}

	}

}
