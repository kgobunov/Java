package ru.aplana.app;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ru.aplana.tools.CreateLogger;
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

	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	public static boolean debug = false;

	public static Element smtp = null;

	public static Element db = null;

	public static Element mq = null;

	private Document config = null;

	private Element root = null;

	private String mode = null;

	public RunServer() {

		if (Validation.validating()) {

			initSettings();

		} else {

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

		File xmlSettings = new File("Config.xml");

		try {

			config = (Document) builder.build(xmlSettings);

			root = config.getRootElement();

			smtp = root.getChild("systems").getChild("smtp");

			// Set logger
			if (null == loggerInfo) {

				String logName = smtp.getChildText("logName");

				CreateLogger loggers = new CreateLogger(logName,
						Integer.parseInt(smtp.getChildText("logSize")),
						Integer.parseInt(smtp.getChildText("logCount")));

				loggerInfo = loggers.getInfoLogger();

				loggerSevere = loggers.getSevereLogger();

				loggerInfo.info("Start " + logName + "_info log...");

				loggerSevere.info("Start " + logName + "_severe log...");

			}

			db = root.getChild("connections").getChild("db");

			debug = Boolean.parseBoolean(smtp.getChildText("debug"));

			this.mode = smtp.getChildText("mode");

			loggerInfo.info("Config file " + xmlSettings
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

	@Override
	public void run() {

		// Start smtp server
		ExecutorService sc = Executors.newFixedThreadPool(1);

		sc.submit(SimpleSmtpServer.getInstance());

		loggerInfo.info("Server starting successfully!");

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
