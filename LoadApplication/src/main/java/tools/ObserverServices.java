package tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import pfr.PFR;
import ru.aplana.app.Initialization;

/**
 * 
 * Classname: ObserverServices
 * 
 * Version: 1.0
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

	private final HashMap<Integer, String> scenario;

	private final ScheduledExecutorService sc;

	private static AtomicInteger count = new AtomicInteger(0);

	private int threads;

	private boolean lastStep = false;

	private final long delay;

	private final String url;

	private final int retry;

	private final long delayIteration;

	public ObserverServices(Builder builder) {

		this.settingsArray = builder.settingsArray;

		this.settingsFutureArray = builder.settingsFutureArray;

		this.startTime = System.currentTimeMillis();

		this.currentStep = builder.currentStep;

		this.scenario = builder.scenario;

		this.delayIteration = builder.delayIteration;

		this.threads = builder.threads;

		this.sc = builder.sc;

		this.delay = builder.delay;

		this.url = builder.url;

		this.retry = builder.retry;

		Initialization.executorsSchedule.put(
				"ObserverServices_" + count.getAndIncrement(), this.sc);

	}

	public static class Builder {

		private String[] settingsArray;

		private String[] settingsFutureArray;

		private int currentStep;

		private HashMap<Integer, String> scenario;

		private ScheduledExecutorService sc;

		private int threads;

		private long delay;

		private final String url;

		private final int retry;

		private long delayIteration;

		public Builder(String url, int retryCount, long delayIteration) {

			this.url = url;

			this.retry = retryCount;

			this.delayIteration = delayIteration;

		}

		public Builder setDelay(long delay) {

			this.delay = delay;

			return this;

		}

		public Builder setSettings(String[] settingsArray,
				String[] settingsFutureArray) {

			this.settingsArray = settingsArray;

			this.settingsFutureArray = settingsFutureArray;

			return this;
		}

		public Builder setCurrentStep(int currentStep) {

			this.currentStep = currentStep;

			return this;

		}

		public Builder setScenario(HashMap<Integer, String> scenario) {

			this.scenario = scenario;

			return this;

		}

		public Builder setRunner(int poolSize) {

			int countForStep = Integer.parseInt(this.settingsFutureArray[0]);

			int delaySec = (int) (this.delayIteration / 1000);

			this.threads = (countForStep / (3600 / delaySec)) * poolSize;

			this.sc = Executors.newScheduledThreadPool(this.threads);

			return this;
		}

		public ObserverServices build() {

			return new ObserverServices(this);

		}

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

					for (Entry<String, ExecutorService> sc : Initialization.executors
							.entrySet()) {

						Initialization.info.info("Stop ExecutorService: "
								+ sc.getKey());

						ExecutorService schedule = sc.getValue();

						List<Runnable> listAwait = null;

						if (!schedule.isShutdown()) {

							listAwait = schedule.shutdownNow();

						}

						if (listAwait.size() > 0 && null != listAwait) {

							for (Runnable runnable : listAwait) {

								Initialization.info
										.info("Await executorService: "
												+ runnable.getClass().getName());

							}

						}

					}

					System.exit(0);

				}

			}

		}

	}

}
