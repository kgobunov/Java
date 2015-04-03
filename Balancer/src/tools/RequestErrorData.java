package tools;

import org.apache.axiom.om.OMElement;

/**
 * Class gets data from request for bad urls
 * 
 * @author Maksim Stepanov
 * 
 */
public class RequestErrorData {

	private OMElement request;

	private RequestErrorData(OMElement req) {

		this.request = req;

	}

	public static RequestErrorData getInstance(OMElement request) {

		return new RequestErrorData(request);

	}

	public final int getType() {

		int type = Integer.parseInt(((OMElement) this.request
				.getChildrenWithLocalName("type").next()).getText());

		return type;

	}

	public final String getErrorServer() {

		String errorServer = ((OMElement) this.request
				.getChildrenWithLocalName("server").next()).getText();

		return errorServer;

	}

}
