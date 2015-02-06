package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import ru.aplana.app.EsbMqJms;
import tools.PropsChecker;
import tools.Queues;

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
public class FSBListener  implements MessageListener {
	
	private MQQueueSession session;
	
	private MQQueue queueSend;
	
	private MQQueueConnection connection;
	
	private boolean debug;
	
	public FSBListener(MQQueueConnection connection) throws JMSException {
		
		this.connection = connection;
		
		this.session = getSession(this.connection, false, MQQueueSession.AUTO_ACKNOWLEDGE);
		
		this.queueSend = (MQQueue) this.session.createQueue(Queues.ETSM_OUT);
		
		this.debug = EsbMqJms.debug;
		
	}

	public void onMessage(Message inputMsg) {

		try {
			
			String request = parseMessMQ(inputMsg);
			
			String response = null;
			
			if (debug) {
				
				PropsChecker.loggerInfo.info("Message from FSB: " + request);
			}
			
			
			// For this system response equals request
	
			response  = request;
			
			TextMessage outputMsg = this.session.createTextMessage(response);
			
			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);
			
			MessageProducer producer = this.session.createProducer(this.queueSend);
			
			producer.send(outputMsg);
			
			producer.close();
			
			if (debug) {
				
				PropsChecker.loggerInfo.info("Request to ETSM from FSB: " + response);
			}
		
		
		} catch (JMSException e) {
			
			PropsChecker.loggerSevere.severe(e.getMessage());
			
			e.printStackTrace();
		}

	}

}
