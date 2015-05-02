package ru.aplana.app;

import static ru.aplana.app.Initialization.root;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.jms.JMSException;

import listeners.Listener;

import org.jdom.Element;

import pfr.PFR;
import requests.DespatchRequest;
import requests.RequestHelper;
import statistics.Statistics;
import tools.LoggerImplimentation;
import tools.Observer;
import tools.ObserverServices;

/**
 * Classname: InitSystems
 * 
 * Version: 1.1
 * 
 * 
 * Initialization systems for load test
 * 
 * @author Maksim Stepanov
 * 
 */
public class InitSystems {

	@SuppressWarnings({ "rawtypes", "unused", "unchecked" })
	public static final void initSystems() throws NumberFormatException,
			JMSException {

		// single system (check for single mode)
		String singleSystem = root.getChild("common").getChild("oneSystem")
				.getChildText("name");

		// flag for saving responses to database
		boolean flagStat = Boolean.parseBoolean(root.getChild("common")
				.getChildText("flagStat"));

		int flush = Integer.parseInt(root.getChild("common").getChildText(
				"partFlush")); // divisor for detecting flush time

		List systems = null; // list of mq systems

		List webServices = null; // list of webservices

		// check mode - single or not
		if (!singleSystem.equalsIgnoreCase("null")) {

			String systemType = root.getChild("common").getChild("oneSystem")
					.getChildText("type");

			if (systemType.equalsIgnoreCase("mq")) {

				List names = root.getChild("systems").getChildren();

				Element searched = null;

				for (int i = 0; i < names.size(); i++) {

					Element el = (Element) names.get(i);

					String findSystemName = el.getChildText("name");

					if (findSystemName.equals(singleSystem)) {

						searched = el;

					}

				}

				systems = new ArrayList(1);

				systems.add(searched);

			} else if (systemType.equalsIgnoreCase("webservice")) {

				List names = root.getChild("webservices").getChildren();

				Element searched = null;

				for (int i = 0; i < names.size(); i++) {

					Element el = (Element) names.get(i);

					String findSystemName = el.getChildText("name");

					if (findSystemName.equals(singleSystem)) {

						searched = el;

					}

				}

				webServices = new ArrayList(1);

				webServices.add(searched);

			}

		} else {

			// Multiple mode
			systems = root.getChild("systems").getChildren("system");

			webServices = root.getChild("webservices")
					.getChildren("webservice");

		}

		if (null != systems) {

			int countSystems = systems.size();

			// for statistics - put messages to queue by systems
			ScheduledExecutorService scMonitor = null;

			// initialization statistic
			if (flagStat) {

				scMonitor = Executors.newScheduledThreadPool(countSystems);

				Initialization.systems = new ConcurrentHashMap<String, Vector<Timestamp>>();

				Initialization.executorsSchedule.put("save_stat", scMonitor);

			}

			// initialization system/s
			for (int i = 0; i < countSystems; i++) {

				Element node = (Element) systems.get(i);

				// system ready to load test
				boolean activeSystem = Boolean.parseBoolean(node
						.getChildText("active"));

				// support MQRFH2 header
				boolean jmsSupport = Boolean.parseBoolean(node
						.getChildText("jms"));

				// add additional information to MQRFH2 header
				boolean addionalProp = Boolean.parseBoolean(node
						.getChildText("usrProperties"));

				if (activeSystem) {

					// validating expressions
					String[] validating = node.getChildText("validation")
							.split(";");

					ArrayList<String> dataValidating = new ArrayList<String>(
							validating.length);

					HashMap<String, String> settingsListener = new HashMap<String, String>();

					HashMap<String, String> settingsRequest = new HashMap<String, String>();

					for (String string : validating) {

						dataValidating.add(string);

					}

					// debug mode
					boolean debug = Boolean.parseBoolean(node
							.getChildText("debug"));

					// system name
					String nameSystem = node.getChildText("name");

					// request per hour
					int countRequest = Integer.parseInt(node
							.getChildText("countRequestByHour"));

					// if flagStat true - saving responses (check every 10
					// seconds)
					if (flagStat) {

						Initialization.systems.put(nameSystem,
								new Vector<Timestamp>());

						scMonitor.scheduleAtFixedRate(new Statistics(
								nameSystem, countRequest, flush), 0, 10,
								TimeUnit.SECONDS);

					}

					// create logger for system
					LoggerImplimentation loggers = new LoggerImplimentation(
							"logs\\" + nameSystem, 512000000, 2);

					Logger info = loggers.getInfoLogger();

					Logger severe = loggers.getSevereLogger();

					// type validation
					String typeValidation = node.getChildText("typeValidating");

					// request queue
					String requestQueue = node.getChildText("requestQueue");

					// response queue
					String responseQueue = node.getChildText("responseQueue");

					// replyTo propertie to jms header
					String[] replyTo = node.getChildText("replyTo").split(";");

					String[] requestFile = node.getChildText("requestFile")
							.split(";"); // request message

					RequestHelper.addRequest(nameSystem, requestFile);

					// count working threads
					int threads = Integer
							.parseInt(node.getChildText("threads"));

					int countListener = Integer.parseInt(node
							.getChildText("countListener")); // count listeners

					settingsListener.put("systemName", nameSystem);

					settingsListener.put("typeValid", typeValidation);

					settingsListener.put("respQ", responseQueue);

					settingsRequest.put("reqQ", requestQueue);

					settingsRequest.put("systemName", nameSystem);

					// set listener for system
					Listener listener = new Listener(settingsListener,
							dataValidating, debug, countListener, info, severe);

					ScheduledExecutorService sc = null;

					if (Initialization.testType.equalsIgnoreCase("step")) {

						HashMap<Integer, String> scenario = new HashMap<Integer, String>(); // scenario

						String[] steps = node.getChildText("step").split(";");

						// size pool equal step count
						int poolThreadSize = steps.length + 1;

						int k = 1;

						for (String step : steps) {

							scenario.put(k, step);

							k++;

						}

						String settings = null;

						String settingsFuture = null;

						String[] settingsArray = null;

						String[] settingsFutureArray = null;

						int currentStep = 1;

						// read first time
						settings = scenario.get(currentStep);

						settingsFuture = scenario
								.get((currentStep + 1 > scenario.size()) ? scenario
										.size() : currentStep + 1);

						settingsArray = settings.split(",");

						settingsFutureArray = settingsFuture.split(",");

						info.info("Settings: " + settings);

						info.info("Future settings: " + settingsFuture);

						float temp = ((float) 3600 / Integer
								.parseInt(settingsArray[0])) * 1000;

						long interval = (long) temp; // intensity

						sc = Executors.newScheduledThreadPool(threads + 1);

						for (int j = 0; j < threads; j++) {

							DespatchRequest dr = new DespatchRequest.Builder(
									settingsRequest, debug)
									.setLoggers(info, severe)
									.setRequestData(replyTo, requestFile)
									.setMessageOptions(jmsSupport, addionalProp)
									.setSession().build();

							sc.scheduleAtFixedRate(dr, 0, interval,
									TimeUnit.MILLISECONDS);

						}

						Observer ob = new Observer.Builder(debug)
								.setCurrentStep(currentStep)
								.setLoggers(info, severe)
								.setRequestData(requestFile)
								.setSettings(settingsRequest, settingsArray,
										settingsFutureArray)
								.setScenario(scenario)
								.setMessageOption(replyTo, jmsSupport,
										addionalProp)
								.setRunner(poolThreadSize, threads).build();

						// check next step
						sc.scheduleAtFixedRate(ob, 0, 10, TimeUnit.SECONDS);

					} else {

						float temp = ((float) 3600 / countRequest) * 1000;

						long interval = (long) temp; // intensity

						sc = Executors.newScheduledThreadPool(threads);

						for (int j = 0; j < threads; j++) {

							DespatchRequest dr = new DespatchRequest.Builder(
									settingsRequest, debug)
									.setLoggers(info, severe)
									.setRequestData(replyTo, requestFile)
									.setMessageOptions(jmsSupport, addionalProp)
									.setSession().build();

							sc.scheduleAtFixedRate(dr, 0, interval,
									TimeUnit.MILLISECONDS);

						}

					}

					Initialization.executorsSchedule.put(nameSystem, sc);

				} else {

					String nameSystem = node.getChildText("name");

					Initialization.info.info(nameSystem
							+ " is not used in load test");
				}

			}

		}

		// run web services

		if (null != webServices) {

			int countServices = webServices.size();

			for (int i = 0; i < countServices; i++) {

				Element webService = (Element) webServices.get(i);

				// webservice ready to load test
				boolean activeService = Boolean.parseBoolean(webService
						.getChildText("active"));

				String nameService = webService.getChildText("name");

				if (activeService) {

					// count request per hour
					int countRequest = Integer.parseInt(webService
							.getChildText("countRequestByHour"));

					int delaySec = Integer.parseInt(webService
							.getChildText("delay"));

					// time iteration in ms
					long delayIteration = delaySec * 1000;

					// calculating count thread for intensity with delay per
					// hour (3600 sec) - intensity / (3600 /delay)
					int threads = countRequest / (3600 / delaySec);

					// webservice url
					String url = webService.getChildText("url");

					long delay = (Long.parseLong(webService
							.getChildText("checkConfirmDate"))) * 1000;

					int retryCount = Integer.parseInt(webService
							.getChildText("countRetryForOpenDate"));

					ScheduledExecutorService sc = null;

					if (Initialization.testType.equalsIgnoreCase("step")) {

						HashMap<Integer, String> scenario = new HashMap<Integer, String>();

						String[] steps = webService.getChildText("step").split(
								";");

						int poolThreadSize = steps.length + 1;

						int k = 1;

						for (String step : steps) {

							scenario.put(k, step);

							k++;

						}

						String settings = null;

						String settingsFuture = null;

						String[] settingsArray = null;

						String[] settingsFutureArray = null;

						int currentStep = 1;

						// read first time
						settings = scenario.get(currentStep);

						settingsFuture = scenario
								.get((currentStep + 1 > scenario.size()) ? scenario
										.size() : currentStep + 1);

						settingsArray = settings.split(",");

						settingsFutureArray = settingsFuture.split(",");

						sc = Executors.newScheduledThreadPool(threads + 1);

						for (int j = 0; j < threads; j++) {

							try {

								Thread.sleep(1000);

							} catch (InterruptedException e) {

								e.printStackTrace();
							}

							sc.scheduleAtFixedRate(new PFR(delay, url,
									retryCount), 0, delayIteration,
									TimeUnit.MILLISECONDS);

						}

						ObserverServices obSc = new ObserverServices.Builder(
								url, retryCount, delayIteration)
								.setCurrentStep(currentStep)
								.setDelay(delay)
								.setScenario(scenario)
								.setSettings(settingsArray, settingsFutureArray)
								.setRunner(poolThreadSize).build();

						sc.scheduleAtFixedRate(obSc, 0, 10, TimeUnit.SECONDS);

					} else {

						sc = Executors.newScheduledThreadPool(threads);

						for (int j = 0; j < threads; j++) {

							try {

								Thread.sleep(1000);

							} catch (InterruptedException e) {

								e.printStackTrace();
							}

							sc.scheduleAtFixedRate(new PFR(delay, url,
									retryCount), 0, delayIteration,
									TimeUnit.MILLISECONDS);

						}

					}

					Initialization.executorsSchedule.put(nameService, sc);

				} else {

					Initialization.info.info(nameService
							+ " is not used in load test");

				}

			}

		}

		systems = null;

		webServices = null;

	}
}
