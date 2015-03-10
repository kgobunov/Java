package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import ru.aplana.tools.GetData;
import db.DbOperation;

/**
 * Implementation listener
 * 
 * @author Maksim Stepanov
 * 
 */
public class ESBListener implements MessageListener {

	private ArrayList<String> dataArray = null;

	public ESBListener() {

		loggerInfo.info("Init ESBListener!");

	}

	@Override
	public void onMessage(Message inputMsg) {

		this.dataArray = new ArrayList<String>(6);

		try {

			String request = parseMessMQ(inputMsg);

			GetData getData = new GetData(request);

			try {

				this.dataArray.add(getData.getValueByName("RqUID"));

				this.dataArray.add(getData.getValueByName("StatusCode"));

				this.dataArray.add(getData.getValueByName("ApplicationNumber"));

				if (this.dataArray.get(2).length() == 0) {

					this.dataArray.set(2, "null");

					this.dataArray.add(getData.getValueByName("ErrorCode"));

					this.dataArray.add(getData.getValueByName("Message"));

				} else {

					String error_code = getData.getValueByName("ErrorCode");

					if (error_code.length() > 0) {

						this.dataArray.add(error_code);

					} else {

						this.dataArray.add("0");
					}

					switch (Integer.valueOf(this.dataArray.get(1))) {

					case -1:
						this.dataArray.add("Ошибки заполнения");
						break;
					case 0:
						this.dataArray.add("Отказ");
						break;
					case 1:
						this.dataArray.add("Заявка создана успешно");
						break;
					case 2:
						this.dataArray.add("Одобрена");
						break;
					case 3:
						this.dataArray.add("Обрабатывается");
						break;
					case 4:
						this.dataArray.add("Кредит Выдан");
						break;
					default:
						break;
					}

				}

			} catch (Exception e) {

				loggerSevere.severe(e.getMessage());

				e.printStackTrace();

			}

			try {

				DbOperation.getInstance().evalOperation(2, this.dataArray);

			} catch (Exception e) {

				loggerSevere.severe(e.getMessage());

				e.printStackTrace();

			}

		} catch (JMSException e) {

			loggerSevere.severe(e.getMessage());

			e.printStackTrace();
		}

	}

}
