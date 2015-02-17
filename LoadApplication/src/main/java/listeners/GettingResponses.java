package listeners;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

import connections.AsyncResponseTime;
import connections.SaveData;

import ru.aplana.app.Initialization;

import ru.aplana.tools.Common;

/**
 * 
 * Classname: GettingResponses
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * Implementation listener
 * 
 * @author Maksim Stepanov
 * 
 */
public class GettingResponses implements MessageListener {

	private String sysName = null;

	private String methodParse = null;

	private boolean debug = false;

	private Logger infoLog;

	private Logger severeLog;

	private ArrayList<String> xpath = new ArrayList<String>();

	/**
	 * 
	 * @param queue
	 *            for listener
	 * @param sysName
	 *            - name external system
	 * @param xpath
	 *            - checking valid status for response
	 */

	public GettingResponses(String sysName, ArrayList<String> xpath,
			String methodParse, boolean debugOnMessage, Logger infoLog,
			Logger severeLog) {

		this.sysName = sysName;

		this.xpath = xpath;

		this.methodParse = methodParse;

		this.debug = debugOnMessage;

		this.infoLog = infoLog;

		this.severeLog = severeLog;

	}

	public void onMessage(Message inputMsg) {

		String request = null;

		String corrId = null;

		try {

			corrId = inputMsg.getJMSCorrelationID();

		} catch (JMSException e1) {

			// nothing

		}

		try {

			request = Common.parseMessMQ(inputMsg);

			if (null != corrId) {

				Initialization.saveDb.submit(new AsyncResponseTime(corrId));

			} else {

				this.infoLog.info("Correlation id is null! System: " + this.sysName);

				this.infoLog.info("Request: " + request);

			}

			if (null != request) {

				if (this.methodParse.equalsIgnoreCase("checknode")) {

					checkXMLbyNode(request, this.sysName);

				}

				if (this.methodParse.equalsIgnoreCase("checkbyxpath")) {

					getValuebyXPath(request, this.sysName);
				}

				if (this.methodParse.equalsIgnoreCase("checkregexp")) {

					getValuebyRegexp(request, this.sysName);
				}

			} else {

				this.infoLog.info("Get null! System: " + this.sysName);

			}

		} catch (JMSException e) {

			this.severeLog.info("Can't parse message: " + request + "Error: "
					+ e.getMessage());

			e.printStackTrace();
		}

	}

	private final void checkXMLbyNode(String request, String system) {

		if (this.debug) {

			this.infoLog.info("System: " + system + "; Request: " + request);

		}

		Object value = null;

		boolean flagCheck = false;

		XPath xPath = XPathFactory.newInstance().newXPath();

		for (int i = 0; i < this.xpath.size(); i++) {

			try {

				String xpathExpression = this.xpath.get(i);

				XPathExpression expr = xPath.compile(xpathExpression);

				value = expr.evaluate(
						new InputSource(new StringReader(request)),
						XPathConstants.NODE);

				if (null != value) {

					flagCheck = true;

					if (system.equalsIgnoreCase("eq")
							|| system.equalsIgnoreCase("eq_ul")) {

						if (xpathExpression
								.equalsIgnoreCase("//switch_response/title_part/commercial")) {

							system = "EQ_UL";

						} else if (xpathExpression
								.equalsIgnoreCase("//response/title_part/lastname")) {

							system = "EQ";

						}

					}

				}

			} catch (XPathExpressionException e) {

				this.severeLog.severe("Error checkXML: " + e.getMessage());

			} finally {

				/**
				 * 
				 * nothing
				 */
			}

		}

		Initialization.saveDb.execute(new SaveData(request, flagCheck, system));

	}

	private final void getValuebyXPath(String request, String system) {

		if (this.debug) {

			this.infoLog.info("System: " + system + "; Request: " + request);

		}

		String value = null;

		boolean flagCheck = false;

		XPath xPath = XPathFactory.newInstance().newXPath();

		for (int i = 0; i < this.xpath.size(); i++) {

			try {

				value = xPath.evaluate(this.xpath.get(i), new InputSource(
						new StringReader(request)));

			} catch (XPathExpressionException e) {

				this.severeLog.severe("Error getValue: " + e.getMessage());

				value = "";

			} finally {

				flagCheck = false;
			}

			if (system.equalsIgnoreCase("srg")) {

				if (value.equalsIgnoreCase("Ошибок нет.")) {

					flagCheck = true;

				}
			}

			if (system.equalsIgnoreCase("fps")) {

				if (value.equalsIgnoreCase("0")) {

					flagCheck = true;

				}
			}

			if (system.equalsIgnoreCase("mbki")) {

				if (value.equalsIgnoreCase("0")) {

					flagCheck = true;

				}
			}

			if (system.equalsIgnoreCase("fms")) {

				if (value.equalsIgnoreCase("0")) {

					flagCheck = true;

				}
			}

			if (system.equalsIgnoreCase("ei")) {

				if (value.equalsIgnoreCase("0")) {

					flagCheck = true;

				}
			}

			if (system.equalsIgnoreCase("nbki")) {

				if (value.equalsIgnoreCase("483647")) {

					flagCheck = true;

				}

			}

			if (system.equalsIgnoreCase("ei_ul")) {

				if (value.equalsIgnoreCase("0")) {

					flagCheck = true;

				}

			}

			if (system.equalsIgnoreCase("nbki_ul")) {

				if (value.equalsIgnoreCase("483647")) {

					flagCheck = true;

				}
			}

			if (system.equalsIgnoreCase("brs")) {

				if (value.equalsIgnoreCase("39893022")) {

					flagCheck = true;

				}

			}

		}

		Initialization.saveDb.execute(new SaveData(request, flagCheck, system));

	}

	private final void getValuebyRegexp(String request, String system) {

		if (this.debug) {

			this.infoLog.info("System: " + system + "; Request: " + request);

		}

		String value = null;

		boolean flagCheck = false;

		for (int i = 0; i < this.xpath.size(); i++) {

			Pattern p = Pattern.compile(this.xpath.get(i));

			Matcher m = p.matcher(request);

			if (m.find()) {

				value = m.group(1);

			}
		}

		if (null != value) {

			if (system.equalsIgnoreCase("nh")) {

				if (value.equalsIgnoreCase("0")) {

					flagCheck = true;

				}

			}

		}

		Initialization.saveDb.execute(new SaveData(request, flagCheck, system));

	}

}
