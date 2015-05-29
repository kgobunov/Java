package tsm_methods;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import static tsm_methods.WebAppContext.flagCheck;
import static tsm_methods.WebAppContext.lq;

/**
 * 
 * Check queue with async request on time for sending to esb
 * 
 * @author Sbt, Maksim Stepanov
 * 
 */
public class ParserStack implements Runnable {

	private static final Logger logger = LogManager
			.getLogger(ParserStack.class.getName());

	public void run() {

		// ���� ��������� ������ ������ � �������

		while (lq.isEmpty()) {

			try {

				Thread.sleep(5000);

			} catch (InterruptedException e) {

				logger.error(e.getMessage(), e);

			}

		}

		RequestTSM rTSM = null;

		long cTime = 0;

		while (flagCheck.get()) {
			// ���� ������� ������ ���� 200 ��
			if (lq.isEmpty()) {

				try {

					Thread.sleep(500);

				} catch (InterruptedException e) {

					logger.error(e.getMessage(), e);

				}

			} else {

				logger.debug("Getting request");

				rTSM = lq.poll();

				cTime = System.currentTimeMillis();

				// ������� ���� ������� ����� ������ �� 100 �� ������� ��������
				// ������� ������, ���� ��������
				if (rTSM.getTimeRequest() > (cTime + 100)) {

					try {

						Thread.sleep(rTSM.getTimeRequest() - cTime);

					} catch (InterruptedException e) {

						logger.error(e.getMessage(), e);
					}
				}

				String tsmId = rTSM.getTsmId();

				MQTools.sendMessage(rTSM.getTextRequest(), tsmId);

				logger.debug("Message put successfully. TSMId: " + tsmId);
			}
		}
	}
}
