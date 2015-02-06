package tools;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import pfr.PFR;

import ru.aplana.app.Initialization;

/**
 * 
 * Classname: ObserverServices
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * Check next step
 * 
 * @author Maksim Stepanov
 * 
 */
public class ObserverServices implements Runnable {

	private String[] settingsArray;

	private String[] settingsFutureArray;

	private long startTime;

	private int currentStep;

	private HashMap<Integer, String> scenario;

	private ScheduledExecutorService sc;

	private int threads;

	private boolean lastStep = false;

	private long delay;

	private String url;

	private int retry;

	private long delayIteration;

	public ObserverServices(long delayIteration, long delay, String url,
			int retryCount, String[] settingsArray,
			String[] settingsFutureArray, int currentStep,
			HashMap<Integer, String> scenario, int poolSize) {

		this.settingsArray = settingsArray;

		this.settingsFutureArray = settingsFutureArray;

		this.startTime = System.currentTimeMillis();

		this.currentStep = currentStep;

		this.scenario = scenario;

		this.delayIteration = delayIteration;

		int countForStep = Integer.parseInt(this.settingsFutureArray[0]);

		int delaySec = (int) (this.delayIteration / 1000);

		this.threads = (countForStep / (3600 / delaySec)) * poolSize;

		this.sc = Executors.newScheduledThreadPool(this.threads);

		this.delay = delay;

		this.url = url;

		this.retry = retryCount;

	}

	@Override
	public void run() {

		long now = System.currentTimeMillis();

		long diff = now - this.startTime;

		long timeStep = (Long.parseLong(this.settingsArray[1])) * 60 * 1000;

		if (diff > timeStep && (this.currentStep != this.scenario.size())) {

			int countRequest = Integer.parseInt(this.settingsFutureArray[0]);

			this.currentStep++;

			int delaySec = (int) (this.delayIteration / 1000);

			this.threads = countRequest / (3600 / delaySec);

			Initialization.info.info("New step PFR: " + this.currentStep);

			for (int i = 0; i < this.threads; i++) {

				try {

					Thread.sleep(1000);

				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				this.sc.scheduleAtFixedRate(new PFR(this.delay, this.url,
						this.retry), 0, this.delayIteration,
						TimeUnit.MILLISECONDS);

			}

			this.startTime = System.currentTimeMillis();

			String settings = this.scenario.get(this.currentStep);

			String settingsFuture = this.scenario
					.get((this.currentStep + 1 > this.scenario.size()) ? this.scenario
							.size() : this.currentStep + 1);

			Initialization.info.info("Settings: " + settings);

			Initialization.info.info("settingsFuture: " + settingsFuture);

			this.settingsArray = settings.split(",");

			this.settingsFutureArray = settingsFuture.split(",");

			if (!(this.currentStep != this.scenario.size())) {

				Initialization.info.info("Last step succeed!");

				this.lastStep = true;

			}

		} else {

			if (this.lastStep) {

				long nowTime = System.currentTimeMillis();

				long diffTime = nowTime - this.startTime;

				long timeLastStep = (Long.parseLong(this.settingsArray[1])) * 60 * 1000;

				if (diffTime > timeLastStep) {

					Initialization.info.info("Load test finished!");

					System.exit(0);

				}

			}

		}

	}

}
