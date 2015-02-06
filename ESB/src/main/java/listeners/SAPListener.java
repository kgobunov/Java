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
import javax.xml.soap.SOAPMessage;

import ru.aplana.app.EsbMqJms;
import ru.aplana.tools.GetData;
import tools.CreateSOAPRq;
import tools.PropsChecker;
import tools.Queues;

import answers.ReqToSAP;

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

	private ArrayList<String> dataRq, dataRs;

	private SOAPMessage soapResponse;

	private String soapResp;

	private boolean debug;

	public SAPListener(MQQueueConnection connection) throws JMSException {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		this.queueSend = (MQQueue) this.session.createQueue(Queues.ETSM_OUT);

		this.debug = EsbMqJms.debug;
	}

	public void onMessage(Message inputMsg) {

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			if (this.debug) {

				PropsChecker.loggerInfo.info("Stub[SAP_HR] - Message from ETSM: "
						+ request);
			}

			GetData processRq = new GetData(request);

			this.dataRq = new ArrayList<String>();

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

				dataRq.add(new String(processRq
						.getValueByName("PassportSeries").getBytes(), "UTF-8"));

				dataRq.add(new String(processRq
						.getValueByName("PassportNumber").getBytes(), "UTF-8"));

				dataRq.add(new String(processRq.getValueByName("IssueDate")
						.getBytes(), "UTF-8"));

				dataRq.add(new String(processRq.getValueByName("MessageId")
						.getBytes(), "UTF-8"));

				dataRq.add(new String(processRq.getValueByName("ns2:TSMId")
						.getBytes(), "UTF-8"));

				dataRq.add(new String(processRq.getValueByName("MessageDT")
						.getBytes(), "UTF-8"));

			} catch (Exception e) {

				PropsChecker.loggerSevere
						.severe("[SAP_HR ServicesListener] Error parsing message: "
								+ e.getMessage());

				e.printStackTrace();

			}

			try {

				// Create SOAP Connection
				SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
						.newInstance();

				SOAPConnection soapConnection = soapConnectionFactory
						.createConnection();

				// Send SOAP Message to SOAP Server
				
				String url = PropsChecker.esb.getChildText("urlSAPHR");

				this.soapResponse = soapConnection.call(new CreateSOAPRq(
						this.dataRq).getRs(), url);

				// Process the SOAP Response
				try {

					this.soapResp = convertSOAPResponse(soapResponse);

				} catch (Exception e) {

					PropsChecker.loggerSevere
							.severe("Error convert soap to string: "
									+ e.getMessage());

				}

				if (this.debug) {

					PropsChecker.loggerInfo.info("Response from service SAP_HR: "
							+ soapResp);
				}

				soapConnection.close();

			} catch (Exception e) {

				PropsChecker.loggerSevere
						.severe("Error occurred while sending SOAP Request to Server: "
								+ e.getMessage());

				e.printStackTrace();
			}

			this.dataRs = new ArrayList<String>();

			GetData processRs = new GetData(soapResp);

			this.dataRs.add(processRs.getValueByName("MessageID"));

			this.dataRs.add(processRs.getValueByName("MessageDate"));

			this.dataRs.add(processRs.getValueByName("MessageNumber"));

			this.dataRs.add(processRs.getValueByName("Code"));

			this.dataRs.add(processRs.getValueByName("Descr"));

			this.dataRs.add(processRs.getValueByName("SystemId"));

			this.dataRs.add(processRs.getValueByName("SberbankEmployerFlag"));

			this.dataRs.add(processRs.getValueByName("ChartNumber"));

			this.dataRs.add(processRs.getValueByName("StartDate"));

			this.dataRs.add(processRs.getValueByName("EndDate"));

			response = new ReqToSAP(this.dataRs).getRq();

			try {

				TextMessage outputMsg = this.session
						.createTextMessage(response);

				this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

				MessageProducer producer = this.session
						.createProducer(this.queueSend);

				producer.send(outputMsg);

				producer.close();

			} catch (JMSException e) {

				PropsChecker.loggerSevere.severe(e.getMessage());
				
				e.printStackTrace();
			}

			if (this.debug) {

				PropsChecker.loggerInfo.info("Response to ETSM from SAP_HR: "
						+ response);
			}

		} catch (Exception e2) {

			PropsChecker.loggerSevere.severe(e2.getMessage());

			e2.printStackTrace();
		}
	}

}
