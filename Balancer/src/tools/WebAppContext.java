package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

/**
 * Application Lifecycle Listener implementation
 * 
 * @author Maksim Stepanov
 */
public class WebAppContext implements ServletContextListener {

	private ScheduledExecutorService propsChecker;

	private static Vector<String> urlArrayKI;

	private static Vector<String> urlArrayUND;

	public static int sizeKI;

	public static int sizeUND;

	private static int countUrls;

	public static boolean debug;

	public static MultiThreadedHttpConnectionManager connManager;

	public static Properties properties = new Properties();

	private static AtomicInteger callCounterKI = new AtomicInteger(0);

	private static AtomicInteger callCounterUND = new AtomicInteger(0);

	// logger's
	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	// logger's
	public static Logger loggerInfoBlack = null;

	public static Logger loggerSevereBlack = null;

	public static AtomicBoolean ready = new AtomicBoolean(false);

	private String properties_fname = System.getProperty("user.dir")
			+ "\\conf\\Balancer.properties";

	private long previosModification;

	private File configFile;

	public class PropsChecker implements Runnable {

		public PropsChecker() {

			readProps(false);

		}

		public void run() {

			checkProps();

		}

		private void checkProps() {

			long lastModification = configFile.lastModified();

			if (lastModification > previosModification) {

				loggerInfo
						.info(">>>Balancer config file was modified! Reload properties...");

				previosModification = lastModification;

				ready.set(false);

				readProps(true);

			}
		}

		private void readProps(boolean loadFlag) {

			try {

				if (loadFlag) {

					loadProperties(configFile);

				}

				synchronized (connManager) {

					connManager.getParams().setMaxTotalConnections(
							Integer.valueOf(properties
									.getProperty("MaxConnections")));

					connManager.getParams().setDefaultMaxConnectionsPerHost(
							Integer.valueOf(properties
									.getProperty("MaxConnections")));

					debug = Boolean.valueOf(properties.getProperty("Debug"));

				}

				Iterator<String> propsIter = properties.stringPropertyNames()
						.iterator();

				String propKey;

				urlArrayKI.clear();

				urlArrayUND.clear();

				while (propsIter.hasNext()) {

					propKey = propsIter.next();

					if (propKey.startsWith("Balancer.UrlKI.")) {

						urlArrayKI.addElement(properties.getProperty(propKey));

						loggerInfo.info(">>>Balancer KI Add url="
								+ properties.getProperty(propKey));
					}

					if (propKey.startsWith("Balancer.UrlUND.")) {

						urlArrayUND.addElement(properties.getProperty(propKey));

						loggerInfo.info(">>>Balancer Und Add url="
								+ properties.getProperty(propKey));
					}

				}

				sizeKI = getSizeKI();

				sizeUND = getSizeUND();

				countUrls = sizeKI + sizeUND;

				ready.set(true);

				loggerInfo.info(">>>All urls loaded successfully!");

				loggerInfo.info(">>>Size KI: " + sizeKI + "; Size Under: "
						+ sizeUND + "; Count urls: " + countUrls);

			} catch (FileNotFoundException e) {

				e.printStackTrace();

			} catch (IOException e) {

				e.printStackTrace();

			}
		}

	}

	/**
	 * Default constructor.
	 */
	public WebAppContext() {

		urlArrayKI = new Vector<String>(110);

		urlArrayUND = new Vector<String>(80);

		configFile = new File(properties_fname);

		previosModification = configFile.lastModified();

		try {

			loadProperties(configFile);

		} catch (IOException e) {

			e.printStackTrace();

		}

		if (null == loggerInfo) {

			CreateLogger loggers = new CreateLogger(
					properties.getProperty("logName"),
					Integer.parseInt(properties.getProperty("sizeLog")) * 1000000,
					Integer.parseInt(properties.getProperty("countFileLog")));

			loggerInfo = loggers.getInfoLogger();

			loggerSevere = loggers.getSevereLogger();

			loggerInfo.info(">>>Balancer log Started!!!!");

		}

		if (null == loggerInfoBlack) {

			CreateLogger loggersBlack = new CreateLogger(
					properties.getProperty("blackLogName"),
					Integer.parseInt(properties.getProperty("blackSizeLog")) * 1000000,
					Integer.parseInt(properties
							.getProperty("blackCountFileLog")));

			loggerInfoBlack = loggersBlack.getInfoLogger();

			loggerSevereBlack = loggersBlack.getSevereLogger();

			loggerInfoBlack.info(">>>Black log Started!!!!");

		}

	}

	/**
	 * 
	 * Loading properties
	 * 
	 * @param configFile
	 * @throws IOException
	 */
	private void loadProperties(File configFile) throws IOException {

		FileReader fr = new FileReader(configFile);

		synchronized (properties) {

			properties.clear();

			properties.load(fr);
		}

		fr.close();

	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		connManager = new MultiThreadedHttpConnectionManager();

		propsChecker = Executors.newSingleThreadScheduledExecutor();

		propsChecker.scheduleAtFixedRate(new PropsChecker(), 0, 10,
				TimeUnit.SECONDS);

	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {

		propsChecker.shutdownNow();

		loggerInfo.info(">>>Balancer Stopped!!!!");
	}

	public static String getNextUrlKI() {

		String url = null;

		if (ready.get()) {

			url = urlArrayKI.get(Math.abs(callCounterKI.getAndIncrement()
					% sizeKI));
		}

		return url;
	}

	public static int getSizeKI() {

		return urlArrayKI.size();
	}

	public static String getNextUrlUND() {

		String url = null;

		if (ready.get()) {

			url = urlArrayUND.get(Math.abs(callCounterUND.getAndIncrement()
					% sizeUND));

		}

		return url;
	}

	public static int getSizeUND() {

		return urlArrayUND.size();
	}

}
