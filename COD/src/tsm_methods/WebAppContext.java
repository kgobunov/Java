package tsm_methods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;

import tools.CreateLogger;

/**
 * Application Lifecycle Listener implementation
 */
public class WebAppContext implements ServletContextListener {

	public static MultiThreadedHttpConnectionManager connManager;

	public static AtomicInteger currentExecutableThreadCounter = new AtomicInteger(
			0);

	public static Properties properties = null;

	public static ExecutorService ex = null;

	public static Logger loggerIncome = null;

	public static Logger loggerOutgo = null;

	public static String sysName = "CS";

	public static long delay;

	public static boolean debug;

	private ExecutorService propsChecker;

	private boolean stopped = false;

	public class PropsChecker implements Runnable {

		private long previosModification;

		private File config_file;

		public PropsChecker() {

			properties = new Properties();

			String properties_fname = System.getProperty("user.dir")
					+ "\\conf\\TSM.properties";

			config_file = new File(properties_fname);

			previosModification = config_file.lastModified();

			readProps();

		}

		public void run() {

			while (!stopped) {
				try {
					Thread.sleep(10000);
				} catch (NumberFormatException e1) {
					e1.printStackTrace();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}

				checkProps();
			}

		}

		private void checkProps() {

			long lastModification = config_file.lastModified();
			if (lastModification > previosModification) {

				System.out
						.println(">>>COD_CARD Stub config file was modified! Reload properties...");
				previosModification = lastModification;

				readProps();
			}
		}

		private void readProps() {
			try {
				FileReader fr = new FileReader(config_file);
				synchronized (properties) {
					properties.clear();
					properties.load(fr);
				}
				fr.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {

				synchronized (connManager) {
					connManager.getParams().setMaxTotalConnections(
							Integer.parseInt(properties
									.getProperty("MaxConnections")));

					connManager.getParams().setDefaultMaxConnectionsPerHost(
							Integer.parseInt(properties
									.getProperty("MaxConnections")));
				}

				delay = Long.parseLong(properties.getProperty("Delay_COD"));

				debug = Boolean.parseBoolean(properties.getProperty("Debug"));

				// Set logger
				if (debug && null == loggerIncome) {

					loggerIncome = new CreateLogger("incoming_COD", 512000000,
							3).getInfoLogger();

					loggerOutgo = new CreateLogger("outgoing_COD", 512000000, 3)
							.getInfoLogger();

					loggerIncome.fine("Start incoming log... "
							+ loggerIncome.getName());

					loggerOutgo.fine("Start outgoing log... "
							+ loggerOutgo.getName());

				}

				System.out
						.println(">>>COD_CARD Stub Properties loaded successfully!");
			}
		}

	}

	public void shutdown() {
		stopped = true;
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

		if (null == ex) {

			ex = Executors.newCachedThreadPool();

		}

		connManager = new MultiThreadedHttpConnectionManager();

		propsChecker = Executors.newFixedThreadPool(1);

		propsChecker.submit(new PropsChecker());

		System.out.println(">>>COD_CARD Stub Started!!!!");
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {

		shutdown();

		propsChecker.shutdownNow();

		ex.shutdownNow();

		System.out.println(">>>COD_CARD Stub Stopped!!!!");
	}

}
