package tsm_methods;

import java.util.ArrayList;

import org.apache.axiom.om.OMElement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Put request to inside queue for future sending
 * 
 * @author Maksim Stepanov
 * 
 */
public class StubExecutionCard implements Runnable {

	private static final Logger logger = LogManager
			.getLogger(StubExecutionCard.class.getName());

	private ArrayList<String> dataArray;

	private String tsmId = "";

	public StubExecutionCard(ArrayList<String> dataArray) {

		this.dataArray = dataArray;

		this.tsmId = this.dataArray.get(0);

	}

	public void run() {

		logger.debug(WebAppContext.sysName + " Stub] MsgId: " + this.tsmId);

		// генерируем по dataArray (необходимые данные из запроса) item1
		String busResponse = new AsyncResponseCard(this.dataArray).get();

		// создаем для item1 - обертку (формат запроса OUG)
		OMElement ougRequest = OUGFormat.createElement(WebAppContext.sysName,
				busResponse);

		WebAppContext.lq.add(new RequestTSM(ougRequest.toString(),
				WebAppContext.delay, this.tsmId));

	}

}
