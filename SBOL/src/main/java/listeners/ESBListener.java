package listeners;

import static ru.aplana.tools.Common.parseMessMQ;

import java.util.ArrayList;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.aplana.tools.GetData;
import db.DbOperation;

/**
 * Implementation listener
 * 
 * @author Maksim Stepanov
 * 
 */
public class ESBListener implements MessageListener {

	private static final Logger logger = LogManager
			.getFormatterLogger(ESBListener.class.getName());

	public ESBListener() {

		logger.info("Init ESBListener!");

	}

	@Override
	public void onMessage(Message inputMsg) {

		ArrayList<String> dataArray = new ArrayList<String>(6);

		String request = parseMessMQ(inputMsg);

		GetData getData = GetData.getInstance(request);

		try {

			dataArray.add(getData.getValueByName("RqUID"));

			dataArray.add(getData.getValueByName("StatusCode"));

			dataArray.add(getData.getValueByName("ApplicationNumber"));

			if (dataArray.get(2).length() == 0) {

				dataArray.set(2, "null");

				dataArray.add(getData.getValueByName("ErrorCode"));

				dataArray.add(getData.getValueByName("Message"));

			} else {

				String error_code = getData.getValueByName("ErrorCode");

				if (error_code.length() > 0) {

					dataArray.add(error_code);

				} else {

					dataArray.add("0");
				}

				switch (Integer.parseInt(dataArray.get(1))) {

				case -1:
					dataArray.add("Ошибки заполнения");
					break;
				case 0:
					dataArray.add("Отказ");
					break;
				case 1:
					dataArray.add("Заявка создана успешно");
					break;
				case 2:
					dataArray.add("Одобрена");
					break;
				case 3:
					dataArray.add("Обрабатывается");
					break;
				case 4:
					dataArray.add("Кредит Выдан");
					break;
				default:
					break;
				}

			}

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

		}

		try {

			DbOperation.getInstance().evalOperation(2, dataArray);

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

		}

	}

}
