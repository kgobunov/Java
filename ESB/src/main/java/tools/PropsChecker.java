package tools;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ru.aplana.app.EsbMqJms;

import ru.aplana.tools.CreateLogger;
import tools.Validation;

/**
 * Check properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class PropsChecker implements Runnable {

	public static AtomicBoolean debug = new AtomicBoolean();

	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	public static Logger loggerInfoServices = null;

	public static Logger loggerSevereServices = null;

	public static boolean flagListener = false;

	private static Vector<String> urlArray;

	public static MultiThreadedHttpConnectionManager connManager;

	private static AtomicInteger callCounter = new AtomicInteger(0);

	private long previosModification;

	private long timeStop;

	private long startTime;

	private File configFile;

	public static Element esb = null;

	public static Element db = null;

	public static Element mq = null;

	public static Element common = null;

	private Document config = null;

	private Element root = null;

	private static int size = 0;

	public static String mode = "round";

	public static HashMap<String, Integer> urlBind = null;

	public PropsChecker() {

		this.startTime = System.currentTimeMillis();

		this.configFile = new File("Config.xml");

		this.previosModification = this.configFile.lastModified();

		connManager = new MultiThreadedHttpConnectionManager();

		urlArray = new Vector<String>(20);

		if (Validation.validating()) {

			readProps();

			// to millisecond
			this.timeStop = Long.parseLong(esb.getChildText("runTime")) * 60 * 1000;

		} else {

			EsbMqJms.sc.shutdownNow();

			System.err
					.println("Config is not valid! See error log! Application stopped!");

			System.exit(0);
		}

	}

	/**
	 * Init settings
	 * 
	 * @return
	 */
	private void initSettings() {

		SAXBuilder builder = new SAXBuilder();

		File xmlSettings = this.configFile;

		try {

			config = (Document) builder.build(xmlSettings);

			System.out.println("Config file " + xmlSettings
					+ " read successfully!");

		} catch (JDOMException e) {

			System.err.println("[JDOM Error] Can't parse " + xmlSettings
					+ "; Error:" + e.getMessage());

			e.printStackTrace();

		} catch (IOException e) {

			System.err.println("[IO Error] Can't read " + xmlSettings
					+ "; Error:" + e.getMessage());

			e.printStackTrace();
		}

	}

	public void run() {

		// between now and start time
		long diff = System.currentTimeMillis() - this.startTime;

		if (diff >= this.timeStop && this.timeStop > 0) {

			loggerInfo.info("Service stopped! Work time: " + this.timeStop);

			EsbMqJms.sc.shutdownNow();

			EsbMqJms.executor.shutdownNow();

		}

		checkProps();

	}

	/**
	 * check was modified config file
	 * 
	 */
	private void checkProps() {

		long lastModification = this.configFile.lastModified();

		if (lastModification > this.previosModification) {

			loggerInfo
					.info(">>>ESB config file was modified! Reload properties...");

			this.previosModification = lastModification;

			readProps();
		}
	}

	/**
	 * Read config file
	 * 
	 */
	@SuppressWarnings("rawtypes")
	private void readProps() {

		initSettings();

		root = config.getRootElement();

		esb = root.getChild("systems").getChild("esb");

		// Set loggers (info and severe)
		if (null == loggerInfo && null == loggerInfoServices) {

			String logName = esb.getChildText("logName");

			CreateLogger loggers = new CreateLogger(logName,
					Integer.parseInt(esb.getChildText("logSize")),
					Integer.parseInt(esb.getChildText("logCount")));

			CreateLogger loggersServices = new CreateLogger(logName
					+ "_services",
					Integer.parseInt(esb.getChildText("logSize")),
					Integer.parseInt(esb.getChildText("logCount")));

			loggerInfo = loggers.getInfoLogger();

			loggerSevere = loggers.getSevereLogger();

			loggerInfoServices = loggersServices.getInfoLogger();

			loggerSevereServices = loggersServices.getSevereLogger();

			loggerInfo.info("Start " + logName + "_info log...");

			loggerSevere.severe("Start " + logName + "_severe log...");

			loggerInfoServices.info("Start " + logName
					+ "_services_info log...");

			loggerSevereServices.severe("Start " + logName
					+ "_services_severe log...");

			loggerInfo.info(">>>" + logName + " Started!!!!");

		}

		db = root.getChild("connections").getChild("db");

		mq = root.getChild("connections").getChild("mq");

		common = root.getChild("common");

		debug.set(Boolean.parseBoolean(esb.getChildText("debug")));

		mode = common.getChildText("mode");

		synchronized (connManager) {

			connManager.getParams().setMaxTotalConnections(
					Integer.parseInt(esb.getChildText("maxConnections")));

			connManager.getParams().setDefaultMaxConnectionsPerHost(
					Integer.parseInt(esb.getChildText("maxConnections")));
		}

		List osgi = esb.getChild("osgi").getChildren();

		urlArray.clear();

		int osgiSize = osgi.size();

		switch (mode) {
		
		case "round":

			for (int i = 0; i < osgiSize; i++) {

				String url = ((Element) osgi.get(i)).getChildText("link");

				urlArray.addElement(url);

				loggerInfo.info(">>>ESB Properties add osgi url: " + url);

			}

			size = urlArray.size();

			break;

		case "bind":
			
			urlBind = new HashMap<String, Integer>();
			
			for (int i = 0; i < osgiSize; i++) {

				urlBind.put(((Element) osgi.get(i)).getChildText("link"), Integer.valueOf(((Element) osgi.get(i)).getChildText("countListener")));
				
			}
			break;
			
		default:
			break;
		}

		loggerInfo.info(">>>ESB Properties loaded successfully!");

	}

	public static synchronized String getNextUrl() {

		String url;

		url = urlArray.get(Math.abs(callCounter.getAndIncrement() % size));

		return url;
	}

}
