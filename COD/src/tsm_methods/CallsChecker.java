package tsm_methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.DataBaseHelper;

/**
 * 
 * Save count calls to database
 * 
 * @author Maksim Stepanov
 * 
 */
public class CallsChecker implements Runnable {

	private static final Logger logger = LogManager
			.getLogger(CallsChecker.class.getName());

	private final String sysName;

	public CallsChecker(String sysName) {

		this.sysName = sysName;

		logger.info("Init calls checker for system: " + this.sysName);

	}


	public void run() {

		DataBaseHelper.getInstance().saveCountCall(this.sysName);

	}
}
