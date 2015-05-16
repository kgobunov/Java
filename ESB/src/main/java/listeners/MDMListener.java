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
import answers.Requests;

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
public class MDMListener implements MessageListener {

	private MQQueueSession session;

	private MQQueue queueSend;

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(MDMListener.class.getName());

	public MDMListener(MQQueueConnection connection) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		try {

			this.queueSend = (MQQueue) this.session.createQueue(Queues.MDM_OUT);

			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

		} catch (JMSException e) {

			logger.error("Can't create queue: %s", e.getMessage(), e);

		}

	}

	public void onMessage(Message inputMsg) {

		MessageProducer producer = null;

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			logger.debug("Message from TSM_MDM: %s", request);

			response = Requests.mdmResponse(request);

			TextMessage outputMsg = this.session.createTextMessage(response);

			producer = this.session.createProducer(this.queueSend);

			producer.send(outputMsg);

			logger.debug("Request to ETSM from MDM: %s", response);

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		} finally {

			try {

				if (null != producer) {

					producer.close();

				}
			} catch (JMSException e) {

				logger.error(e.getMessage(), e);

			}

		}

	}

}
