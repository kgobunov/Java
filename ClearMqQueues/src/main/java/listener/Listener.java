package listener;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueSession;

public class Listener implements MessageListener {

	private MQQueueSession session;

	private MQQueue queueSend;

	private MQQueueConnection connection;

	public Listener(MQQueueConnection connection) throws JMSException {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		this.queueSend = (MQQueue) this.session.createQueue("APLANA.TEST");

	}

	@Override
	public void onMessage(Message inputMsg) {

		try {

			String request = parseMessMQ(inputMsg);

			TextMessage outputMsg = this.session.createTextMessage(request);

			MessageProducer producer = this.session
					.createProducer(this.queueSend);

			producer.send(outputMsg);

			producer.close();

		} catch (JMSException e) {

			e.printStackTrace();
		}

	}

}
