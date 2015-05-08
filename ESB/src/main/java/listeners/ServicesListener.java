package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropsChecker.debug;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;
import org.xml.sax.InputSource;

import tools.PropsChecker;
import tools.Queues;

import com.ibm.mq.jms.JMSC;
import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueSession;
import com.scorex.osgi.oug.components.ougwebcomponent.OUGWSStub;

/**
 * 
 * Get messages from services and to osgi
 * 
 * @author Maksim Stepanov
 * 
 */
@SuppressWarnings("deprecation")
public class ServicesListener implements MessageListener {

	private OUGWSStub ougwsStub;

	private MQQueueSession session;

	private MQQueue queueSend;

	private MQQueueConnection connection;

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'hh:mm:ss.SSSSSS");

	private XPath xPath = XPathFactory.newInstance().newXPath();

	public ServicesListener(MQQueueConnection connection) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		try {

			this.queueSend = (MQQueue) this.session
					.createQueue(Queues.SERVICE_GARBAGE_OUT);

			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

		} catch (JMSException e) {

			e.printStackTrace();

		}

	}

	public void onMessage(Message inputMsg) {

		String request = null;

		request = parseMessMQ(inputMsg);

		if (debug.get()) {

			PropsChecker.loggerInfo.info("Request to OSGI: " + request);

		}

		String workflowName = "undefined";

		if (request != null) {

			try {

				workflowName = this.xPath.evaluate("//workflow_name",
						new InputSource(new StringReader(request)));

			} catch (XPathExpressionException e1) {

				e1.printStackTrace();

			}

		}

		OMElement ougRequest = null;

		// convert string to omelement
		try {

			ougRequest = AXIOMUtil.stringToOM(request);

		} catch (XMLStreamException e2) {

			PropsChecker.loggerSevere
					.severe("Can't convert string to Omelement! "
							+ e2.getMessage());

			e2.printStackTrace();
		}

		String urlOsgi = PropsChecker.getNextUrl();

		// Set endpoint with properties
		try {

			this.ougwsStub = new OUGWSStub(urlOsgi);

			Options opts = this.ougwsStub._getServiceClient().getOptions();

			opts.setProperty(HTTPConstants.HTTP_PROTOCOL_VERSION,
					HTTPConstants.HEADER_PROTOCOL_11);

			opts.setTimeOutInMilliSeconds(Integer.parseInt(PropsChecker.esb
					.getChildText("ougClientTimeout")));

			opts.setProperty(HTTPConstants.MULTITHREAD_HTTP_CONNECTION_MANAGER,
					PropsChecker.connManager);

		} catch (AxisFault e) {

			PropsChecker.loggerSevere.severe("OSGI: " + urlOsgi
					+ "; Can't set endpoint! " + e.getMessage());

			e.printStackTrace();
		}

		OMElement ougResponse = null;

		boolean flag = false;

		if (debug.get()) {

			PropsChecker.loggerInfo.info("Object: " + this);

		}

		Thread t = Thread.currentThread();

		String threadName = t.getName();

		long threadId = t.getId();

		try {

			long start = System.currentTimeMillis();

			ougResponse = this.ougwsStub.ougWS(ougRequest);

			long end = System.currentTimeMillis();

			if (debug.get()) {

				PropsChecker.loggerInfo.info("Time: "
						+ sdf.format(new Date(System.currentTimeMillis()))
						+ "; Workflow: " + workflowName
						+ "; Thread info: ID - " + threadId + "; " + threadName
						+ "; Start time to UG " + sdf.format(new Date(start))
						+ "; End time to UG " + sdf.format(new Date(end))
						+ "; Response time from UG " + urlOsgi + ": "
						+ (end - start) + " ms");

			}

			flag = true;

		} catch (Exception e) {

			PropsChecker.loggerSevere.severe("Time: "
					+ sdf.format(new Date(System.currentTimeMillis()))
					+ "Workflow:" + workflowName + "; Thread info: ID - "
					+ threadId + "; " + threadName + "; OSGI url: " + urlOsgi
					+ "; Can't send to OSGI: " + request + "; Error message: "
					+ e.getMessage());

			e.printStackTrace();

		} finally {

			// Clean up!
			try {

				this.ougwsStub._getServiceClient().cleanupTransport();

				this.ougwsStub._getServiceClient().cleanup();

			} catch (AxisFault e) {

				PropsChecker.loggerSevere.severe("Failed clenup! "
						+ e.getMessage());

				e.printStackTrace();

			}

			this.ougwsStub = null;

		}

		if (flag) {

			if (debug.get()) {

				PropsChecker.loggerInfo.info("Send to " + urlOsgi
						+ "; OSGI response: " + ougResponse.toString());

			}

		} else {

			PropsChecker.loggerInfo
					.info("Error on listener! Incoming message: " + request);

			// send bad message to garbage queue

			MessageProducer producer = null;

			try {

				TextMessage outputMsg = this.session.createTextMessage(request);

				producer = this.session.createProducer(this.queueSend);

				producer.send(outputMsg);

				PropsChecker.loggerInfo.info("OSGI: " + urlOsgi
						+ "; Message send to " + Queues.SERVICE_GARBAGE_OUT
						+ " successfully!");

			} catch (Exception e) {

				PropsChecker.loggerSevere.severe("OSGI: " + urlOsgi
						+ "; Can't send message to "
						+ Queues.SERVICE_GARBAGE_OUT + ". Error: "
						+ e.getMessage());

				e.printStackTrace();

			} finally {

				try {

					if (null != producer) {

						producer.close();

					}

				} catch (JMSException e) {

					PropsChecker.loggerSevere.severe("Can't close produser! "
							+ e.getMessage());

					e.printStackTrace();

				}

			}

		}

	}
}
