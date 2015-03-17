package ru.aplana.tools;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * A Logger object is used to log messages for a specific system or application
 * component.
 * 
 * @author Maksim Stepanov
 * 
 */
public class CreateLogger {

	private final Logger loggerInfo;

	private final Logger loggerSevere;

	private final int limit;

	private final int count;

	private final String pattern;

	/**
	 * 
	 * Logger initialization
	 * 
	 * @param pattern
	 *            the pattern for naming the output file. You can set absolute
	 *            path with name
	 * @param limit
	 *            the maximum number of bytes to write to any one file
	 * @param count
	 *            Files the number of files to use
	 */
	public CreateLogger(String pattern, int limit, int count) {

		this.loggerInfo = Logger.getLogger(pattern + "_info");

		this.loggerSevere = Logger.getLogger(pattern + "_severe");

		this.pattern = pattern;

		this.limit = limit;

		this.count = count;

		Handler handlerInfo = null;

		Handler handlerSevere = null;

		try {

			handlerInfo = new FileHandler(this.pattern + "_info%g.log",
					this.limit, this.count);

			handlerSevere = new FileHandler(this.pattern + "_severe%g.log",
					this.limit, this.count);

		} catch (IOException e) {

			handlerInfo = new ConsoleHandler();

			handlerSevere = new ConsoleHandler();

		}

		handlerInfo.setFormatter(new SimpleFormatter());

		handlerSevere.setFormatter(new SimpleFormatter());

		this.loggerInfo.addHandler(handlerInfo);

		this.loggerSevere.addHandler(handlerSevere);

	}

	/**
	 * Get info log
	 * 
	 * @return Info logger
	 * 
	 * @author Maksim Stepanov
	 */
	public Logger getInfoLogger() {

		return this.loggerInfo;

	}

	/**
	 * Get sever log
	 * 
	 * @return Severe log
	 * @author Maksim Stepanov
	 */
	public Logger getSevereLogger() {

		return this.loggerSevere;
	}

	/**
	 * Get size of logs
	 * 
	 * @return the maximum number of bytes to write to any one file
	 */
	public int getSizeLogs() {

		return this.limit;

	}

	/**
	 * Get count of log's files
	 * 
	 * @return Files the number of files to use
	 */
	public int getCountLogs() {

		return this.count;

	}

	/**
	 * Get log's name
	 * 
	 * pattern the pattern for naming the output file
	 * 
	 * @return
	 */
	public String getLogName() {

		return this.pattern;

	}

	@Override
	public String toString() {

		return "Logger information: path to logs - " + this.pattern
				+ ", size logs - " + this.limit + ", number of file to use - "
				+ this.count + "}";

	}
}
