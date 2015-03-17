package listeners;

import static ru.aplana.tools.Common.parseMessMQ;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.ArrayList;

import javax.jms.Message;
import javax.jms.MessageListener;

import ru.aplana.app.CRMMqJms;
import ru.aplana.tools.GetData;
import db.DbOperation;

/**
 * Implementation listener for queue in ESB for CRM status message
 * 
 * @author Maksim Stepanov
 * 
 */
public class ESBListener implements MessageListener {

	public ESBListener() {

		loggerInfo.info("Init ESBListener");

	}

	@Override
	public void onMessage(Message inputMsg) {

		ArrayList<String> dataArray = new ArrayList<String>(5);

		String request = parseMessMQ(inputMsg);

		GetData getData = GetData.getInstance(request);

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

			int status = -99;

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

				if (CRMMqJms.logTsmApp) {

					DbOperation.getInstance().evalOperation(3, dataArray);

				}

			} else {

				DbOperation.getInstance().evalOperation(2, dataArray);
			}

		} catch (Exception e) {

			loggerSevere.severe(e.getMessage());

			e.printStackTrace();

		}

	}

}
