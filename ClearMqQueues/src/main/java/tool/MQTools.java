package tool;

import javax.jms.JMSException;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Connection setting to MQ and method for create factory
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings({ "deprecation" })
public class MQTools {

	public static final String IP = "10.67.4.25";

	public static final String PORT = "1421";

	public static final String MANAGER = "M00.MOSCOW";

	public static final String CHANNEL = "SC.ESB";

	public static final String USER = "zpr99usr";

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

		factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		return factory;

	}

}
