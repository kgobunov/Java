package ru.aplana.tools;

import java.util.HashMap;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;

import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;

/**
 * 
 * Common operations with websphere MQ - Create factory, session, connection,
 * producer, consumer.
 * 
 * @author Makism Stepanov
 * 
 */
public class MQTools {

	private MQTools() {
	}

	/**
	 * Creating factory for properties
	 * 
	 * @param properties
	 *            - hash with minimum properties: ip address, port, manage,
	 *            channel
	 * @author Maksim Stepanov
	 */
	public static MQQueueConnectionFactory getFactory(
			HashMap<String, String> properties) {

		HashMap<String, String> hashProperties = new HashMap<String, String>();

		hashProperties = properties;

		MQQueueConnectionFactory factory = new MQQueueConnectionFactory();

		factory.setHostName(hashProperties.get("ip"));

		try {

			factory.setPort(Integer.parseInt(hashProperties.get("port")));

			factory.setQueueManager(hashProperties.get("manager"));

			factory.setChannel(hashProperties.get("channel"));

		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (JMSException e) {

			e.printStackTrace();

		}

		return factory;

	}
	
	
	/**
	 * Creating mq session
	 * 
	 * @param connection
	 *            - connect to MQ
	 * @param transacted
	 *            - transaction mode (true/false)
	 * @param acknowledgemode
	 * @return session
	 * @author Maksim Stepanov
	 */
	public static MQQueueSession getSession(MQQueueConnection connection,
			Boolean transacted, int acknowledgemode) {

		MQQueueSession session = null;

		try {

			session = (MQQueueSession) connection.createQueueSession(
					transacted, acknowledgemode);

		} catch (JMSException e) {

			e.printStackTrace();
		}

		return session;

	}

	/**
	 * Creating connect to MQ
	 * 
	 * @param factory
	 * @return connection to MQ for factory
	 * @author Maksim Stepanov
	 */
	public static MQQueueConnection getConnection(
			MQQueueConnectionFactory factory, String userId, String password) {

		MQQueueConnection connection = null;

		try {

			if (userId != null && password != null) {

				connection = (MQQueueConnection) factory
						.createQueueConnection(userId, password);

			} else {
				connection = (MQQueueConnection) factory
						.createQueueConnection();
			}

		} catch (JMSException e) {

			e.printStackTrace();
		}

		return connection;
	}

	/**
	 * 
	 * Creating consumer for queue
	 * 
	 * @param connection
	 *            - connection to MQ
	 * @param queue
	 *            - consumer queue
	 * @return - consumer
	 * @author Maksim Stepanov
	 */
	public static MessageConsumer getConsumer(MQQueueConnection connection,
			String queue) {

		MQQueueSession session = null;

		MessageConsumer consumer = null;

		try {

			session = (MQQueueSession) connection.createQueueSession(false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			MQQueue queueConsumer = (MQQueue) session.createQueue(queue);

			consumer = session.createConsumer(queueConsumer);

		} catch (JMSException e) {

			e.printStackTrace();

		}

		return consumer;
	}

	/**
	 * Creating producer for queue
	 * 
	 * @param connection
	 *            - connect to MQ
	 * @param queue
	 * @return producer
	 * @author Maksim Stepanov
	 */
	public static MessageProducer getProducer(MQQueueConnection connection,
			String queue) {

		MQQueueSession session = null;

		MessageProducer producer = null;

		try {
			session = (MQQueueSession) connection.createQueueSession(false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			MQQueue queueProducer = (MQQueue) session.createQueue(queue);

			producer = session.createProducer(queueProducer);

		} catch (JMSException e) {

			e.printStackTrace();
		}

		return producer;
	}

}
