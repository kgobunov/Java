package tsm_methods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map.Entry;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.DataBaseHelper;

/**
 * Application Lifecycle Listener implementation
 */
public class WebAppContext implements ServletContextListener {

	public static final Logger logger = LogManager
			.getLogger(WebAppContext.class.getName());

	public static AtomicLong countCalls = new AtomicLong(0);

	public static MultiThreadedHttpConnectionManager connManager;

	public static AtomicBoolean flagSend = new AtomicBoolean(false);

	public static ConcurrentHashMap<String, String> lostApp = new ConcurrentHashMap<String, String>();

	public static String queue = null;

	public static Properties properties = null;

	public static ThreadPoolExecutor ex = null;

	public static String sysName = "CS";

	public static long delay;

	private ScheduledExecutorService propsChecker;

	private ScheduledExecutorService checkCalls = null;

	private long timeCheckCalls = 0;

	private int poolSize;

	public static ConcurrentLinkedQueue<RequestTSM> lq = null;

	private ExecutorService checkers = null;

	public static AtomicBoolean flagCheck = new AtomicBoolean(true);

	private class PropsChecker implements Runnable {

		private long previosModification;

		private File configFile;

		public PropsChecker() {

			properties = new Properties();

			String properties_fname = System.getProperty("user.dir")
					+ "\\conf\\TSM.properties";

			configFile = new File(properties_fname);

			previosModification = configFile.lastModified();

			readProps();

			timeCheckCalls = Long
					.parseLong(properties.getProperty("timeCheck"));

			delay = Long.parseLong(properties.getProperty("Delay_COD"));

			if (null == queue) {

				queue = properties.getProperty("Queue");

			}

			poolSize = Integer.parseInt(properties.getProperty("PoolSizeCOD"));

			if (null == ex) {

				ex = (ThreadPoolExecutor) Executors
						.newFixedThreadPool(poolSize);

			}

			if (lq == null) {
				lq = new ConcurrentLinkedQueue<RequestTSM>();
			}

			int sizeCheckers = Integer.parseInt(properties
					.getProperty("countCheckerCOD"));

			if (checkers == null) {

				checkers = Executors.newFixedThreadPool(sizeCheckers);

			}

			for (int i = 0; i < sizeCheckers; i++) {

				logger.info("Add checker: " + (i + 1));

				checkers.execute(new ParserStack());

			}

		}

		public void run() {

			checkProps();

			logger.info("Active threads: " + ex.getActiveCount()
					+ "; Queue size: " + ex.getQueue().size()
					+ "; Inside queue size: " + lq.size());

		}

		private void checkProps() {

			if (null == checkCalls) {

				checkCalls = Executors.newSingleThreadScheduledExecutor();

				DataBaseHelper.getInstance().saveCountCall(sysName);

				checkCalls.scheduleAtFixedRate(new CallsChecker(sysName), 0,
						timeCheckCalls, TimeUnit.SECONDS);

			}

			long lastModification = configFile.lastModified();

			if (lastModification > previosModification) {

				logger.info(">>>COD_CARD Stub config file was modified! Reload properties!");

				previosModification = lastModification;

				readProps();
			}

			if (lostApp.size() > 0) {

				for (Entry<String, String> app : lostApp.entrySet()) {

					String key = app.getKey();

					String value = app.getValue();

					boolean send = MQTools.sendMessage(value, key);

					if (send) {

						lostApp.remove(key);

						logger.info("Key: " + key + " remove successfully!");

					}

				}

			}
		}

		private void readProps() {

			FileReader fr = null;

			try {

				fr = new FileReader(configFile);

				synchronized (properties) {
					properties.clear();
					properties.load(fr);
				}

				synchronized (connManager) {
					connManager.getParams().setMaxTotalConnections(
							Integer.parseInt(properties
									.getProperty("MaxConnections")));

					connManager.getParams().setDefaultMaxConnectionsPerHost(
							Integer.parseInt(properties
									.getProperty("MaxConnections")));
				}

				System.out
						.println(">>>COD_CARD Stub Properties loaded successfully!");

			} catch (FileNotFoundException e) {

				logger.error(e.getMessage(), e);

			} catch (IOException e) {

				logger.error(e.getMessage(), e);

			} finally {

				try {

					if (null != fr) {

						fr.close();

					}

				} catch (IOException e) {

					e.printStackTrace();
				}

			}
		}

	}

	/**
	 * Default constructor.
	 */
	public WebAppContext() {

	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {

		connManager = new MultiThreadedHttpConnectionManager();

		propsChecker = Executors.newSingleThreadScheduledExecutor();

		propsChecker.scheduleAtFixedRate(new PropsChecker(), 0, 30,
				TimeUnit.SECONDS);

		System.out.println(">>>COD_CARD Stub Started!!!!");
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {

		propsChecker.shutdownNow();

		DataBaseHelper.getInstance().disconnect();

		Enumeration<Driver> drivers = DriverManager.getDrivers();

		while (drivers.hasMoreElements()) {

			Driver driver = (Driver) drivers.nextElement();

			System.out.println("Deregister driver: "
					+ driver.getClass().getName());

			try {

				DriverManager.deregisterDriver(driver);

			} catch (SQLException e) {

				e.printStackTrace();

			}

		}

		checkCalls.shutdownNow();

		ex.shutdownNow();

		flagCheck.set(false);

		checkers.shutdownNow();

		System.out.println(">>>COD_CARD Stub Stopped!!!!");
	}

}
