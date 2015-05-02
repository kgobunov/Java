package connections;

import static connections.Connections.MQ_CHANNEL;
import static connections.Connections.MQ_HOST;
import static connections.Connections.MQ_MANAGER;
import static connections.Connections.MQ_PORT;

import javax.jms.JMSException;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Classname: MQ
 * 
 * Version: 1.0
 * 
 * 
 * MQ settings
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class MQ {

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	public MQ() {

		try {

			initFactory();

		} catch (JMSException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * init mq factory
	 * 
	 * @throws JMSException
	 */
	private void initFactory() throws JMSException {

		this.factory = new MQQueueConnectionFactory();

		this.factory.setHostName(MQ_HOST);

		this.factory.setPort(MQ_PORT);

		this.factory.setQueueManager(MQ_MANAGER);

		this.factory.setChannel(MQ_CHANNEL);

		this.factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

		this.connection = (MQQueueConnection) this.factory
				.createQueueConnection(Connections.MQ_USER, "");

	}

	// get factory
	public MQQueueConnectionFactory getFactory() {

		return this.factory;

	}

	// get connection
	public MQQueueConnection getConnection() {

		return this.connection;

	}

}
