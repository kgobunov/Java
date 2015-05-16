package tools;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private static final Logger logger = LogManager
			.getFormatterLogger(CheckConn.class.getName());

	public static boolean checkConn() {

		boolean status = false;

		MQQueueConnection connection = null;

		try {

			MQQueueConnectionFactory factory = MQConn.getFactory();

			factory.setTransportType(JMSC.MQJMS_TP_CLIENT_MQ_TCPIP);

			connection = (MQQueueConnection) factory.createQueueConnection();

			connection.start();

			status = true;

		} catch (NumberFormatException | JMSException e) {

			logger.error(e.getMessage(), e);

		} finally {

			if (null != connection) {

				try {

					connection.close();

				} catch (JMSException e) {

					logger.error(e.getMessage(), e);
				}

			}

		}

		return status;
	}
}
