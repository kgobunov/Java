package tools;

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

import ru.aplana.app.FSBMqJms;
import ru.aplana.app.Request;

/**
 * Check properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class PropCheck implements Runnable {

	public static ExecutorService cacheSaverPool = Executors
			.newCachedThreadPool();
	
	public static Element fsb = null;

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

			FSBMqJms.sc.shutdownNow();

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

			fsb = this.root.getChild("systems").getChild("fsb");

			db = this.root.getChild("connections").getChild("db");

			mq = this.root.getChild("connections").getChild("mq");

			common = this.root.getChild("common");

			this.stopTime = Long.parseLong(fsb.getChildText("runTime")) * 60 * 1000;

			logger.info("Config file %s read successfully!", xmlSettings);

		} catch (JDOMException | IOException e) {

			logger.error("Can't parse %s; Error: %s", xmlSettings,
					e.getMessage(), e);

		}

	}

	@Override
	public void run() {

		// between now and start time
		long diff = System.currentTimeMillis() - this.startTime;

		if (diff >= this.stopTime) {

			FSBMqJms.flagRequest.set(false);

			try {

				Thread.sleep(Request.delayForStop.get());

			} catch (InterruptedException e) {

				logger.error(e.getMessage(), e);
			}

			FSBMqJms.ex.shutdownNow();

			FSBMqJms.executor.shutdownNow();

			FSBMqJms.sc.shutdownNow();
			
			cacheSaverPool.shutdownNow();

			logger.info("Executors stopped!");

			logger.info("Service stopped! Work time: %s", this.stopTime);

		}

	}

}
