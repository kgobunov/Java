package listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MessageConsumer;


import ru.aplana.app.Initialization;

import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;

import connections.Connections;

/**
 * 
 * Classname: Listener
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 *
 */
public class Listener {

	private MQQueueConnectionFactory factory;

	private MQQueueConnection connection;

	private MQQueueSession session;

	private MQQueue queueConsumer;

	private MessageConsumer consumer;

	private boolean debug = false;

	private String queue = null;

	private String sysName = null;

	private String methodParse = null;
	
	private Logger infoLog;
	
	private Logger severeLog;

	private ArrayList<String> xpath = new ArrayList<String>();

	/**
	 * 
	 * @param queue
	 *            for listener
	 * @param sysName
	 *            - name external system
	 * @param xpath
	 *            - checking valid status for response
	 * @param methodParse
	 *            - set validating method
	 * @throws JMSException
	 */
	public Listener(HashMap<String, String> settingsListener,
			ArrayList<String> dataValidating, boolean debug,
			int countListeners, Logger infoLog, Logger severeLog)
			throws JMSException {

		this.factory = Initialization.mqFactory.getFactory();

		this.queue = settingsListener.get("respQ");

		this.sysName = settingsListener.get("systemName");

		this.xpath = dataValidating;

		this.methodParse = settingsListener.get("typeValid");

		this.debug = debug;
		
		this.infoLog = infoLog;
		
		this.severeLog = severeLog;

		try {

			this.connection = (MQQueueConnection) this.factory
					.createQueueConnection(Connections.MQ_USER, "");

			this.connection.start();

			for (int i = 0; i < countListeners; i++) {

				this.session = (MQQueueSession) this.connection
						.createQueueSession(false,
								MQQueueSession.AUTO_ACKNOWLEDGE);

				this.queueConsumer = (MQQueue) this.session
						.createQueue(this.queue);

				this.consumer = this.session.createConsumer(this.queueConsumer);

				this.consumer.setMessageListener(new GettingResponses(this.sysName,
						this.xpath, this.methodParse, this.debug, this.infoLog, this.severeLog));

			}

			if (this.debug) {

				this.infoLog.info("System: " + this.sysName
						+ "; Listener setted to queue " + this.queue
						+ " successfully! " + "Count listeners: "
						+ countListeners);
			}

		} catch (JMSException e) {

			this.severeLog.severe("Error: Can't set listener " + e.getMessage());

			e.printStackTrace();
		}

	}

}
