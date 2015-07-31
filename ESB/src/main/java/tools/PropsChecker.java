package tools;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.jms.JMSException;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import com.ibm.mq.jms.MQQueueConnection;

import ru.aplana.app.Main;
import db.DataBaseHelper;

/**
 * Read config file and check shutdown time
 * 
 * @author Maksim Stepanov
 * 
 */
public class PropsChecker implements Runnable {

	public static AtomicLong callsCountEsopss = new AtomicLong(0);

	public static AtomicLong callsCountMdm = new AtomicLong(0);

	public static AtomicLong callsCountFms = new AtomicLong(0);

	public static AtomicLong callsCountSpoobk = new AtomicLong(0);

	private ArrayList<String> systems = new ArrayList<String>(4);

	public static boolean flagListener = false;

	public static ArrayList<MQQueueConnection> connections = new ArrayList<MQQueueConnection>();

	private static final Logger logger = LogManager
			.getFormatterLogger(PropsChecker.class.getName());

	private static Vector<String> urlArray;

	public static MultiThreadedHttpConnectionManager connManager;

	private static AtomicInteger callCounter = new AtomicInteger(0);

	private ScheduledExecutorService checkCalls = null;

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

	public static HashMap<String, Integer> urlLeast = null;

	public PropsChecker() {

		this.systems.add("ESOPSS");

		this.systems.add("MDM");

		this.systems.add("FMS");

		this.systems.add("SPOOBK");

		this.startTime = System.currentTimeMillis();

		this.configFile = new File("Config.xml");

		this.previosModification = this.configFile.lastModified();

		connManager = new MultiThreadedHttpConnectionManager();

		urlArray = new Vector<String>(20);

		if (Validation.validating()) {

			readProps();

			if (null == this.checkCalls) {

				this.checkCalls = Executors.newScheduledThreadPool(this.systems
						.size());

				for (String system : this.systems) {

					DataBaseHelper.getInstance().saveCountCalls(system);

					this.checkCalls.scheduleAtFixedRate(
							new CallsChecker(system), 0, 60, TimeUnit.SECONDS);

				}

			}

			// to millisecond
			this.timeStop = Long.parseLong(esb.getChildText("runTime")) * 60 * 1000;

		} else {

			Main.sc.shutdownNow();

			logger.error("Config is not valid! See error log! Application stopped!");

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

			logger.info("Config file %s read successfully!", xmlSettings);

		} catch (JDOMException | IOException e) {

			logger.error("Can't parse %s; Error: %s", xmlSettings,
					e.getMessage(), e);

		}

	}

	public void run() {

		// between now and start time
		long diff = System.currentTimeMillis() - this.startTime;

		if (diff >= this.timeStop && this.timeStop > 0) {

			logger.info("Service stopped! Work time: %s", this.timeStop);

			DataBaseHelper.getInstance().disconnect();

			Enumeration<Driver> drivers = DriverManager.getDrivers();

			while (drivers.hasMoreElements()) {

				Driver driver = (Driver) drivers.nextElement();

				logger.info("Deregister driver: " + driver.getClass().getName());

				try {

					DriverManager.deregisterDriver(driver);

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			}
			for (MQQueueConnection connection : connections) {

				try {

					String clientId = connection.getClientID();

					connection.clear();

					connection.close();

					logger.info("Connection %s closed successfully", clientId);

				} catch (JMSException e) {

					logger.error(e.getMessage(), e);

				}

			}

			this.checkCalls.shutdownNow();

			Main.sc.shutdownNow();

			Main.executor.shutdownNow();

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

			logger.info("ESB config file was modified! Reload properties...");

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

		case "least":

			logger.info("Mode: %s", mode);

			urlLeast = new HashMap<String, Integer>(osgiSize);

			for (int i = 0; i < osgiSize; i++) {

				String url = ((Element) osgi.get(i)).getChildText("link");

				urlLeast.put(url, 0);

				logger.info("ESB Properties add osgi url: " + url);

			}

			size = urlArray.size();

			break;

		case "round":

			logger.info("Mode: %s", mode);

			for (int i = 0; i < osgiSize; i++) {

				String url = ((Element) osgi.get(i)).getChildText("link");

				urlArray.addElement(url);

				logger.info("ESB Properties add osgi url: " + url);

			}

			size = urlArray.size();

			break;

		case "bind":

			logger.info("Mode: %s", mode);

			urlBind = new HashMap<String, Integer>(osgiSize);

			for (int i = 0; i < osgiSize; i++) {

				urlBind.put(((Element) osgi.get(i)).getChildText("link"),
						Integer.valueOf(((Element) osgi.get(i))
								.getChildText("countListener")));

			}
			break;

		default:
			break;
		}

		logger.info("ESB Properties loaded successfully!");

	}

	public static synchronized String getUrlRoundRobin() {

		String url = urlArray
				.get(Math.abs(callCounter.getAndIncrement() % size));

		return url;
	}

	public static synchronized String getUrlLeastConnection() {

		Entry<String, Integer> min = Collections.min(urlLeast.entrySet(),
				new Comparator<Entry<String, Integer>>() {

					@Override
					public int compare(Entry<String, Integer> entryFirst,
							Entry<String, Integer> entrySecond) {

						return entryFirst.getValue().compareTo(
								entrySecond.getValue());
					}
				});

		String url = min.getKey();

		int count = min.getValue();

		logger.debug("Url: %s; min counter: %s", url, count);

		urlLeast.put(url, ++count);

		return url;
	}
}
