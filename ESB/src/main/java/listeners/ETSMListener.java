package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.aplana.tools.GetData;
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
@SuppressWarnings({ "deprecation" })
public class ETSMListener implements MessageListener {

	private MQQueueSession session;

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(ETSMListener.class.getName());

	public ETSMListener(MQQueueConnection connection) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

	}

	public void onMessage(Message inputMsg) {

		MQQueue queueSend = null;

		ArrayList<String> data = null;

		MessageProducer producer = null;

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			logger.debug("Message from ETSM: %s", request);

			GetData processRq = GetData.getInstance(request);

			String system = processRq.getValueByName("ToAbonent");

			if (system.equalsIgnoreCase("CRM")) {

				response = request;

				queueSend = (MQQueue) this.session.createQueue(Queues.CRM_OUT);

			}

			if (system.equalsIgnoreCase("FSB")) {

				response = request;

				queueSend = (MQQueue) this.session.createQueue(Queues.FSB_OUT);

			}

			if (system.equalsIgnoreCase("SBOL")) {

				// Array data for response
				data = new ArrayList<String>(8);

				queueSend = (MQQueue) this.session.createQueue(Queues.ERIB_OUT);

				try {

					data.add(processRq.getValueByName("RqUID"));

					data.add(processRq.getValueByName("RqTm"));

					data.add(processRq.getValueByName("SrcObjID"));

					String code = processRq.getValueByName("StatusCode");

					data.add(code);

					data.add(processRq.getValueByName("ApplicationNumber"));

					if (code.equalsIgnoreCase("2")) {

						data.add(processRq.getValueByName("PeriodM"));

						data.add(processRq.getValueByName("Amount"));

						data.add(processRq.getValueByName("InterestRate"));

					} else {

						data.add(processRq.getValueByName("ErrorCode"));

						data.add(processRq.getValueByName("Message"));

					}

				} catch (Exception e) {

					logger.error("Parcing message failed: %s", e.getMessage(),
							e);

				}

				response = Requests.getRequestToERIB(data);

			}

			if (response == null) {

				logger.error("Unknown system!");

				queueSend = (MQQueue) this.session
						.createQueue(Queues.GARBAGE_OUT);

				response = request;

			}

			TextMessage outputMsg = this.session.createTextMessage(response);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			producer = this.session.createProducer(queueSend);

			producer.send(outputMsg);

			logger.debug("Queue: %s; Response to %s from ETSM: %s", queueSend,
					system, response);

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
