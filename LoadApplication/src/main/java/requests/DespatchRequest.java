package requests;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.jms.JMSException;

import ru.aplana.app.Initialization;

import com.ibm.mq.jms.MQQueueSession;

/**
 * 
 * Classname: Maksim Stepanov
 * 
 * Version: 1.1
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 *
 */

public class DespatchRequest implements Runnable {

	private String requestQueue; // request queue

	private String[] replyTo; // reply queue

	private String[] file; // request message/s

	private String sysName; //system name

	private MQQueueSession session; // mq session

	private Logger infoLog;

	private Logger severeLog;

	private boolean debug;
	
	private boolean jmsSupport;
	
	private boolean additionalProp;

	private int length;

	public DespatchRequest(HashMap<String, String> settings, Logger infoLog,
			Logger severeLog, boolean debug, String[] replyTo,
			String[] requestFile, boolean jms, boolean addional) {

		this.requestQueue = settings.get("reqQ");

		this.replyTo = replyTo;

		this.file = requestFile;

		this.sysName = settings.get("systemName");

		this.infoLog = infoLog;

		this.severeLog = severeLog;

		this.debug = debug;
		
		this.jmsSupport = jms;
		
		this.additionalProp = addional;

		this.length = this.file.length;

		try {

			this.session = (MQQueueSession) Initialization.connection
					.createQueueSession(false, MQQueueSession.AUTO_ACKNOWLEDGE);

		} catch (JMSException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void run() {

		if (this.length == 1) {

			Initialization.sendRequest(this.file[0], this.requestQueue,
					this.replyTo[0], this.sysName, this.session, this.infoLog,
					this.severeLog, this.debug, this.jmsSupport, this.additionalProp);

		} else {

			for (int i = 0; i < this.length; i++) {

				Initialization.sendRequest(this.file[i], this.requestQueue,
						this.replyTo[i], this.sysName, this.session,
						this.infoLog, this.severeLog, this.debug, this.jmsSupport, this.additionalProp);

			}

		}

	}

}
