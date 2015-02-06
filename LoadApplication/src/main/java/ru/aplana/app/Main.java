package ru.aplana.app;

import javax.jms.JMSException;

public class Main {

	/**
	 * @param args
	 * @throws JMSException
	 */

	public static void main(String[] args) {

		try {

			// init application
			Initialization.init();

			// Start load testing
			InitSystems.initSystems();

		} catch (JMSException e) {

			System.err.println("Can't start application! Error: "
					+ e.getMessage());

			e.printStackTrace();

			System.err.println("Application stopped!");

			System.exit(0);

		}

	}

}
