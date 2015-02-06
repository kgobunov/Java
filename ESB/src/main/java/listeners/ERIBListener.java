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

import answers.ReqToETSM;

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
public class ERIBListener  implements MessageListener  {
	
	private MQQueueSession session;
	
	private MQQueue queueSend;
	
	private MQQueueConnection connection;
	
	private ArrayList<String> data = null;
	
	private boolean debug;
	
	public ERIBListener(MQQueueConnection connection) throws JMSException {

		this.connection = connection;
		
		this.session = getSession(this.connection, false, MQQueueSession.AUTO_ACKNOWLEDGE);
		
		this.queueSend = (MQQueue) this.session.createQueue(Queues.ETSM_OUT);
		
		this.debug = EsbMqJms.debug;
		
	}

	public void onMessage(Message inputMsg) {

		try {
			
			String request = parseMessMQ(inputMsg);
			
			String response = null;
			
			if (this.debug) {
				
				PropsChecker.loggerInfo.info("Message from ERIB: " + request);
			}
			
			GetData processRq = new GetData(request); 
			
			// Array data for response 
			this.data = new ArrayList<String>(15);
			
			try {
				
				this.data.add(processRq.getValueByName("RqUID"));
				
				this.data.add(processRq.getValueByName("RqTm"));
				
				this.data.add(processRq.getValueByName("OperUID"));
				
				this.data.add(processRq.getValueByName("FromAbonent"));
				
				this.data.add(processRq.getValueByName("Code"));
				
				this.data.add(processRq.getValueByName("SubProductCode"));
				
				this.data.add(processRq.getValueByName("LastName"));
				
				this.data.add(processRq.getValueByName("FirstName"));
				
				this.data.add(processRq.getValueByName("MiddleName"));
				
				this.data.add(processRq.getValueByName("Birthday"));
				
				this.data.add(processRq.getValueByName("IssueDt"));
				
				this.data.add(processRq.getValueByName("SigningDate"));
				
				this.data.add(processRq.getValueByXpath("ChargeLoanApplicationRq/Application/Applicant/EmploymentHistory/SBEmployeeFlag"));
				
				this.data.add(processRq.getValueByName("Unit"));
				
				this.data.add(processRq.getValueByName("Channel"));
				
	
			} catch (Exception e) {
				
				PropsChecker.loggerSevere.severe("[ERIB ServicesListener] Error parcing message: " + e.getMessage());
			
				e.printStackTrace();
			}
			
			response  = new ReqToETSM(this.data).getRq();
			
			TextMessage outputMsg = this.session.createTextMessage(response);
			
			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
			
			MessageProducer producer = this.session.createProducer(this.queueSend);
			
			producer.send(outputMsg);
			
			producer.close();
			
			if (this.debug) {
				
				PropsChecker.loggerInfo.info("Request to ETSM from ERIB: " + response);
			}
		
		} catch (JMSException e) {
			
			PropsChecker.loggerSevere.severe(e.getMessage());
			
			e.printStackTrace();
		}

	}
	


}
