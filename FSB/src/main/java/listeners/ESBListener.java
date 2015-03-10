package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static ru.aplana.tools.MQTools.getSession;
import static tools.PropCheck.fsb;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import requests.ReplyToESB;
import ru.aplana.app.FSBMqJms;
import ru.aplana.tools.GetData;
import tools.PropCheck;

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

	private MQQueueConnection connection;

	public ESBListener(MQQueueConnection connection) {

		loggerInfo.info("Init ESBListener");

		this.connection = connection;

		try {

			this.session = getSession(this.connection, false,
					MQQueueSession.AUTO_ACKNOWLEDGE);

		} catch (JMSException e) {

			e.printStackTrace();

		}

	}

	@Override
	public void onMessage(Message inputMsg) {

		ArrayList<String> dataArray = new ArrayList<String>(5);

		int status = -99;

		GetData getData = null;

		try {

			String request = parseMessMQ(inputMsg);

			getData = new GetData(request);

			boolean flag_tsm = false;

			try {

				String rquid = getData.getValueByName("RqUID");

				if (rquid.length() == 0) {

					rquid = getData.getValueByName("MessageId");

					flag_tsm = true;

				}

				dataArray.add(rquid);

				dataArray.add(getData.getValueByName("StatusCode"));

				dataArray.add(getData.getValueByName("ApplicationNumber"));

				dataArray.add(getData.getValueByName("ErrorCode"));

				try {

					status = Integer.parseInt(dataArray.get(1));

				} catch (Exception e) {

					loggerSevere.severe("Error: Can't parse status. "
							+ e.getMessage());

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

				loggerSevere.severe("Error: Can't save data from TSM "
						+ e.getMessage());

				e.printStackTrace();

			}

			try {

				if (flag_tsm) {

					if (FSBMqJms.logTsmApp) {

						DbOperation.getInstance().evalOperation(3, dataArray);

					}

				} else {

					DbOperation.getInstance().evalOperation(2, dataArray);
				}

			} catch (Exception e) {

				loggerSevere.severe(e.getMessage());

				e.printStackTrace();

			}

			if (status == 2) {

				ArrayList<String> data = new ArrayList<String>(4);

				data.add(getData.getValueByName("MessageId"));

				data.add(getData.getValueByName("MessageDT"));

				data.add(getData.getValueByName("RqUID"));

				data.add(getData.getValueByName("RqTm"));

				String queue = fsb.getChildText("queueTo");

				String appNumber = getData.getValueByName("ApplicationNumber");

				if (PropCheck.debug) {

					loggerInfo.info("Credit confirm for app number: "
							+ appNumber);

				}

				String req = ReplyToESB.getResp(data);

				if (PropCheck.debug) {

					loggerInfo.info("FSB to ESB: " + req);

				}

				TextMessage outputMsg = this.session.createTextMessage(req);

				MQQueue queueSend = (MQQueue) this.session.createQueue(queue);

				MessageProducer producer = this.session
						.createProducer(queueSend);

				long delay = Long.parseLong(fsb.getChildText("delay")) * 1000;

				if (delay > 0) {

					try {

						Thread.sleep(delay);

					} catch (NumberFormatException e) {

						e.printStackTrace();

					} catch (InterruptedException e) {

						e.printStackTrace();

					}

				}

				producer.send(outputMsg);

				producer.close();

			}

		} catch (JMSException e) {

			loggerSevere.severe(e.getMessage());

			e.printStackTrace();
		}

	}

}
