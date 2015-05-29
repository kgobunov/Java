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
@SuppressWarnings("deprecation")
public class ERIBListener implements MessageListener {

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(ERIBListener.class.getName());

	public ERIBListener(MQQueueConnection connection) {

		this.connection = connection;

	}

	public void onMessage(Message inputMsg) {

		try {
			Thread.sleep(15000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ArrayList<String> data = null;

		String request = parseMessMQ(inputMsg);

		String response = null;

		logger.debug("Message from ERIB: %s", request);

		GetData processRq = GetData.getInstance(request);

		// Array data for response
		data = new ArrayList<String>(15);

		try {

			data.add(processRq.getValueByName("RqUID"));

			data.add(processRq.getValueByName("RqTm"));

			data.add(processRq.getValueByName("OperUID"));

			data.add(processRq.getValueByName("FromAbonent"));

			data.add(processRq.getValueByName("Code"));

			data.add(processRq.getValueByName("SubProductCode"));

			data.add(processRq.getValueByName("LastName"));

			data.add(processRq.getValueByName("FirstName"));

			data.add(processRq.getValueByName("MiddleName"));

			data.add(processRq.getValueByName("Birthday"));

			data.add(processRq.getValueByName("IssueDt"));

			data.add(processRq.getValueByName("SigningDate"));

			data.add(processRq
					.getValueByXpath("ChargeLoanApplicationRq/Application/Applicant/EmploymentHistory/SBEmployeeFlag"));

			data.add(processRq.getValueByName("Unit"));

			data.add(processRq.getValueByName("Channel"));

			data.add(processRq.getValueByName("CardNum"));

		} catch (Exception e) {

			logger.error("Parcing message failed: %s", e.getMessage(), e);

		}

		response = Requests.getRequestToETSM(data);

		MessageProducer producer = null;

		MQQueueSession session = null;

		MQQueue queueSend = null;

		TextMessage outputMsg = null;

		try {

			session = getSession(this.connection, false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

			queueSend = (MQQueue) session.createQueue(Queues.ETSM_OUT);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			outputMsg = session.createTextMessage(response);

			producer = session.createProducer(queueSend);

			producer.send(outputMsg);

			logger.debug("Request to ETSM from ERIB: %s", response);

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		} finally {

			if (null != producer) {

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

}
