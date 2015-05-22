/*
 * Dumbster - a dummy SMTP server
 * Copyright 2004 Jason Paul Kitchen
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package smtp;

import static testing.MailProperties.BANDWIDTH;
import static testing.MailProperties.CONNECTION_TIME;
import static testing.MailProperties.GC_CALLS;
import static testing.MailProperties.LATENCY;
import static testing.MailProperties.MAX_CONNECTION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.DatatypeConverter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.aplana.app.RunServer;
import db.dataBaseHelper;

/**
 * Dummy SMTP server for testing purposes.
 * 
 * @todo constructor allowing user to pass preinitialized ServerSocket
 */

public class SimpleSmtpServer implements Runnable {

	private static final Logger logger = LogManager
			.getFormatterLogger(SimpleSmtpServer.class.getName());

	private SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS");

	/**
	 * Count calls
	 * 
	 */
	private static AtomicInteger countCall = new AtomicInteger(0);

	/**
	 * 
	 * pattern for find application number
	 * 
	 */

	private static Pattern p = Pattern.compile(".*([0-9]{9}).*");

	/**
	 * DB instance
	 * 
	 */
	public static volatile dataBaseHelper helper;

	/**
	 * Stores all of the email received since this instance started up.
	 */
	@SuppressWarnings("rawtypes")
	private Vector receivedMail = null;

	private static volatile SimpleSmtpServer server;

	/**
	 * Default SMTP port is 25.
	 */
	public static final int DEFAULT_SMTP_PORT = 25;

	/**
	 * Indicates whether this server is stopped or not.
	 */
	private volatile boolean stopped = true;

	/**
	 * Handle to the server socket this server listens to.
	 */
	private ServerSocket serverSocket;

	/**
	 * Port the server listens on - set to the default SMTP port initially.
	 */
	private int port = DEFAULT_SMTP_PORT;

	/**
	 * Timeout listening on server socket.
	 */
	private static final int TIMEOUT = 500;

	/**
	 * Constructor.
	 * 
	 * @param port
	 *            port number
	 */

	public SimpleSmtpServer(int port) {

		this.port = port;

	}

	/**
	 * Main loop of the SMTP server.
	 */
	@SuppressWarnings({ "rawtypes" })
	public void run() {

		stopped = false;

		try {

			try {

				serverSocket = new ServerSocket(port, MAX_CONNECTION);

				serverSocket.setSoTimeout(TIMEOUT); // Block for maximum of 1.5
													// seconds

				serverSocket.setPerformancePreferences(CONNECTION_TIME,
						LATENCY, BANDWIDTH);

			} finally {

				synchronized (this) {
					// Notify when server socket has been created
					notifyAll();
				}
			}

			// Server: loop until stopped
			while (!isStopped()) {

				// Start server socket and listen for client connections
				Socket socket = null;

				try {

					socket = serverSocket.accept();

				} catch (Exception e) {

					//logger.error(e.getMessage(), e);

					if (socket != null) {

						socket.close();

					}
					continue; // Non-blocking socket timeout occurred: try
								// accept() again
				}

				countCall.getAndIncrement();

				// Get the input and output streams
				BufferedReader input = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));

				PrintWriter out = new PrintWriter(socket.getOutputStream());

				synchronized (this) {
					/*
					 * We synchronize over the handle method and the list update
					 * because the client call completes inside the handle
					 * method and we have to prevent the client from reading the
					 * list until we've updated it. For higher concurrency, we
					 * could just change handle to return void and update the
					 * list inside the method to limit the duration that we hold
					 * the lock.
					 */

					List msgs = handleTransaction(out, input);

					Iterator iter = msgs.iterator();

					while (iter.hasNext()) {

						SmtpMessage message = (SmtpMessage) iter.next();

						byte[] b = DatatypeConverter.parseBase64Binary(message
								.getBody());

						String body = new String(b);

						if (RunServer.debug) {

							Matcher m = p.matcher(body);

							String appNumber = null;

							if (m.find()) {

								appNumber = m.group(1);

							}

							if (null != appNumber) {

								dataBaseHelper.getInstance().insert(body,
										appNumber);

							} else {

								logger.info(
										"Can't get application number from body: %s",
										body);

							}

							m = null;

						} else {

							logger.info(sdf.format(new Date()) + " Body: %s",
									body);
						}

					}

					if (countCall.get() == GC_CALLS) {

						countCall.set(0);

						Runtime rn = Runtime.getRuntime();

						logger.info("Total memory: %s", rn.totalMemory());

						logger.info("Free memory before GC: %s",
								rn.freeMemory());

						rn.gc();

						logger.info("Free memory after GC: %s", rn.freeMemory());

						logger.info("Call GC!");

					}
				}

				socket.close();
			}
		} catch (Exception e) {

			/** @todo Should throw an appropriate exception here. */
			logger.error("Can't get email! %s", e.getMessage(), e);

		} finally {

			if (serverSocket != null) {

				try {

					serverSocket.close();

					logger.info("Server socket close!");

					server.stop();

				} catch (IOException e) {

					logger.error("Can't close socket! %s", e.getMessage(), e);

				}
			}
		}

	}

	/**
	 * Check if the server has been placed in a stopped state. Allows another
	 * thread to stop the server safely.
	 * 
	 * @return true if the server has been sent a stop signal, false otherwise
	 */
	public synchronized boolean isStopped() {

		return stopped;

	}

	/**
	 * Stops the server. Server is shutdown after processing of the current
	 * request is complete.
	 */
	public synchronized void stop() {

		// Mark us closed
		stopped = true;

		try {

			// Kick the server accept loop
			if (null != serverSocket) {

				serverSocket.close();
			}

		} catch (IOException e) {

			logger.error("Can't stop smtp server! %s", e.getMessage(), e);

		}

	}

	/**
	 * Handle an SMTP transaction, i.e. all activity between initial connect and
	 * QUIT command.
	 * 
	 * @param out
	 *            output stream
	 * @param input
	 *            input stream
	 * @return List of SmtpMessage
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List handleTransaction(PrintWriter out, BufferedReader input) {

		// Initialize the state machine
		SmtpState smtpState = SmtpState.CONNECT;

		SmtpRequest smtpRequest = new SmtpRequest(SmtpActionType.CONNECT, "",
				smtpState);

		// Execute the connection request
		SmtpResponse smtpResponse = smtpRequest.execute();

		// Send initial response
		sendResponse(out, smtpResponse);

		smtpState = smtpResponse.getNextState();

		Vector msgList = new Vector(50);

		SmtpMessage msg = new SmtpMessage();

		while (smtpState != SmtpState.CONNECT) {

			String line = null;

			try {

				line = input.readLine();

			} catch (IOException e) {

				logger.error("Can't read email! %s", e.getMessage(), e);

				logger.error("Line %s", line);

			}

			if (line == null) {

				break;
			}

			// Create request from client input and current state
			SmtpRequest request = SmtpRequest.createRequest(line, smtpState);

			// Execute request and create response object
			SmtpResponse response = request.execute();

			// Move to next internal state
			smtpState = response.getNextState();

			// Send reponse to client
			sendResponse(out, response);

			// Store input in message
			String params = request.getParams();

			msg.store(response, params);

			// If message reception is complete save it
			if (smtpState == SmtpState.QUIT) {

				msgList.add(msg);

				msg = new SmtpMessage();
			}
		}

		return msgList;
	}

	/**
	 * Send response to client.
	 * 
	 * @param out
	 *            socket output stream
	 * @param smtpResponse
	 *            response object
	 */
	private static void sendResponse(PrintWriter out, SmtpResponse smtpResponse) {

		if (smtpResponse.getCode() > 0) {

			int code = smtpResponse.getCode();

			String message = smtpResponse.getMessage();

			out.print(code + " " + message + "\r\n");

			out.flush();
		}
	}

	/**
	 * Get email received by this instance since start up.
	 * 
	 * @return List of String
	 */
	@SuppressWarnings("rawtypes")
	public synchronized Iterator getReceivedEmail() {

		return receivedMail.iterator();

	}

	/**
	 * clear email received.
	 * 
	 * 
	 */
	public synchronized void deleteReceivedEmail() {

		receivedMail.clear();

	}

	/**
	 * Get the number of messages received.
	 * 
	 * @return size of received email list
	 */
	public synchronized int getReceivedEmailSize() {

		return receivedMail.size();

	}

	public static SimpleSmtpServer getInstance() {

		return LasySmtpInstance.server;

	}

	private static class LasySmtpInstance {

		public static SimpleSmtpServer server = new SimpleSmtpServer(
				DEFAULT_SMTP_PORT);

	}

}
