package listener;

import static ru.aplana.tools.Common.parseMessMQ;
import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.util.ArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import ru.aplana.tools.GetData;
import db.DatabaseOperation;

/**
 * Implementation listener for ckpit statuses
 * 
 * @author Maksim Stepanov
 * 
 */
public class Listener implements MessageListener {

	private ArrayList<String> dataArray = null;

	public Listener() {

		loggerInfo.info("Init CKPITListener!");

	}

	@Override
	public void onMessage(Message inputMsg) {

		this.dataArray = new ArrayList<String>(3);

		try {

			String request = parseMessMQ(inputMsg);

			GetData getData = new GetData(request);

			try {

				this.dataArray.add(getData.getValueByName("UID"));

				this.dataArray.add(getData.getValueByName("code"));

				this.dataArray.add(getData.getValueByName("description"));

			} catch (Exception e) {

				loggerSevere.severe(e.getMessage());

				e.printStackTrace();

			}

			try {

				DatabaseOperation.getInstance().evalOperation(2, this.dataArray);

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
