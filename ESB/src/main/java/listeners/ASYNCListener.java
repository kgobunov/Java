package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropsChecker.callsCountFms;
import static tools.PropsChecker.callsCountSpoobk;

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
public class ASYNCListener implements MessageListener {

	private MQQueueSession session;

	private MQQueue queueSend;

	private MQQueueConnection connection;

	private boolean flagError = false;

	private static final Logger logger = LogManager
			.getFormatterLogger(ASYNCListener.class.getName());

	public ASYNCListener(MQQueueConnection connection) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

	}

	public void onMessage(Message inputMsg) {

		ArrayList<String> data = null;

		MessageProducer producer = null;

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			logger.debug("Message from ETSM: %s", request);

			GetData processRq = GetData.getInstance(request);

			String typeRequest = null;

			// Need for detecting response
			int flagSystem = -1;

			String fmsBook = processRq
					.getValueByXpath("//*[local-name()='FMSBookRequest']");

			String fmsResult = processRq
					.getValueByXpath("//*[local-name()='FMSResultRequest']");

			String spoobk = processRq
					.getValueByXpath("//*[local-name()='SrvCardApproveInfoRq']");

			if (fmsBook.length() > 0) {

				typeRequest = "FMSBookRequest".toUpperCase();

				flagSystem = 0;

			}

			if (fmsResult.length() > 0) {

				typeRequest = "FMSResultRequest".toUpperCase();

				flagSystem = 0;
			}

			if (spoobk.length() > 0) {
				typeRequest = "SPOOBK";

				flagSystem = 1;

			}

			// Get data from message

			if (null != typeRequest) {

				switch (typeRequest) {

				case "FMSBOOKREQUEST":
					data = new ArrayList<String>(8);

					try {

						data.add(processRq.getValueByName("MessageId"));

						data.add(processRq.getValueByName("MessageDate"));

						data.add(processRq.getValueByName("FromAbonent"));

						data.add(processRq.getValueByName("ApplicationNumber"));

						data.add(processRq.getValueByName("PassportNumber"));

						data.add(processRq.getValueByName("DocSeries"));

						data.add(processRq.getValueByName("IssueLoc"));

						data.add(processRq.getValueByName("IssDt"));

					} catch (Exception e) {

						logger.error(
								"[FMSBookRequest] Error parcing message: %s",
								e.getMessage(), e);

					}
					break;
				case "FMSRESULTREQUEST":
					data = new ArrayList<String>(3);

					try {

						data.add(processRq.getValueByName("MessageId"));

						data.add(processRq.getValueByName("MessageDate"));

						data.add(processRq.getValueByName("FromAbonent"));

					} catch (Exception e) {

						logger.error(
								"[FMSResultRequest] Error parcing message: %s",
								e.getMessage(), e);

					}

					break;
				case "SPOOBK":
					data = new ArrayList<String>(3);

					try {

						data.add(processRq.getValueByName("MessageId"));

						data.add(processRq.getValueByName("MessageDT"));

						data.add(processRq.getValueByName("FromAbonent"));

					} catch (Exception e) {

						logger.error("[Spoobk] Error parcing message: %s",
								e.getMessage(), e);

					}
					break;
				default:
					break;
				}

			}

			// Create response
			switch (flagSystem) {

			case 0:

				response = Requests.getRequestToTSMFromFMS(data, typeRequest);

				callsCountFms.getAndIncrement();

				break;

			case 1:

				response = Requests.getRequestToTSMFromSPOOBK(data);

				callsCountSpoobk.getAndIncrement();

				break;

			default:
				break;
			}

			this.queueSend = (MQQueue) this.session
					.createQueue(Queues.ETSM_OUT);

			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			if (null == response) {

				logger.error("Can't build response: %s; Request: %s", response,
						request);

				response = request;

				this.queueSend = (MQQueue) this.session
						.createQueue(Queues.SERVICE_GARBAGE_OUT);

				this.flagError = true;

			}

			TextMessage outputMsg = this.session.createTextMessage(response);

			producer = this.session.createProducer(this.queueSend);

			// Send response
			producer.send(outputMsg);

			if (this.flagError) {

				this.queueSend = (MQQueue) this.session
						.createQueue(Queues.ETSM_OUT);

				this.flagError = false;

			}

			if (response.equalsIgnoreCase(request) == false) {

				logger.debug("%s - response to ETSM from : %s", typeRequest,
						response);
			}

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		} finally {

			if (null != producer) {

				try {

					producer.close();

				} catch (JMSException e) {

					logger.error(e.getMessage(), e);

				}

			}
		}

	}
}
