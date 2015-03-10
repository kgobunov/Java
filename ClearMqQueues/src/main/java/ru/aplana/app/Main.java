package ru.aplana.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tool.ListenersSet;
import tool.checkDepth;
import tool.listQueues;

public class Main {

	public static ArrayList<String> queuesNames = new ArrayList<String>();

	public static HashMap<String, Integer> checkDepth = new HashMap<String, Integer>();

	public static ExecutorService exec = Executors.newCachedThreadPool();

	public static ScheduledExecutorService sce = Executors
			.newSingleThreadScheduledExecutor();

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		System.out.println("Starting clear queues ...");

		new listQueues();

		exec.submit(new ListenersSet());

		sce.scheduleAtFixedRate(new checkDepth(), 0, 10, TimeUnit.SECONDS);

	}
}
