package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;
import javax.xml.stream.XMLStreamException;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.util.AXIOMUtil;
import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.transport.http.HTTPConstants;

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

	public ServicesListener(MQQueueConnection connection) throws JMSException {

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		this.queueSend = (MQQueue) this.session
				.createQueue(Queues.SERVICE_GARBAGE_OUT);

	}

	public void onMessage(Message inputMsg) {

		String request = null;

		request = parseMessMQ(inputMsg);

		PropsChecker.loggerInfo.info("Request to OSGI: " + request);

		OMElement ougRequest = null;

		// convert string to omelement
		try {

			ougRequest = AXIOMUtil.stringToOM(request);

		} catch (XMLStreamException e2) {

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

		try {

			ougResponse = ougwsStub.ougWS(ougRequest);

			flag = true;

		} catch (Exception e) {

			PropsChecker.loggerSevere.severe("OSGI url: " + urlOsgi
					+ ";Can't send to OSGI: " + request + "; Error message: "
					+ e.getMessage());

			e.printStackTrace();

		} finally {

			// Clean up!
			try {

				this.ougwsStub._getServiceClient().cleanupTransport();

				this.ougwsStub._getServiceClient().cleanup();

			} catch (AxisFault e) {

				e.printStackTrace();

			}

			this.ougwsStub = null;

		}

		if (flag) {

			PropsChecker.loggerInfo.info("Send to " + urlOsgi
					+ "; OSGI response: " + ougResponse.toString());

		} else {

			PropsChecker.loggerInfo
					.info("Error on listener! Incoming message: " + request);

			// send bad message to garbage queue

			try {

				TextMessage outputMsg = this.session.createTextMessage(request);

				this.queueSend.setTargetClient(JMSC.MQJMS_CLIENT_NONJMS_MQ);

				MessageProducer producer = this.session
						.createProducer(this.queueSend);

				producer.send(outputMsg);

				producer.close();

				PropsChecker.loggerInfo.info("OSGI: " + urlOsgi
						+ "; Message send to " + Queues.SERVICE_GARBAGE_OUT
						+ " successfully!");

			} catch (JMSException e) {

				PropsChecker.loggerSevere.severe("OSGI: " + urlOsgi
						+ "; Can't send message to "
						+ Queues.SERVICE_GARBAGE_OUT + ". Error: "
						+ e.getMessage());

				e.printStackTrace();
			}

		}

	}

}
