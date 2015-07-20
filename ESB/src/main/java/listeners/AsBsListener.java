package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import tools.Queues;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueSession;

/**
 * Implementation listener
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class AsBsListener implements MessageListener {

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(AsBsListener.class.getName());

	public AsBsListener(MQQueueConnection connection) {

		this.connection = connection;

	}

	public void onMessage(Message inputMsg) {

		MessageProducer producer = null;

		MQQueueSession session = null;

		MQQueue queueSend = null;

		TextMessage outputMsg = null;

		try {

			String request = parseMessMQ(inputMsg);

			logger.debug("Message from AS BS to ETSM: %s", request);

			// For this system response equals request
			String response = request;

			session = getSession(this.connection, false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			queueSend = (MQQueue) session.createQueue(Queues.BS_OUT);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			outputMsg = session.createTextMessage(response);

			producer = session.createProducer(queueSend);

			producer.send(outputMsg);

			logger.debug("Request to ETSM from AS BS: %s", response);

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		} finally {

			try {

				if (null != producer) {

					producer.close();

				}

				if (null != session) {

					session.close();

				}

				queueSend = null;

				outputMsg = null;

			} catch (JMSException e) {

				logger.error(e.getMessage(), e);

			}

		}

	}

}
