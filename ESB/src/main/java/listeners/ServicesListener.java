package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

	// link to UG
	private String url = "";

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSSSSS");

	private XPath xPath = XPathFactory.newInstance().newXPath();

	private static final Logger logger = LogManager
			.getFormatterLogger(ServicesListener.class.getName());

	public ServicesListener(MQQueueConnection connection) {

		this(connection, null);

	}

	public ServicesListener(MQQueueConnection connection, String url) {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		try {

			this.queueSend = (MQQueue) this.session
					.createQueue(Queues.SERVICE_GARBAGE_OUT);

			this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

		} catch (JMSException e) {

			logger.error("Can't create queue %s", e.getMessage(), e);

		}

		this.url = url;

	}

	public void onMessage(Message inputMsg) {

		String request = null;

		request = parseMessMQ(inputMsg);

		logger.debug("Request to OSGI: %s", request);

		String workflowName = "undefined";

		if (request != null) {

			try {

				workflowName = this.xPath.evaluate("//workflow_name",
						new InputSource(new StringReader(request)));

			} catch (XPathExpressionException e1) {

				logger.error(e1.getMessage(), e1);

			}

		}

		OMElement ougRequest = null;

		// convert string to omelement
		try {

			ougRequest = AXIOMUtil.stringToOM(request);

		} catch (XMLStreamException e2) {

			logger.error("Can't convert string to Omelement! %s",
					e2.getMessage(), e2);

		}

		String urlOsgi = (null == this.url) ? PropsChecker.getNextUrl()
				: this.url;

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

			logger.error("OSGI: %s; Can't set endpoint! %s", urlOsgi,
					e.getMessage(), e);

		}

		OMElement ougResponse = null;

		boolean flag = false;

		Thread t = Thread.currentThread();

		long threadId = t.getId();

		String threadName = t.getName();

		try {

			long start = System.currentTimeMillis();

			ougResponse = this.ougwsStub.ougWS(ougRequest);

			long end = System.currentTimeMillis();

			logger.debug("| "
					+ sdf.format(new Date(System.currentTimeMillis())) + " | "
					+ workflowName + " | " + threadId + " |"
					+ sdf.format(new Date(start)) + " | "
					+ sdf.format(new Date(end)) + " | " + urlOsgi + " | "
					+ (end - start));

			flag = true;

		} catch (Exception e) {

			logger.error(
					"Time: %s; Workflow: %s; Thread info: ID - %d; %s; OSGI url: %s; Can't send to OSGI: %s; Error message: %s",
					sdf.format(new Date(System.currentTimeMillis())),
					workflowName, threadId, threadName, urlOsgi, request,
					e.getMessage(), e);

		} finally {

			// Clean up!
			try {

				this.ougwsStub._getServiceClient().cleanupTransport();

				this.ougwsStub._getServiceClient().cleanup();

			} catch (AxisFault e) {

				logger.error("Failed clenup! %s", e.getMessage(), e);

			}

			this.ougwsStub = null;

		}

		if (flag) {

			logger.debug("OSGI response: %s", ougResponse.toString());

		} else {

			logger.info("Can't send to UG %s", request);

			// send bad message to garbage queue

			MessageProducer producer = null;

			try {

				TextMessage outputMsg = this.session.createTextMessage(request);

				producer = this.session.createProducer(this.queueSend);

				producer.send(outputMsg);

				logger.info("OSGI: %s; Message send to %s successfully!",
						urlOsgi, Queues.SERVICE_GARBAGE_OUT);

			} catch (Exception e) {

				logger.error("OSGI: %s; Can't send message to %s. Error: %s",
						urlOsgi, Queues.SERVICE_GARBAGE_OUT, e.getMessage(), e);

			} finally {

				try {

					if (null != producer) {

						producer.close();

					}

				} catch (JMSException e) {

					logger.error("Can't close producer! %s", e.getMessage(), e);

				}

			}

		}

	}
}
