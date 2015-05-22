package listener;

import static ru.aplana.tools.Common.parseMessMQ;

import java.util.ArrayList;

import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.aplana.tools.GetData;
import db.DatabaseOperation;

/**
 * Implementation listener for ckpit statuses
 * 
 * @author Maksim Stepanov
 * 
 */
public class Listener implements MessageListener {

	private static final Logger logger = LogManager
			.getFormatterLogger(Listener.class.getName());

	public Listener() {

		logger.info("Init CKPITListener!");

	}

	@Override
	public void onMessage(Message inputMsg) {

		ArrayList<String> dataArray = new ArrayList<String>(3);

		String request = parseMessMQ(inputMsg);

		logger.debug("Message from TSM for CKPIT: %s", request);

		GetData getData = GetData.getInstance(request);

		try {

			dataArray.add(getData.getValueByName("UID"));

			dataArray.add(getData.getValueByName("code"));

			dataArray.add(getData.getValueByName("description"));

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

		}

		try {

			DatabaseOperation.getInstance().evalOperation(2, dataArray);

		} catch (Exception e) {

			logger.error(e.getMessage(), e);

		}

	}

}
