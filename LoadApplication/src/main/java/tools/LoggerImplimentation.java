package tools;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * 
 * Classname: LoggerImplimentation
 * 
 * Versiom: 1.0
 * 
 * Copyright: OO Aplana
 * 
 * Implementation logger
 * 
 * @author Maksim Stepanov
 * 
 */
public class LoggerImplimentation {

	private Logger logger_info = null;

	private Logger logger_severe = null;

	private Handler handler_info = null;

	private Handler handler_severe = null;

	public LoggerImplimentation(String logName, int sizeFile, int maxcountFile) {

		this.logger_info = Logger.getLogger(logName + "_info");
		
		this.logger_severe = Logger.getLogger(logName + "_severe");

		try {
			this.handler_info = new FileHandler(logName + "_info%g.log",
					sizeFile, maxcountFile);

			this.handler_severe = new FileHandler(logName + "_severe%g.log",
					sizeFile, maxcountFile);

		} catch (IOException e) {

			this.handler_info = new ConsoleHandler();

			this.handler_severe = new ConsoleHandler();

		}

		this.logger_info.addHandler(this.handler_info);

		this.logger_severe.addHandler(this.handler_severe);

		this.handler_info.setFormatter(new SimpleFormatter());

		this.handler_severe.setFormatter(new SimpleFormatter());

	}

	/**
	 * Info logger
	 * 
	 * @return info log
	 * @author Maksim Stepanov
	 */
	public Logger getInfoLogger() {

		return this.logger_info;

	}

	/**
	 * Severe logger
	 * 
	 * @return severe log
	 * @author Maksim Stepanov
	 */
	public Logger getSevereLogger() {

		return this.logger_severe;
	}

}
