package connections;

import ru.aplana.app.Initialization;

public class AsyncResponseTime implements Runnable {

	private String corrId = null;

	public AsyncResponseTime(String corrId) {

		this.corrId = corrId;

	}

	@Override
	public void run() {

		Initialization.helper.updateCorrIdResp(this.corrId);

	}

}
