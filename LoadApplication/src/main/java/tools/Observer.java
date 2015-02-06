package tools;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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

	private HashMap<Integer, String> scenario;

	private ScheduledExecutorService sc;

	private int threads;

	private HashMap<String, String> settingsRequest;

	private Logger infoLog;

	private Logger severeLog;

	private boolean debug;

	private String[] replyTo;

	private String[] files;

	private boolean lastStep = false;

	private boolean jmsSupport;

	private boolean additionalProp;

	public Observer(HashMap<String, String> settingsRequest, Logger infoLog,
			Logger severeLog, boolean debug, String[] settingsArray,
			String[] settingsFutureArray, int currentStep,
			HashMap<Integer, String> scenario, int poolSize, int threads,
			String[] replyTo, String[] files, boolean jms, boolean addional) {

		this.settingsArray = settingsArray;

		this.settingsFutureArray = settingsFutureArray;

		this.startTime = System.currentTimeMillis();

		this.currentStep = currentStep;

		this.scenario = scenario;

		this.threads = threads;

		this.settingsRequest = settingsRequest;

		this.infoLog = infoLog;

		this.severeLog = severeLog;

		this.debug = debug;

		this.jmsSupport = jms;

		this.additionalProp = addional;

		this.sc = Executors.newScheduledThreadPool(poolSize);

		this.replyTo = replyTo;

		this.files = files;

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

				this.sc.scheduleAtFixedRate(new DespatchRequest(
						this.settingsRequest, this.infoLog, this.severeLog,
						this.debug, this.replyTo, this.files, this.jmsSupport,
						this.additionalProp), 0, interval,
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

				Initialization.info.info("Last step finished!");

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
