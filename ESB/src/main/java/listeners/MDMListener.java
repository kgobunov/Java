package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropsChecker.callsCountMdm;

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

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(MDMListener.class.getName());

	public MDMListener(MQQueueConnection connection) {

		this.connection = connection;

	}

	public void onMessage(Message inputMsg) {

		callsCountMdm.getAndIncrement();
		
		MessageProducer producer = null;

		MQQueueSession session = null;

		MQQueue queueSend = null;

		TextMessage outputMsg = null;

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			logger.debug("Message from TSM_MDM: %s", request);

			response = Requests.mdmResponse(request);

			session = getSession(this.connection, false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			queueSend = (MQQueue) session.createQueue(Queues.MDM_OUT);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			outputMsg = session.createTextMessage(response);

			producer = session.createProducer(queueSend);

			producer.send(outputMsg);

			logger.debug("Request to ETSM from MDM: %s", response);

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
