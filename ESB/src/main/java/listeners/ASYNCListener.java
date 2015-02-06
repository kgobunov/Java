package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.xpath.XPathExpressionException;

import ru.aplana.app.EsbMqJms;
import ru.aplana.tools.GetData;
import tools.PropsChecker;
import tools.Queues;

import answers.ReqToTSMFromFMS;
import answers.ReqToTSMFromSPOOBK;

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
public class ASYNCListener implements MessageListener {

	private MQQueueSession session;

	private MQQueue queueSend;

	private MQQueueConnection connection;

	private ArrayList<String> data = null;

	private boolean debug;
	
	private boolean flagError = false;

	public ASYNCListener(MQQueueConnection connection) throws JMSException {

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

				PropsChecker.loggerInfo
						.info("Stub[ASYNC] - Message from ETSM: " + request);
			}

			GetData processRq = new GetData(request);

			String type_request = null;

			// Need for detecting response
			int flagSystem = -1;

			// Detecting system
			try {

				String fms_book = processRq
						.getValueByXpath("//*[local-name()='FMSBookRequest']");

				String fms_result = processRq
						.getValueByXpath("//*[local-name()='FMSResultRequest']");

				String spoobk = processRq
						.getValueByXpath("//*[local-name()='SrvCardApproveInfoRq']");

				if (fms_book.length() > 0) {
					
					type_request = "FMSBookRequest";

					flagSystem = 0;

				}

				if (fms_result.length() > 0) {

					type_request = "FMSResultRequest";

					flagSystem = 0;
				}

				if (spoobk.length() > 0) {
					type_request = "SPOOBK";

					flagSystem = 1;

				}

			} catch (XPathExpressionException e1) {

				PropsChecker.loggerSevere
						.severe("[FMS ServicesListener] Error parcing type request: "
								+ e1.getMessage());

				e1.printStackTrace();
			}

			// Get data from message

			if (null != type_request) {

				if (type_request.equalsIgnoreCase("FMSBookRequest")) {

					this.data = new ArrayList<String>(8);

					try {

						this.data.add(processRq.getValueByName("MessageId"));

						this.data.add(processRq.getValueByName("MessageDate"));

						this.data.add(processRq.getValueByName("FromAbonent"));

						this.data.add(processRq.getValueByName("ApplicationNumber"));

						this.data.add(processRq.getValueByName("PassportNumber"));

						this.data.add(processRq.getValueByName("DocSeries"));

						this.data.add(processRq.getValueByName("IssueLoc"));

						this.data.add(processRq.getValueByName("IssDt"));

					} catch (Exception e) {

						PropsChecker.loggerSevere
								.severe("[FMSBookRequest] Error parcing message: "
										+ e.getMessage());

						e.printStackTrace();
					}

				}

				if (type_request.equalsIgnoreCase("FMSResultRequest")) {

					this.data = new ArrayList<String>(3);

					try {

						this.data.add(processRq.getValueByName("MessageId"));

						this.data.add(processRq.getValueByName("MessageDate"));

						this.data.add(processRq.getValueByName("FromAbonent"));

					} catch (Exception e) {

						PropsChecker.loggerSevere
								.severe("[FMSResultRequest] Error parcing message: "
										+ e.getMessage());

						e.printStackTrace();
					}

				}

				if (type_request.equalsIgnoreCase("spoobk")) {

					this.data = new ArrayList<String>(3);

					try {

						this.data.add(processRq.getValueByName("MessageId"));

						this.data.add(processRq.getValueByName("MessageDT"));

						this.data.add(processRq.getValueByName("FromAbonent"));

					} catch (Exception e) {

						PropsChecker.loggerSevere
								.severe("[Spoobk] Error parcing message: "
										+ e.getMessage());

						e.printStackTrace();
					}

				}

			}

			// Create response
			switch (flagSystem) {

			case 0:
				response = new ReqToTSMFromFMS(this.data, type_request)
						.getResp();
				break;

			case 1:
				response = new ReqToTSMFromSPOOBK(this.data).getResp();
				break;

			default:
				break;
			}

			if (null == response) {

				PropsChecker.loggerSevere.severe("Can't build response: "
						+ response + "; Request: " + request);

				response = request;

				this.queueSend = (MQQueue) this.session
						.createQueue(Queues.SERVICE_GARBAGE_OUT);
				
				this.flagError = true;

			}

			TextMessage outputMsg = this.session.createTextMessage(response);

			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			MessageProducer producer = this.session
					.createProducer(this.queueSend);

			// Send response
			producer.send(outputMsg);

			producer.close(); 
			
			if (this.flagError) {
				
				this.queueSend = (MQQueue) this.session.createQueue(Queues.ETSM_OUT);
				
				this.flagError = false;
				
			}

			if (this.debug && (response.equalsIgnoreCase(request) == false)) {

				PropsChecker.loggerInfo.info(type_request
						+ " - response to ETSM from : " + type_request + " "
						+ response);
			}

		} catch (JMSException e) {

			PropsChecker.loggerSevere.severe(e.getMessage());

			e.printStackTrace();
		}

	}

}
