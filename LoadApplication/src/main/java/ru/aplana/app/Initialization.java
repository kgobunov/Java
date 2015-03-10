package ru.aplana.app;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import requests.RequestHelper;
import tools.HaltApplication;
import tools.LoggerImplimentation;
import tools.Validation;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueConnectionFactory;
import com.ibm.mq.jms.MQQueueSession;
import com.ibm.msg.client.jms.JmsConstants;

import connections.Connections;
import connections.DatabaseHelper;
import connections.MQ;
import connections.SaveCorrId;

/**
 * 
 * Classname: Initialization
 * 
 * Version: 1.1
 * 
 * Copyright: OOO Aplana
 * 
 * Init application
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class Initialization {

	public static MQ mqFactory = null;

	public static DatabaseHelper helper = null;

	public static MQQueueConnection connection = null;

	public static MQQueueConnectionFactory factory = null;

	public static Document settings = null;

	public static Element root = null;

	public static LoggerImplimentation loggers = null;

	public static Logger info = null;

	public static Logger severe = null;

	public static ConcurrentHashMap<String, Vector<Timestamp>> systems = null;

	public static ExecutorService saveDb = null;

	public static String saveMode = null;

	public static String testType;

	public static boolean transaction;

	public static ConcurrentHashMap<String, ScheduledExecutorService> executorsSchedule = new ConcurrentHashMap<String, ScheduledExecutorService>();

	public static ConcurrentHashMap<String, ExecutorService> executors = new ConcurrentHashMap<String, ExecutorService>();

	public static ScheduledExecutorService haltApp = null;

	public static void init() throws JMSException {

		loggers = new LoggerImplimentation("logs\\Init", 512000000, 1);

		info = loggers.getInfoLogger();

		severe = loggers.getSevereLogger();

		initSettings(info, severe);

		root = settings.getRootElement();

		if (!Validation.validating()) {

			severe.severe("Config is not valid! See error log! Application stopped!");

			System.exit(0);

		}

		saveDb = Executors.newCachedThreadPool();

		executors.put("DB", saveDb);

		saveMode = root.getChild("common").getChildText("modeSave");

		transaction = Boolean.parseBoolean(root.getChild("common")
				.getChildText("getTransaction"));

		Connections.initConnections();

		helper = new DatabaseHelper();

		mqFactory = new MQ();

		factory = mqFactory.getFactory();

		connection = mqFactory.getConnection();

		connection.start();

		info.info("Connected to MQ successfully!");

		info.info("Factory created with parameters: HostName: "
				+ factory.getHostName() + "; Port: " + factory.getPort()
				+ "; QueueManager: " + factory.getQueueManager()
				+ "; Channel: " + factory.getChannel());

		testType = root.getChild("common").getChildText("testType");

		if (testType.equalsIgnoreCase("none")) {

			String runTime = root.getChild("common").getChildText("runTime");

			haltApp = Executors.newScheduledThreadPool(1);

			haltApp.scheduleAtFixedRate(new HaltApplication(runTime), 0, 10,
					TimeUnit.SECONDS);

		}

	}

	/**
	 * Send request to mq queue
	 * 
	 * @param hash
	 *            with data^ String path String queueRequest String queueReplyTo
	 *            String system MQQueueSession session Logger info Logger severe
	 *            Boolean debug
	 */
	public static void sendRequest(HashMap<String, Object> data) {

		Logger info = (Logger) data.get("infoLog");

		Logger severe = (Logger) data.get("severeLog");

		String system = (String) data.get("system");

		if (null != connection) {

			MessageProducer producer = null;

			try {

				String request = null;

				request = RequestHelper.getRequest((String) data.get("path"));

				MQQueueSession session = (MQQueueSession) data.get("session");

				TextMessage outputMsg = session.createTextMessage(request);

				// Create send queues
				MQQueue queueSendASRequest = (MQQueue) session
						.createQueue((String) data.get("queueRequest"));

				Boolean jmsSupport = (Boolean) data.get("jmsSupport");

				Boolean addionalProp = (Boolean) data.get("addionalProp");

				Boolean debug = (Boolean) data.get("debug");

				if (!jmsSupport) {

					queueSendASRequest
							.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

				}

				if (addionalProp) {

					String rquid = genRquid();

					outputMsg.setStringProperty("RqUID", rquid);

					if (debug) {

						info.info("System name: " + system + "; RQUID: "
								+ rquid);

					}

				}

				String messID = genCorrId();

				outputMsg.setJMSCorrelationIDAsBytes(messID.getBytes());

				queueSendASRequest.setMQMDWriteEnabled(true);

				outputMsg.setObjectProperty(JmsConstants.JMS_IBM_MQMD_MSGID,
						messID.getBytes());

				String queueReplyTo = (String) data.get("queueReplyTo");

				if (queueReplyTo != null) {

					outputMsg.setJMSReplyTo((MQQueue) session
							.createQueue(queueReplyTo));

				}

				producer = session.createProducer(queueSendASRequest);

				producer.send(outputMsg);

				Date now = new Date();

				if (null != system) {

					systems.get(system)
							.addElement(new Timestamp(now.getTime()));

				}

				if (transaction) {

					saveDb.submit(new SaveCorrId(messID, system));

				}

				if (debug) {

					info.info("System: " + system
							+ "; Request to AS send successfully!: " + request);

				}

			} catch (JMSException e) {

				severe.severe("System: " + system + "; Error send request: "
						+ e.getMessage());

				e.printStackTrace();

			} finally {

				if (null != producer) {

					try {

						producer.close();

					} catch (JMSException e) {

						e.printStackTrace();

					}

				}
			}

		}

	}

	/**
	 * 
	 * Generating rquid - additional info about message
	 * 
	 * @return rquid number
	 */
	public static String genRquid() {

		String rquid = "000000";

		Random rand = new Random();

		for (int i = 0; i < 26; i++) {

			rquid += rand.nextInt(10);

		}

		return rquid;
	}

	/**
	 * 
	 * Generating correlationId - additional info about message
	 * 
	 * @return rquid number
	 */
	public static String genCorrId() {

		String corrId = "";

		Random rand = new Random();

		for (int i = 0; i < 8; i++) {

			corrId += rand.nextInt(10);

		}

		corrId += System.currentTimeMillis();

		return corrId;
	}

	/**
	 * Init settings
	 * 
	 * @return
	 */
	private static void initSettings(Logger info, Logger severe) {

		SAXBuilder builder = new SAXBuilder();

		File xmlSettings = new File("Settings.xml");

		try {

			settings = (Document) builder.build(xmlSettings);

			info.info("Settings file " + xmlSettings + " read successfully!");

		} catch (JDOMException e) {

			severe.severe("[JDOM Error] Can't parse " + xmlSettings
					+ "; Error:" + e.getMessage());

			e.printStackTrace();

		} catch (IOException e) {

			severe.severe("[IO Error] Can't read " + xmlSettings + "; Error:"
					+ e.getMessage());

			e.printStackTrace();
		}

	}

}
