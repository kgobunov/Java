package tools;

import org.apache.axiom.om.OMElement;

/**
 * Parse request.
 * 
 * @author Maksim Stepanov
 * 
 */
public class RequestData {

	private OMElement request;

	private RequestData(OMElement req) {

		this.request = req;

	}

	public static RequestData getInstance(OMElement req) {

		return new RequestData(req);

	}

	public final int getType() {

		String typeStr = ((OMElement) this.request.getChildrenWithLocalName(
				"in").next()).getText();

		int type = Integer.parseInt(typeStr);

		return type;

	}

}
