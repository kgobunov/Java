package tools;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
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

	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	public static boolean flagListener = false;

	// public static Properties properties = null;

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

	public PropsChecker() {

		this.startTime = System.currentTimeMillis();

		this.configFile = new File("Config.xml");

		this.previosModification = this.configFile.lastModified();

		connManager = new MultiThreadedHttpConnectionManager();

		urlArray = new Vector<String>(20);

		// Set loggers (info and severe)
		if (null == loggerInfo) {

			CreateLogger loggers = new CreateLogger("ESB", 512000000, 2);

			loggerInfo = loggers.getInfoLogger();

			loggerSevere = loggers.getSevereLogger();

			loggerInfo.info("Start ESB_info log...");

			loggerSevere.info("Start ESB_severe log...");

		}

		loggerInfo.info(">>>ESB Started!!!!");

		if (Validation.validating()) {

			readProps();

			// to millisecond
			this.timeStop = Long.parseLong(esb.getChildText("runTime")) * 60 * 1000;

		} else {

			EsbMqJms.sc.shutdownNow();

			loggerSevere
					.severe("Config is not valid! See error log! Application stopped!");

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

			loggerInfo.info("Config file " + xmlSettings
					+ " read successfully!");

		} catch (JDOMException e) {

			loggerSevere.severe("[JDOM Error] Can't parse " + xmlSettings
					+ "; Error:" + e.getMessage());

			e.printStackTrace();

		} catch (IOException e) {

			loggerSevere.severe("[IO Error] Can't read " + xmlSettings
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

		db = root.getChild("connections").getChild("db");

		mq = root.getChild("connections").getChild("mq");

		common = root.getChild("common");

		synchronized (connManager) {

			connManager.getParams().setMaxTotalConnections(
					Integer.parseInt(esb.getChildText("maxConnections")));

			connManager.getParams().setDefaultMaxConnectionsPerHost(
					Integer.parseInt(esb.getChildText("maxConnections")));
		}

		List osgi = esb.getChild("osgi").getChildren();

		urlArray.clear();

		int osgiSize = osgi.size();

		for (int i = 0; i < osgiSize; i++) {

			String url = ((Element) osgi.get(i)).getChildText("link");
			
			urlArray.addElement(url);
			
			loggerInfo.info(">>>ESB Properties add osgi url: " + url);	

		}

		loggerInfo.info(">>>ESB Properties loaded successfully!");

	}

	public static String getNextUrl() {

		String url;

		url = urlArray.get(Math.abs(callCounter.getAndIncrement()
				% urlArray.size()));

		return url;
	}

}
