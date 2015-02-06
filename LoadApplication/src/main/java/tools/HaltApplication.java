package tools;

import ru.aplana.app.Initialization;

/**
 * 
 * Classname: HaltApplication
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 *
 */
public class HaltApplication implements Runnable {

	private long stopTime;

	private long startApp;

	public HaltApplication(String runTime) {

		this.stopTime = Long.parseLong(runTime) * 60 * 1000;

		this.startApp = System.currentTimeMillis();

	}

	@Override
	public void run() {

		long now = System.currentTimeMillis();

		long diff = now - this.startApp;

		if (diff > this.stopTime) {

			Initialization.info.info("Load test stopped! Time test: "
					+ this.stopTime);

			System.exit(0);

		}

	}

}
