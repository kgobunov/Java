package tools;

import static tools.PropCheck.mq;

import javax.jms.JMSException;

import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Connection setting to MQ and method for create factory
 * 
 * @author Maksim Stepanov
 * 
 */
public class MQConn {

	public static final String IP = mq.getChildText("host");

	public static final String PORT = mq.getChildText("port");

	public static final String MANAGER = mq.getChildText("manager");

	public static final String CHANNEL = mq.getChildText("channel");

	/**
	 * Method creates factory for MQ connection
	 * 
	 * @return factory for connect to MQ
	 * @throws NumberFormatException
	 * @throws JMSException
	 * @author Maksim Stepanov
	 */
	public static final MQQueueConnectionFactory getFactory()
			throws NumberFormatException, JMSException {

		MQQueueConnectionFactory factory = new MQQueueConnectionFactory();

		factory.setHostName(IP);

		factory.setPort(Integer.valueOf(PORT));

		factory.setQueueManager(MANAGER);

		factory.setChannel(CHANNEL);

		return factory;

	}

}
