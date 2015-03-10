package tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import ru.aplana.app.CRMMqJms;
import ru.aplana.app.Request;
import ru.aplana.tools.CreateLogger;

/**
 * Check properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class PropCheck implements Runnable {

	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	public static boolean debug = false;

	public static Element crm = null;

	public static Element db = null;

	public static Element mq = null;

	public static Element common = null;

	private Document config = null;

	private Element root = null;

	private long stopTime;

	private long startTime = System.currentTimeMillis();

	public PropCheck() {

		// Set logger
		if (null == loggerInfo) {

			CreateLogger loggers = new CreateLogger("CRM", 512000000, 2);

			loggerInfo = loggers.getInfoLogger();

			loggerSevere = loggers.getSevereLogger();

			loggerInfo.info("Start CRM_info log...");

			loggerSevere.info("Start CRM_severe log...");

		}

		if (Validation.validating()) {

			initSettings();

			this.root = this.config.getRootElement();

			crm = this.root.getChild("systems").getChild("crm");

			db = this.root.getChild("connections").getChild("db");

			mq = this.root.getChild("connections").getChild("mq");

			common = this.root.getChild("common");

			this.stopTime = Long.parseLong(crm.getChildText("runTime")) * 60 * 1000;

			debug = Boolean.parseBoolean(crm.getChildText("debug"));

		} else {

			CRMMqJms.sc.shutdownNow();

			loggerSevere
					.severe("Config is not valid! See error log! Application stopped!");

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

		// between now and start time
		long diff = System.currentTimeMillis() - this.startTime;

		if (diff >= this.stopTime) {

			CRMMqJms.flagRequest = false;

			try {
				Thread.sleep(Request.delay);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			CRMMqJms.ex.shutdownNow();

			CRMMqJms.executor.shutdownNow();

			CRMMqJms.sc.shutdownNow();

			loggerInfo.info("Executors stopped!");

			loggerInfo.info("Service stopped! Work time: " + this.stopTime);

		}

	}

}
