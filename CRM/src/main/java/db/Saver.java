package db;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classname: Saver
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 * 
 *         Save data to db in async mode
 * 
 */
public class Saver implements Runnable {

	private final String operation;

	private final ArrayList<String> dataArray;

	private static final Logger logger = LogManager
			.getFormatterLogger(Saver.class.getName());

	public Saver(String operation, ArrayList<String> dataArray) {

		this.operation = operation;

		this.dataArray = dataArray;
		
		logger.info("Init Saver!");

	}

	@Override
	public void run() {

		switch (this.operation) {

		case "insert":
			DbOperation.getInstance().evalOperation(1, this.dataArray);
			break;

		}

	}

}
