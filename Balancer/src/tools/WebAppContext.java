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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Application Lifecycle Listener implementation
 * 
 * @author Maksim Stepanov
 */
public class WebAppContext implements ServletContextListener {

	public static final Logger logger = LogManager
			.getLogger(WebAppContext.class.getName());

	private ScheduledExecutorService propsChecker;

	private static Vector<String> urlArrayKI;

	private static Vector<String> urlArrayUND;

	public static int sizeKI;

	public static int sizeUND;

	private static int countUrls;

	public static MultiThreadedHttpConnectionManager connManager;

	public static Properties properties = new Properties();

	private static AtomicInteger callCounterKI = new AtomicInteger(0);

	private static AtomicInteger callCounterUND = new AtomicInteger(0);

	public static AtomicBoolean ready = new AtomicBoolean(false);

	private String propertiesFile = System.getProperty("user.dir")
			+ "\\conf\\Balancer.properties";

	private long previosModification;

	private File configFile;

	private class PropsChecker implements Runnable {

		public PropsChecker() {

			readProps(false);

		}

		public void run() {

			checkProps();

		}

		private void checkProps() {

			long lastModification = configFile.lastModified();

			if (lastModification > previosModification) {

				logger.info(">>>Balancer config file was modified! Reload properties...");

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

						System.out.println(">>>Balancer KI Add url="
								+ properties.getProperty(propKey));
					}

					if (propKey.startsWith("Balancer.UrlUND.")) {

						urlArrayUND.addElement(properties.getProperty(propKey));

						System.out.println(">>>Balancer Und Add url="
								+ properties.getProperty(propKey));
					}

				}

				sizeKI = getSizeKI();

				sizeUND = getSizeUND();

				countUrls = sizeKI + sizeUND;

				ready.set(true);

				System.out.println(">>>All urls loaded successfully!");

				System.out.println(">>>Size KI: " + sizeKI + "; Size Under: "
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

		configFile = new File(propertiesFile);

		previosModification = configFile.lastModified();

		try {

			loadProperties(configFile);

		} catch (IOException e) {

			logger.error("Can't load properties -  " + e.getMessage(), e);

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

		System.out.println(">>>Balancer Stopped!!!!");
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
