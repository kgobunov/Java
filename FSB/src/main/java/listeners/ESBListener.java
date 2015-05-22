package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropCheck.fsb;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import requests.ReplyToESB;
import ru.aplana.tools.GetData;

import com.ibm.mq.jms.MQQueue;
import com.ibm.mq.jms.MQQueueConnection;
import com.ibm.mq.jms.MQQueueSession;

import db.DbOperation;

/**
 * Implementation listener for queue in ESB for CRM status message
 * 
 * @author Maksim Stepanov
 * 
 */
public class ESBListener implements MessageListener {

	private MQQueueSession session;

	private final MQQueueConnection connection;

	private MQQueue queueSend;

	private long delay = 0;

	private static final Logger logger = LogManager
			.getFormatterLogger(ESBListener.class.getName());

	public ESBListener(MQQueueConnection connection) {

		logger.info("Init ESBListener");

		this.connection = connection;

		this.session = getSession(this.connection, false,
				MQQueueSession.AUTO_ACKNOWLEDGE);

		try {

			this.queueSend = (MQQueue) this.session.createQueue(fsb
					.getChildText("queueTo"));

		} catch (JMSException e) {

			logger.error(e.getMessage(), e);

		}

		this.delay = Long.parseLong(fsb.getChildText("delay")) * 1000;

	}

	@Override
	public void onMessage(Message inputMsg) {

		ArrayList<String> dataArray = new ArrayList<String>(5);

		int status = -99;

		GetData getData = null;

		String request = parseMessMQ(inputMsg);

		getData = GetData.getInstance(request);

		try {

			dataArray.add(getData.getValueByName("RqUID"));

			dataArray.add(getData.getValueByName("StatusCode"));

			dataArray.add(getData.getValueByName("ApplicationNumber"));

			dataArray.add(getData.getValueByName("ErrorCode"));

			try {

				status = Integer.parseInt(dataArray.get(1));

			} catch (Exception e) {

				logger.error("Can't parse status. %s", e.getMessage(), e);

				dataArray.set(1, "-99");

			}
			switch (status) {
			case 1:
				dataArray.add("Заявка успешно создана!");
				break;
			case 0:
				dataArray.add("Отказ в кредите!");
				break;
			case 2:
				dataArray.add("Заявка одобрена!");
				break;
			case 3:
				dataArray.add("Заявка отправлена в обработку!");
				break;
			case 4:
				dataArray.add("Кредит выдан!");
				break;
			case -99:
				dataArray.add("Unknown xml!");
				break;
			default:
				dataArray.add(getData.getValueByName("Message"));
				break;
			}

		} catch (Exception e) {

			logger.error("Can't save data from TSM %s", e.getMessage(), e);

		}

		try {

			DbOperation.getInstance().evalOperation(2, dataArray);

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

		}

		if (status == 2) {

			ArrayList<String> data = new ArrayList<String>(4);

			data.add(getData.getValueByName("MessageId"));

			data.add(getData.getValueByName("MessageDT"));

			data.add(getData.getValueByName("RqUID"));

			data.add(getData.getValueByName("RqTm"));

			String appNumber = getData.getValueByName("ApplicationNumber");

			logger.info("Credit confirm for app number: %s", appNumber);

			String req = ReplyToESB.getResp(data);

			TextMessage outputMsg = null;

			MessageProducer producer = null;

			try {

				outputMsg = this.session.createTextMessage(req);

				producer = this.session.createProducer(this.queueSend);

				if (this.delay > 0) {

					try {

						Thread.sleep(this.delay);

					} catch (NumberFormatException | InterruptedException e) {

						logger.error(e.getMessage(), e);

					}

				}

				producer.send(outputMsg);

				logger.info("Send successfully - FSB to ESB: %s", req);

			} catch (JMSException e) {

				logger.error(e.getMessage(), e);

			} finally {

				try {

					if (null != producer) {

						producer.close();

					}

				} catch (JMSException e) {

					logger.error(e.getMessage(), e);

				}
			}

		}

	}

}
