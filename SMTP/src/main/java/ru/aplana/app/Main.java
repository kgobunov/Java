package ru.aplana.app;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * SMTP server
 * 
 * @author Maksim Stepanov
 * 
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ExecutorService exec = Executors.newFixedThreadPool(1);

		exec.submit(new RunServer());

	}

}
