package pfr;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import ru.aplana.app.Initialization;
import ru.aplana.tools.Common;
import ru.aplana.tools.CreateLogger;
import ru.aplana.tools.GetData;

/**
 * Classname: PFR
 * 
 * Version: 1.0
 * 
 * 
 * Implementation call pfr service
 * 
 * @author Maksim Stepanov
 * 
 */
public class PFR implements Runnable {

	public static Logger loggerInfo = null;

	public static Logger loggerSevere = null;

	private static CreateLogger loggers = null;

	private static AtomicInteger count = new AtomicInteger(0);

	private SOAPMessage soapResponse;

	private ArrayList<String> privateInfo = new ArrayList<String>();

	private long delay;

	private String url;

	private ExecutorService ex = Executors.newCachedThreadPool();

	private String whatNow;

	private int retry;

	static {

		if (null == loggers) {

			loggers = new CreateLogger("logs\\PFR", 512000000, 2);

			loggerInfo = loggers.getInfoLogger();

			loggerSevere = loggers.getSevereLogger();

			loggerInfo.info("PFR logger info created successfully!");

			loggerSevere.severe("PFR logger severe created successfully!");

		}

	}

	public PFR(long delay, String url, int retryCount) {

		this.delay = delay;

		this.url = url;

		this.retry = retryCount;

		Initialization.executors.put("PFR_" + count.getAndIncrement(), this.ex);

	}

	@Override
	public void run() {

		long startIteration = System.currentTimeMillis();

		try {

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory
					.newInstance();

			SOAPConnection soapConnection = soapConnectionFactory
					.createConnection();

			// Send SOAP Message to SOAP Server
			URL url = new URL(this.url);

			FindPerson person = new FindPerson("Search", true);

			this.whatNow = "FindPerson[Search]";

			long start = System.currentTimeMillis();

			this.soapResponse = soapConnection.call(person.getMessage(), url);

			long responseTime = (System.currentTimeMillis() - start);

			this.ex.submit(new Statistics(this.whatNow, responseTime, null));

			loggerInfo.info(this.whatNow + " response time: " + responseTime
					+ " ms");

			this.privateInfo = person.getFIO();

			String soapResp = Common.convertSOAPResponse(soapResponse);

			loggerInfo.info("Response " + this.whatNow + ": " + soapResp);

			String checkResponse = getValuebyXpath(
					"//*[local-name()='FindPersonResult']", soapResp);

			if (checkResponse.equalsIgnoreCase("<Persons/>")) {

				loggerInfo.info("Not Found");

				this.whatNow = "GetNewStatementRegNumber";

				start = System.currentTimeMillis();

				SOAPMessage respGetNewStatementRegNumber = soapConnection.call(
						new GetNewStatementRegNumber(true).getMessage(), url);

				responseTime = (System.currentTimeMillis() - start);

				this.ex.submit(new Statistics(this.whatNow, responseTime, null));

				loggerInfo.info(this.whatNow + " response time: "
						+ responseTime + " ms");

				String respGetNumber = Common
						.convertSOAPResponse(respGetNewStatementRegNumber);

				loggerInfo.info("Response " + this.whatNow + ": "
						+ respGetNumber);

				String reqNumber = getValuebyXpath(
						"//*[local-name()='GetNewStatementRegNumberResult']",
						respGetNumber);

				loggerInfo.info("reqNumber: " + reqNumber);

				this.privateInfo.add(reqNumber);

				this.whatNow = "CreateOrUpdatePersonWithStatement";

				start = System.currentTimeMillis();

				SOAPMessage respCreateOrUpdatePersonWithStatement = soapConnection
						.call(new CreateOrUpdatePersonWithStatement(
								this.privateInfo, true).getMessage(), url);

				responseTime = (System.currentTimeMillis() - start);

				this.ex.submit(new Statistics(this.whatNow, responseTime, null));

				loggerInfo.info(this.whatNow + " response time: "
						+ responseTime + " ms");

				String respNewPerson = Common
						.convertSOAPResponse(respCreateOrUpdatePersonWithStatement);

				loggerInfo.info("Response " + this.whatNow + ": "
						+ respNewPerson);

				this.whatNow = "CreateRequestForOpen";

				start = System.currentTimeMillis();

				SOAPMessage respCreateRequestForOpen = soapConnection.call(
						new CreateRequestForOpen(this.privateInfo, true)
								.getMessage(), url);

				responseTime = (System.currentTimeMillis() - start);

				this.ex.submit(new Statistics(this.whatNow, responseTime, null));

				loggerInfo.info(this.whatNow + " response time: "
						+ responseTime + " ms");

				String respForOpen = Common
						.convertSOAPResponse(respCreateRequestForOpen);

				loggerInfo
						.info("Response " + this.whatNow + ": " + respForOpen);

				// get statement id

				boolean flag = true;

				String respStatementId = null;

				String statementId = null;

				int time = 0;

				String openConfirmDate = null;

				while (flag) {

					time++;

					this.whatNow = "FindPerson[by request number]";

					start = System.currentTimeMillis();

					SOAPMessage respGetStatementId = soapConnection.call(
							new FindPerson(reqNumber, true).getMessage(), url);

					responseTime = (System.currentTimeMillis() - start);

					this.ex.submit(new Statistics(this.whatNow, responseTime,
							null));

					loggerInfo.info(this.whatNow + " response time: "
							+ responseTime + " ms");

					respStatementId = Common
							.convertSOAPResponse(respGetStatementId);

					loggerInfo.info("respGetStatementId: " + respStatementId);

					String Persons = getValuebyXpath(
							"//*[local-name()='FindPersonResult']",
							respStatementId);

					Pattern p = Pattern.compile(".*Statement id=\"(\\d+)\".*");

					Matcher m = p.matcher(respStatementId);

					if (m.find()) {

						statementId = m.group(1);

						this.privateInfo.add(statementId);

					}

					GetData data = new GetData(Persons);

					openConfirmDate = data.getValueByName("OpenConfirmDate");

					loggerInfo.info("openConfirmDate: " + openConfirmDate);

					if (openConfirmDate.length() > 0) {

						flag = false;

					}

					if (flag) {

						Thread.sleep(this.delay);

					}

					if (time == this.retry) {

						flag = false;

					}

				}

				if (openConfirmDate.length() > 0) {

					loggerInfo.info("Response respGetStatementId: "
							+ respStatementId);

					loggerInfo.info("Response statementId: " + statementId);

					this.whatNow = "CreateRequestForGetInfo";

					start = System.currentTimeMillis();

					SOAPMessage respCreateRequestForGetInfo = soapConnection
							.call(new CreateRequestForGetInfo(this.privateInfo,
									true).getMessage(), url);

					responseTime = (System.currentTimeMillis() - start);

					this.ex.submit(new Statistics(this.whatNow, responseTime,
							null));

					loggerInfo.info(this.whatNow + " response time: "
							+ responseTime + " ms");

					String respGetInfo = Common
							.convertSOAPResponse(respCreateRequestForGetInfo);

					loggerInfo.info("Response respGetInfo: " + respGetInfo);

					Pattern p = Pattern.compile(".*;Result id=\"(\\d+)\".*");

					Matcher m = p.matcher(respGetInfo);

					String resultId = null;

					if (m.find()) {

						resultId = m.group(1);

						this.privateInfo.add(resultId);
					}

					loggerInfo.info("resultId: " + resultId);

					// GetInfoByRequestID

					this.whatNow = "GetInfoByRequestID";

					start = System.currentTimeMillis();

					SOAPMessage respGetInfoByRequestID = soapConnection.call(
							new GetInfoByRequestID(this.privateInfo, true)
									.getMessage(), url);

					responseTime = (System.currentTimeMillis() - start);

					this.ex.submit(new Statistics(this.whatNow, responseTime,
							null));

					loggerInfo.info(this.whatNow + " response time: "
							+ responseTime + " ms");

					String respGetInfoByRequest = Common
							.convertSOAPResponse(respGetInfoByRequestID);

					loggerInfo.info("Response respGetInfoByRequest: "
							+ respGetInfoByRequest);

				} else {

					this.ex.submit(new Statistics(this.whatNow, 0,
							"Error: Can't get confirm date"));

				}

			} else {

				loggerInfo.info("Person was found!");

			}

		} catch (Exception e) {

			this.ex.submit(new Statistics(this.whatNow, 0, e
					.getLocalizedMessage()));

		} finally {

			this.whatNow = "PFR";

			long stopIteration = System.currentTimeMillis();

			long timeIteration = stopIteration - startIteration;

			this.ex.submit(new Statistics(this.whatNow, timeIteration, null));

		}

	}

	private String getValuebyXpath(String xpathExpr, String xml)
			throws XPathExpressionException {

		String value = null;

		String xpath = xpathExpr;

		XPath xPath = XPathFactory.newInstance().newXPath();

		value = xPath.evaluate(xpath, new InputSource(new StringReader(xml)));

		return value;

	}

}
