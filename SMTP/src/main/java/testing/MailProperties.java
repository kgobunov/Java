package testing;

import ru.aplana.app.RunServer;

/**
 * SMTP properties
 * 
 * @author Maksim Stepanov
 * 
 */
public class MailProperties {

	public static final int PORT = Integer.parseInt(RunServer.smtp
			.getChildText("port"));

	public static final String FROM = RunServer.smtp.getChildText("from");

	public static final String SUBJECT = RunServer.smtp.getChildText("subject");

	public static final String BODY = RunServer.smtp.getChildText("body");

	public static final String TO = RunServer.smtp.getChildText("to");

	// for server socket
	public static final int MAX_CONNECTION = Integer.parseInt(RunServer.smtp
			.getChildText("maxConnection"));

	// Performance preferences

	public static final int CONNECTION_TIME = Integer.parseInt(RunServer.smtp
			.getChildText("connectionTime"));

	public static final int LATENCY = Integer.parseInt(RunServer.smtp
			.getChildText("latency"));

	public static final int BANDWIDTH = Integer.parseInt(RunServer.smtp
			.getChildText("bandwidth"));

	// gc

	public static final int GC_CALLS = Integer.parseInt(RunServer.smtp
			.getChildText("gc"));

}
