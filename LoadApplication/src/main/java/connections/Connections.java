package connections;

import org.jdom.Element;

import ru.aplana.app.Initialization;


/**
 * 
 * Classname: Connections
 * 
 * Version: 1.0
 * 
 * 
 * Connection setting to db (oracle) and mq
 * 
 * Example connection string to DB - jdbc:oracle:thin:/@server:port:sid
 * 
 */
public class Connections {

	// Database
	public static String ORA_HOST;

	public static String ORA_PORT;

	public static String ORA_SID;

	public static String ORA_USER;

	public static String ORA_PASSWORD;

	public static boolean ORA_DEBUG;

	public static String ORA_URL;

	// MQ

	public static String MQ_USER;

	public static String MQ_PASSWORD;

	public static String MQ_HOST;

	public static int MQ_PORT;

	public static String MQ_MANAGER;

	public static String MQ_CHANNEL;

	public static void initConnections() {

		Element connections = Initialization.settings.getRootElement()
				.getChild("connections");

		Element db = connections.getChild("db");

		Element mq = connections.getChild("mq");

		ORA_HOST = db.getChildText("host");

		ORA_PORT = db.getChildText("port");

		ORA_SID = db.getChildText("sid");

		ORA_USER = db.getChildText("user");

		ORA_PASSWORD = db.getChildText("password");

		ORA_DEBUG = Boolean.valueOf(db.getChildText("debug"));

		ORA_URL = "jdbc:oracle:thin://@" + ORA_HOST + ":" + ORA_PORT + ":"
				+ ORA_SID;

		MQ_HOST = mq.getChildText("host");

		MQ_PORT = Integer.valueOf(mq.getChildText("port"));

		MQ_MANAGER = mq.getChildText("manager");

		MQ_CHANNEL = mq.getChildText("channel");

		MQ_USER = mq.getChildText("user");

		MQ_PASSWORD = mq.getChildText("password");

		db = null;

		mq = null;

		connections = null;

	}

}
