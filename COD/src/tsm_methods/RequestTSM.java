package tsm_methods;

/**
 * 
 * Holder for async request to esb
 * 
 * @author Sbt, Maksim Stepanov
 * 
 */
public class RequestTSM {

	private String textRequest;

	private long timeRequest;

	private String tsmId;

	public RequestTSM(String textRequest, long pause, String tsmId) {

		this.textRequest = textRequest;

		this.timeRequest = System.currentTimeMillis() + pause;

		this.tsmId = tsmId;
	}

	public String getTextRequest() {

		return this.textRequest;

	}

	public String getTsmId() {

		return this.tsmId;

	}

	public void setTsmId(String tsmId) {

		this.tsmId = tsmId;

	}

	public void setTextRequest(String textRequest) {

		this.textRequest = textRequest;

	}

	public long getTimeRequest() {

		return this.timeRequest;

	}

	public void setTimeRequest(long timeRequest) {

		this.timeRequest = timeRequest;

	}

}
