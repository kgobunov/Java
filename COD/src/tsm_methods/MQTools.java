package tsm_methods;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

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

	public static final String IP = WebAppContext.properties
			.getProperty("MQ_ip");

	public static final String PORT = WebAppContext.properties
			.getProperty("MQ_port");

	public static final String MANAGER = WebAppContext.properties
			.getProperty("MQ_manager");

	public static final String CHANNEL = WebAppContext.properties
			.getProperty("MQ_channel");

	public static MQQueueConnection connection = null;

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

		factory.setPort(Integer.valueOf(PORT));

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
	public synchronized static final void sendMessage(String message) {

		if (null == connection) {

			try {

				MQTools.setConnection();

				System.out.println("Connected to mq successfully");

			} catch (JMSException e) {

				System.err.println("Can't connect to MQ!");

				e.printStackTrace();
			}
		}

		MessageProducer producer = null;

		MQQueueSession session = null;

		try {

			session = (MQQueueSession) connection.createQueueSession(false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			MQQueue queueSend = (MQQueue) session
					.createQueue(WebAppContext.properties.getProperty("Queue"));

			TextMessage outputMsg = session.createTextMessage(message);

			producer = session.createProducer(queueSend);

			producer.send(outputMsg);

		} catch (JMSException e) {

			System.err.println("JMSException: " + e.getMessage());
			
			e.printStackTrace();

		} finally {

			if (null != producer) {

				try {
					producer.close();

					if (null != session) {

						session.close();
					}

				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	public static final void setConnection() throws JMSException {

		MQQueueConnectionFactory factory = getFactory();

		factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		connection = (MQQueueConnection) factory.createConnection();

		connection.start();
	}

}
