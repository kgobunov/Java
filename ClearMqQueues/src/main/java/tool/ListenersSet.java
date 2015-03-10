package tool;

import static ru.aplana.tools.MQTools.getConsumer;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;

import ru.aplana.app.Main;

import listener.Listener;

import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;

public class ListenersSet implements Runnable {

	@Override
	public void run() {

		for (String queueName : Main.queuesNames) {

			for (int i = 0; i < 10; i++) {
				
				try {

					MQQueueConnectionFactory factory = MQTools.getFactory();

					MQQueueConnection connection = (MQQueueConnection) factory
							.createQueueConnection(MQTools.USER, "");

					connection.start();

					MessageConsumer consumer = getConsumer(connection,
							queueName);

					consumer.setMessageListener(new Listener(connection));

				} catch (NumberFormatException e) {

					e.printStackTrace();

				} catch (JMSException e) {

					e.printStackTrace();
				}

			}

		}

	}

}
