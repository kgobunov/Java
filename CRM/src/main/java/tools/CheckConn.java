package tools;

import javax.jms.JMSException;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;

/**
 * Check mq connection
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class CheckConn {

	public static boolean checkConn() {

		boolean status = false;

		MQQueueConnection connection = null;

		try {

			MQQueueConnectionFactory factory = MQConn.getFactory();

			factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

			connection = (MQQueueConnection) factory.createQueueConnection();

			connection.start();

			status = true;

		} catch (NumberFormatException e) {

			e.printStackTrace();

		} catch (JMSException e) {

			e.printStackTrace();

		} finally {

			if (connection != null) {

				try {
					
					connection.close();
					
				} catch (JMSException e) {

					e.printStackTrace();
				}

			}
		}

		return status;
	}

}
