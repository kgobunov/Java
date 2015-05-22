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
public class FSBListener implements MessageListener {

	private MQQueueSession session;

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(FSBListener.class.getName());

	public FSBListener(MQQueueConnection connection) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

	}

	public void onMessage(Message inputMsg) {

		MessageProducer producer = null;

		TextMessage outputMsg = null;

		MQQueue queueSend = null;

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			logger.debug("Message from FSB: %s", request);

			// For this system response equals request
			response = request;

			outputMsg = this.session.createTextMessage(response);

			queueSend = (MQQueue) this.session.createQueue(Queues.ETSM_OUT);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			producer = this.session.createProducer(queueSend);

			producer.send(outputMsg);

			logger.debug("Request to ETSM from FSB: %s", response);

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		} finally {

			try {

				if (null != producer) {

					producer.close();

					outputMsg = null;

					queueSend = null;
				}

			} catch (JMSException e) {

				logger.error(e.getMessage(), e);

			}
		}

	}

}
