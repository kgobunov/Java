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
	
	private String mode= null;

	public RunServer() {

		// Set logger
		if (null == loggerInfo) {

			CreateLogger loggers = new CreateLogger("SMTP", 512000000, 2);

			loggerInfo = loggers.getInfoLogger();

			loggerSevere = loggers.getSevereLogger();

			if (debug) {

				loggerInfo.info("Start SMTP_info log...");

				loggerSevere.info("Start SMTP_severe log...");

			}
		}

		if (Validation.validating()) {

			initSettings();

			root = config.getRootElement();

			smtp = root.getChild("systems").getChild("smtp");

			db = root.getChild("connections").getChild("db");

			debug = Boolean.parseBoolean(smtp.getChildText("debug"));
			
			this.mode = smtp.getChildText("mode");

		} else {
			
			loggerSevere.severe("Config is not valid! See error log! Application stopped!");
			
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

	@Override
	public void run() {

		// Start smtp server
		ExecutorService sc = Executors.newFixedThreadPool(1);
		
		sc.submit(SimpleSmtpServer.getInstance());
		
		loggerInfo.info("Server starting successfully!");
		
		// selftesting
		if (this.mode.equalsIgnoreCase("test")) {
			
			int countThread = Integer.parseInt(smtp.getChildText("countThreadsSmtp"));
	
			ExecutorService ex = Executors.newFixedThreadPool(countThread);
	
			for (int i = 0; i < countThread; i++) {
	
				ex.submit(new Test());
	
			}
		
		}
		
	}

}
