package tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import requests.DespatchRequest;

import ru.aplana.app.Initialization;

/**
 * 
 * Classname: Observer
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
public class Observer implements Runnable {

	private String[] settingsArray; // current step settings

	private String[] settingsFutureArray; // next step settings

	private long startTime;

	private int currentStep; // current step

	private final HashMap<Integer, String> scenario;

	private final ScheduledExecutorService sc;

	private static AtomicInteger count = new AtomicInteger(0);

	private final int threads;

	private final HashMap<String, String> settingsRequest;

	private final Logger infoLog;

	private final Logger severeLog;

	private final boolean debug;

	private final String[] replyTo;

	private final String[] files;

	private boolean lastStep = false;

	private final boolean jmsSupport;

	private final boolean additionalProp;

	private Observer(Builder builder) {

		this.settingsArray = builder.settingsArray;

		this.settingsFutureArray = builder.settingsFutureArray;

		this.startTime = System.currentTimeMillis();

		this.currentStep = builder.currentStep;

		this.scenario = builder.scenario;

		this.threads = builder.threads;

		this.settingsRequest = builder.settingsRequest;

		this.infoLog = builder.infoLog;

		this.severeLog = builder.severeLog;

		this.debug = builder.debug;

		this.jmsSupport = builder.jmsSupport;

		this.additionalProp = builder.additionalProp;

		this.sc = builder.sc;

		this.replyTo = builder.replyTo;

		this.files = builder.files;

		Initialization.executorsSchedule.put(
				"ObserverMQ_" + count.getAndIncrement(), this.sc);

	}

	public static class Builder {

		private String[] settingsArray; // current step settings

		private String[] settingsFutureArray; // next step settings

		private int currentStep; // current step

		private HashMap<Integer, String> scenario;

		private ScheduledExecutorService sc;

		private int threads;

		private HashMap<String, String> settingsRequest;

		private Logger infoLog;

		private Logger severeLog;

		private final boolean debug;

		private String[] replyTo;

		private String[] files;

		private boolean jmsSupport;

		private boolean additionalProp;

		public Builder(boolean debug) {

			this.debug = debug;

		}

		public Builder setLoggers(Logger infoLog, Logger severeLog) {

			this.infoLog = infoLog;

			this.severeLog = severeLog;

			return this;

		}

		public Builder setSettings(HashMap<String, String> settingsRequest,
				String[] settingsArray, String[] settingsFutureArray) {

			this.settingsRequest = settingsRequest;

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

		public Builder setRunner(int poolSize, int threads) {

			this.threads = threads;

			this.sc = Executors.newScheduledThreadPool(poolSize);

			return this;
		}

		public Builder setRequestData(String[] files) {

			this.files = files;

			return this;

		}

		public Builder setMessageOption(String[] replyTo, boolean jms,
				boolean addional) {

			this.replyTo = replyTo;

			this.jmsSupport = jms;

			this.additionalProp = addional;

			return this;

		}

		public Observer build() {

			return new Observer(this);
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

			float temp = ((float) 3600 / countRequest) * 1000;

			long interval = (long) temp;

			Initialization.info.info("New step: " + this.currentStep);

			Initialization.info.info("new interval: " + interval);

			for (int i = 0; i < this.threads; i++) {

				DespatchRequest dr = new DespatchRequest.Builder(
						this.settingsRequest, this.debug)
						.setLoggers(this.infoLog, this.severeLog)
						.setRequestData(this.replyTo, this.files)
						.setMessageOptions(this.jmsSupport, this.additionalProp)
						.setSession().build();

				this.sc.scheduleAtFixedRate(dr, 0, interval,
						TimeUnit.MILLISECONDS);

				Initialization.info.info("Starting successfully!");

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

				Initialization.info.info("Last step finished!");

				long nowTime = System.currentTimeMillis();

				long diffTime = nowTime - this.startTime;

				long timeLastStep = (Long.parseLong(this.settingsArray[1])) * 60 * 1000;

				if (diffTime > timeLastStep) {

					Initialization.info.info("Load test finished!");

					for (Entry<String, ScheduledExecutorService> sc : Initialization.executorsSchedule
							.entrySet()) {

						Initialization.info
								.info("Stop scheduledExecutorService: "
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

					System.exit(0);

				}

			}

		}

	}

}
