package tool;

import java.io.IOException;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;

import pcf.PCF_CommonMethods;
import ru.aplana.app.Main;

public class listQueues {

	private PCF_CommonMethods pcfCM = null;

	public listQueues() {

		this.pcfCM = new PCF_CommonMethods();

		try {

			this.pcfCM.CreateAgent();

			getListQueues(this.pcfCM);

			this.pcfCM.DestroyAgent();

		} catch (Exception e) {

			this.pcfCM.DisplayException(e);

		}

	}

	/**
	 * 
	 * Get names queues
	 * 
	 * @param pcfCM
	 * @throws PCFException
	 * @throws MQDataException
	 * @throws IOException
	 */
	private void getListQueues(PCF_CommonMethods pcfCM) throws PCFException,
			MQDataException, IOException {

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);

		pcfCmd.addParameter(CMQC.MQCA_Q_NAME, "ESB.*");

		PCFMessage[] pcfResponse = this.pcfCM.agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {

			PCFMessage response = pcfResponse[index];

			Main.queuesNames.add(String.valueOf(response
					.getParameterValue(MQConstants.MQCA_Q_NAME)));

		}

	}

}
