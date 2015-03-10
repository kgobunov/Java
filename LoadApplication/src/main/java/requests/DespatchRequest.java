package requests;

import java.util.HashMap;
import java.util.logging.Logger;

import javax.jms.JMSException;

import ru.aplana.app.Initialization;

import com.ibm.mq.jms.MQQueueSession;

/**
 * Sending request
 * 
 * Classname: DespatchRequest
 * 
 * Version: 1.1
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 * 
 */

public class DespatchRequest implements Runnable {

	private final String requestQueue; // request queue

	private final String[] replyTo; // reply queue

	private final String[] file; // request message/s

	private final String sysName; // system name

	private final MQQueueSession session; // mq session

	private final Logger infoLog; // info log

	private final Logger severeLog; // severe log

	private final boolean debug; // debug mode

	private final boolean jmsSupport; // support jms standart

	private final boolean additionalProp; // set addtional jms headers

	private final int length;

	private HashMap<String, Object> dataRequest = new HashMap<String, Object>();

	private DespatchRequest(Builder builder) {

		this.requestQueue = builder.requestQueue;

		this.replyTo = builder.replyTo;

		this.file = builder.file;

		this.sysName = builder.sysName;

		this.infoLog = builder.infoLog;

		this.severeLog = builder.severeLog;

		this.debug = builder.debug;

		this.jmsSupport = builder.jmsSupport;

		this.additionalProp = builder.additionalProp;

		this.length = builder.length;

		this.session = builder.session;

		// put data for request
		this.dataRequest.put("queueRequest", this.requestQueue);

		this.dataRequest.put("system", this.sysName);

		this.dataRequest.put("session", this.session);

		this.dataRequest.put("infoLog", this.infoLog);

		this.dataRequest.put("severeLog", this.severeLog);

		this.dataRequest.put("debug", this.debug);

		this.dataRequest.put("jmsSupport", this.jmsSupport);

		this.dataRequest.put("addionalProp", this.additionalProp);

	}

	public static class Builder {

		private String requestQueue; // request queue

		private String[] replyTo; // reply queue

		private String[] file; // request message/s

		private final String sysName; // system name

		private Logger infoLog;

		private Logger severeLog;

		private final boolean debug;

		private boolean jmsSupport;

		private boolean additionalProp;

		private int length;

		private MQQueueSession session; // mq session

		public Builder(HashMap<String, String> settings, boolean debug) {

			this.sysName = settings.get("systemName");

			this.requestQueue = settings.get("reqQ");

			this.debug = debug;

		}

		public Builder setLoggers(Logger infoLog, Logger severeLog) {

			this.infoLog = infoLog;

			this.severeLog = severeLog;

			return this;

		}

		public Builder setRequestData(String[] replyTo, String[] requestFile) {

			this.replyTo = replyTo;

			this.file = requestFile;

			this.length = this.file.length;

			return this;
		}

		public Builder setMessageOptions(boolean jms, boolean addional) {

			this.jmsSupport = jms;

			this.additionalProp = addional;

			return this;

		}

		public Builder setSession() {

			try {

				this.session = (MQQueueSession) Initialization.connection
						.createQueueSession(false,
								MQQueueSession.AUTO_ACKNOWLEDGE);

			} catch (JMSException e) {

				e.printStackTrace();
			}

			return this;

		}

		public DespatchRequest build() {

			return new DespatchRequest(this);

		}

	}

	@Override
	public void run() {

		if (this.length == 1) {

			this.dataRequest.put("path", this.file[0]);

			this.dataRequest.put("queueReplyTo", this.replyTo[0]);

			Initialization.sendRequest(this.dataRequest);

		} else {

			for (int i = 0; i < this.length; i++) {

				this.dataRequest.put("path", this.file[i]);

				this.dataRequest.put("queueReplyTo", this.replyTo[i]);

				Initialization.sendRequest(this.dataRequest);

			}

		}

	}

}
