package listeners;

import static ru.aplana.tools.Common.convertSOAPResponse;
import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.aplana.tools.GetData;
import tools.PropsChecker;
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
public class SAPListener implements MessageListener {

	private MQQueueSession session;

	private MQQueue queueSend;

	private MQQueueConnection connection;

	private static final Logger logger = LogManager
			.getFormatterLogger(SAPListener.class.getName());

	public SAPListener(MQQueueConnection connection) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		try {

			this.queueSend = (MQQueue) this.session
					.createQueue(Queues.ETSM_OUT);

			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

		} catch (JMSException e) {

			logger.error("Can't create queue: %s", e.getMessage(), e);

		}

	}

	public void onMessage(Message inputMsg) {

		ArrayList<String> dataRq = null;

		ArrayList<String> dataRs = null;

		String request = parseMessMQ(inputMsg);

		String response = null;

		logger.debug("Message from ETSM: %s", request);

		GetData processRq = GetData.getInstance(request);

		dataRq = new ArrayList<String>();

		// Data for request to service
		try {

			dataRq.add(new String(processRq.getValueByName("LastName")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("FirstName")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("MiddleName")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("BirthDate")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("PassportSeries")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("PassportNumber")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("IssueDate")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("MessageId")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("ns2:TSMId")
					.getBytes(), "UTF-8"));

			dataRq.add(new String(processRq.getValueByName("MessageDT")
					.getBytes(), "UTF-8"));

		} catch (Exception e) {

			logger.error("Parsing message failed: %s", e.getMessage(), e);

		}

		SOAPConnectionFactory soapConnectionFactory = null;

		SOAPConnection soapConnection = null;

		String soapResp = "";

		try {

			// Create SOAP Connection
			soapConnectionFactory = SOAPConnectionFactory.newInstance();

			soapConnection = soapConnectionFactory.createConnection();

			// Send SOAP Message to SOAP Server

			String url = PropsChecker.esb.getChildText("urlSAPHR");

			SOAPMessage soapResponse = soapConnection.call(
					Requests.getSOAPMessage(dataRq), url);

			// Process the SOAP Response
			try {

				soapResp = convertSOAPResponse(soapResponse);

			} catch (Exception e) {

				logger.error("Can't convert soap to string: %s",
						e.getMessage(), e);

			}

			logger.debug("Response from service SAP_HR: %s", soapResp);

		} catch (Exception e) {

			logger.error(
					"Error occurred while sending SOAP Request to Server: %s",
					e.getMessage(), e);

		} finally {

			if (null != soapConnection) {

				try {

					soapConnection.close();

				} catch (SOAPException e) {

					logger.error("Can't close soap connection: %s",
							e.getMessage(), e);

				}

			}

		}

		dataRs = new ArrayList<String>();

		GetData processRs = GetData.getInstance(soapResp);

		dataRs.add(processRs.getValueByName("MessageID"));

		dataRs.add(processRs.getValueByName("MessageDate"));

		dataRs.add(processRs.getValueByName("MessageNumber"));

		dataRs.add(processRs.getValueByName("Code"));

		dataRs.add(processRs.getValueByName("Descr"));

		dataRs.add(processRs.getValueByName("SystemId"));

		dataRs.add(processRs.getValueByName("SberbankEmployerFlag"));

		dataRs.add(processRs.getValueByName("ChartNumber"));

		dataRs.add(processRs.getValueByName("StartDate"));

		dataRs.add(processRs.getValueByName("EndDate"));

		response = Requests.getRequestToSAP(dataRs);

		MessageProducer producer = null;

		try {

			TextMessage outputMsg = this.session.createTextMessage(response);

			producer = this.session.createProducer(this.queueSend);

			producer.send(outputMsg);

			logger.debug("Response to ETSM from SAP_HR: %s", response);

		} catch (JMSException e) {

			logger.error("Can't send SAP_HR response to ETSM: %s",
					e.getMessage(), e);

		} finally {

			try {

				if (null != producer) {

					producer.close();

				}
			} catch (JMSException e) {

				logger.error("Can't close producer: %s", e.getMessage(), e);

			}
		}

	}
}
