package tools;

import java.io.File;
import java.io.IOException;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;

import javax.jms.JMSException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ru.aplana.app.Main;

import com.ibm.mq.jms.MQQueueConnection;

import db.DatabaseOperation;

/**
 * 
 * Check properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class PropCheck implements Runnable {

	public static ArrayList<MQQueueConnection> connections = new ArrayList<MQQueueConnection>();

	public static Element ckpit = null;

	public static Element db = null;

	public static Element mq = null;

	public static Element common = null;

	private static final Logger logger = LogManager
			.getFormatterLogger(PropCheck.class.getName());

	private Document config = null;

	private Element root = null;

	private long stopTime;

	private long startTime = System.currentTimeMillis();

	public PropCheck() {

		if (Validation.validating()) {

			initSettings();

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

		File xmlSettings = new File("Config.xml");

		try {

			config = (Document) builder.build(xmlSettings);

			this.root = this.config.getRootElement();

			ckpit = this.root.getChild("systems").getChild("ckpit");

			db = this.root.getChild("connections").getChild("db");

			mq = this.root.getChild("connections").getChild("mq");

			common = this.root.getChild("common");

			this.stopTime = Long.parseLong(ckpit.getChildText("runTime")) * 60 * 1000;

			logger.info("Config file %s read successfully!", xmlSettings);

		} catch (JDOMException | IOException e) {

			logger.error("Can't parse %s; Error: %s", xmlSettings,
					e.getMessage());

		}

	}

	@Override
	public void run() {

		// between now and start time
		long diff = System.currentTimeMillis() - this.startTime;

		if (diff >= this.stopTime) {

			logger.info("Service stopped! Work time: %d", this.stopTime);

			DatabaseOperation.getInstance().disconnect();

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

			Main.exec.shutdownNow();

			Main.sc.shutdownNow();

		}

	}

}
