package tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ru.aplana.app.Main;
import ru.aplana.tools.CreateLogger;

/**
 * 
 * Check properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class PropCheck implements Runnable {

	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	public static boolean debug = false;

	public static Element ckpit = null;

	public static Element db = null;

	public static Element mq = null;

	public static Element common = null;

	private Document config = null;

	private Element root = null;

	private long stopTime;

	private long startTime = System.currentTimeMillis();

	public PropCheck() {

		if (Validation.validating()) {

			initSettings();

		} else {

			Main.sc.shutdownNow();

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

			this.root = this.config.getRootElement();

			ckpit = this.root.getChild("systems").getChild("ckpit");

			// Set logger
			if (null == loggerInfo) {

				String logName = ckpit.getChildText("logName");

				CreateLogger loggers = new CreateLogger(logName,
						Integer.parseInt(ckpit.getChildText("logSize")),
						Integer.parseInt(ckpit.getChildText("logCount")));

				loggerInfo = loggers.getInfoLogger();

				loggerSevere = loggers.getSevereLogger();

				loggerInfo.info("Start " + logName + "_info log...");

				loggerSevere.info("Start " + logName + "_severe log...");

			}

			db = this.root.getChild("connections").getChild("db");

			mq = this.root.getChild("connections").getChild("mq");

			common = this.root.getChild("common");

			this.stopTime = Long.parseLong(ckpit.getChildText("runTime")) * 60 * 1000;

			debug = Boolean.parseBoolean(ckpit.getChildText("debug"));

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

		// between now and start time
		long diff = System.currentTimeMillis() - this.startTime;

		if (diff >= this.stopTime) {

			loggerInfo.info("Service stopped! Work time: " + this.stopTime);

			Main.exec.shutdownNow();

			Main.sc.shutdownNow();

		}

	}

}
