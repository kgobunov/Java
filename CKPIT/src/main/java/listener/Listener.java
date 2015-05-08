package listener;

import static ru.aplana.tools.Common.parseMessMQ;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.ArrayList;

import javax.jms.Message;
import javax.jms.MessageListener;

import ru.aplana.tools.GetData;
import tools.PropCheck;
import db.DatabaseOperation;

/**
 * Implementation listener for ckpit statuses
 * 
 * @author Maksim Stepanov
 * 
 */
public class Listener implements MessageListener {

	public Listener() {

		loggerInfo.info("Init CKPITListener!");

	}

	@Override
	public void onMessage(Message inputMsg) {

		ArrayList<String> dataArray = new ArrayList<String>(3);

		String request = parseMessMQ(inputMsg);

		if (PropCheck.debug) {

			loggerInfo.info("Message from TSM for CKPIT: " + request);

		}

		GetData getData = GetData.getInstance(request);

		try {

			dataArray.add(getData.getValueByName("UID"));

			dataArray.add(getData.getValueByName("code"));

			dataArray.add(getData.getValueByName("description"));

		} catch (Exception e) {

			loggerSevere.severe(e.getMessage());

			e.printStackTrace();

		}

		try {

			DatabaseOperation.getInstance().evalOperation(2, dataArray);

		} catch (Exception e) {

			loggerSevere.severe(e.getMessage());

			e.printStackTrace();

		}

	}

}
