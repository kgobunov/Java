package tool;

import java.io.IOException;

import com.ibm.mq.constants.CMQC;
import com.ibm.mq.constants.MQConstants;
import com.ibm.mq.headers.MQDataException;
import com.ibm.mq.headers.pcf.PCFException;
import com.ibm.mq.headers.pcf.PCFMessage;

import pcf.PCF_ClearQueue;
import pcf.PCF_CommonMethods;
import ru.aplana.app.Main;

public class checkDepth implements Runnable {

	private PCF_CommonMethods pcfCM = null;

	public checkDepth() {

		this.pcfCM = new PCF_CommonMethods();

		try {

			this.pcfCM.CreateAgent();

		} catch (MQDataException e) {

			e.printStackTrace();

		}

	}

	@Override
	public void run() {

		try {

			checkDepthQueue(this.pcfCM);

			if (Main.queuesNames.size() == Main.checkDepth.size()) {

				this.pcfCM.DestroyAgent();

				Main.exec.shutdownNow();

				new PCF_ClearQueue();

				Main.sce.shutdownNow();

				System.exit(0);

			}

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
	private void checkDepthQueue(PCF_CommonMethods pcfCM) throws PCFException,
			MQDataException, IOException {

		PCFMessage pcfCmd = new PCFMessage(MQConstants.MQCMD_INQUIRE_Q);

		pcfCmd.addParameter(CMQC.MQCA_Q_NAME, "ESB.*");

		PCFMessage[] pcfResponse = this.pcfCM.agent.send(pcfCmd);

		for (int index = 0; index < pcfResponse.length; index++) {

			PCFMessage response = pcfResponse[index];

			int depth = Integer.valueOf(String.valueOf(response
					.getParameterValue(MQConstants.MQIA_CURRENT_Q_DEPTH)));

			String name = String.valueOf(response
					.getParameterValue(MQConstants.MQCA_Q_NAME));

			// System.out.println(name + " :" + depth);

			if (depth == 0) {

				Main.checkDepth.put(name, depth);

			}

		}

	}

}
