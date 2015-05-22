package ru.aplana.app;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import smtp.SimpleSmtpServer;
import testing.Test;

/**
 * 
 * Check properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class RunServer implements Runnable {

	public static Element smtp = null;

	public static Element db = null;

	public static Element mq = null;

	public static boolean debug = false;

	private static final Logger logger = LogManager
			.getFormatterLogger(RunServer.class.getName());

	private Document config = null;

	private Element root = null;

	private String mode = null;

	public RunServer() {

		if (Validation.validating()) {

			initSettings();

		} else {

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

			root = config.getRootElement();

			smtp = root.getChild("systems").getChild("smtp");

			db = root.getChild("connections").getChild("db");

			debug = Boolean.parseBoolean(smtp.getChildText("debugDb"));

			this.mode = smtp.getChildText("mode");

			logger.info("Config file %s read successfully!", xmlSettings);

		} catch (JDOMException | IOException e) {

			logger.error("Can't parse %s; Error: %s", xmlSettings,
					e.getMessage());

		}

	}

	@Override
	public void run() {

		// Start smtp server
		ExecutorService sc = Executors.newFixedThreadPool(1);

		sc.submit(SimpleSmtpServer.getInstance());

		logger.info("Server starting successfully!");

		// selftesting
		if (this.mode.equalsIgnoreCase("test")) {

			int countThread = Integer.parseInt(smtp
					.getChildText("countThreadsSmtp"));

			ExecutorService ex = Executors.newFixedThreadPool(countThread);

			for (int i = 0; i < countThread; i++) {

				ex.submit(new Test());

			}

		}

	}

}
