package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import ru.aplana.app.EsbMqJms;
import ru.aplana.tools.GetData;
import tools.PropsChecker;
import tools.Queues;

import answers.ReqToERIB;

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

	private ArrayList<String> data = null;

	private boolean debug;

	public ETSMListener(MQQueueConnection connection) throws JMSException {

		this.connection = connection;
		
		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		this.debug = EsbMqJms.debug;
	}

	public void onMessage(Message inputMsg) {

		MQQueue queueSend = null;

		try {

			String request = parseMessMQ(inputMsg);

			String response = null;

			if (this.debug) {

				PropsChecker.loggerInfo.info("Message from ETSM: " + request);
			}

			GetData processRq = new GetData(request);

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
				this.data = new ArrayList<String>(8);

				queueSend = (MQQueue) this.session.createQueue(Queues.ERIB_OUT);

				try {

					this.data.add(processRq.getValueByName("RqUID"));

					this.data.add(processRq.getValueByName("RqTm"));

					this.data.add(processRq.getValueByName("SrcObjID"));

					String code = processRq.getValueByName("StatusCode");

					this.data.add(code);

					this.data
							.add(processRq.getValueByName("ApplicationNumber"));

					if (code.equalsIgnoreCase("2")) {

						this.data.add(processRq.getValueByName("PeriodM"));

						this.data.add(processRq.getValueByName("Amount"));

						this.data.add(processRq.getValueByName("InterestRate"));

					} else {

						this.data.add(processRq.getValueByName("ErrorCode"));

						this.data.add(processRq.getValueByName("Message"));

					}

				} catch (Exception e) {

					PropsChecker.loggerSevere
							.severe("[ETSM ServicesListener] Error parcing message: "
									+ e.getMessage());

					e.printStackTrace();
				}

				response = new ReqToERIB(this.data).getRq();

			}

			if (response == null) {

				PropsChecker.loggerInfo.info("Unknown system!");

				queueSend = (MQQueue) this.session
						.createQueue(Queues.GARBAGE_OUT);

				response = request;

			}

			TextMessage outputMsg = this.session.createTextMessage(response);

			queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

			MessageProducer producer = this.session.createProducer(queueSend);

			producer.send(outputMsg);

			producer.close();

			if (this.debug) {

				PropsChecker.loggerInfo.info("Queue: " + queueSend + "; " + "Response to " + system
						+ " from ETSM: " + response);
			}

		} catch (JMSException e) {

			PropsChecker.loggerSevere.severe(e.getMessage());

			e.printStackTrace();
		}

	}

}
