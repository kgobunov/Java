package tools;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import ru.aplana.app.Initialization;

/**
 * 
 * Stop all executors
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

		Initialization.info.info("[HALT] Start application: " + this.startApp);

	}

	@Override
	public void run() {

		long now = System.currentTimeMillis();

		long diff = now - this.startApp;

		if (diff > this.stopTime) {

			Initialization.info.info("Load test stopped! Time test: "
					+ this.stopTime);

			for (Entry<String, ScheduledExecutorService> sc : Initialization.executorsSchedule
					.entrySet()) {

				Initialization.info.info("Stop scheduledExecutorService: "
						+ sc.getKey());

				ScheduledExecutorService schedule = sc.getValue();

				List<Runnable> listAwait = null;

				if (!schedule.isShutdown()) {

					listAwait = schedule.shutdownNow();

				}

				if (listAwait.size() > 0 && null != listAwait) {

					for (Runnable runnable : listAwait) {

						Initialization.info
								.info("Await scheduledExecutorService: "
										+ runnable.getClass().getName());

					}

				}

			}

			for (Entry<String, ExecutorService> sc : Initialization.executors
					.entrySet()) {

				Initialization.info
						.info("Stop ExecutorService: " + sc.getKey());

				ExecutorService schedule = sc.getValue();

				List<Runnable> listAwait = null;

				if (!schedule.isShutdown()) {

					listAwait = schedule.shutdownNow();

				}

				if (listAwait.size() > 0 && null != listAwait) {

					for (Runnable runnable : listAwait) {

						Initialization.info.info("Await executorService: "
								+ runnable.getClass().getName());

					}

				}

			}

			Initialization.haltApp.shutdownNow();

			System.exit(0);

		}

	}

}
