package tools;

import org.apache.axiom.om.OMElement;

/**
 * Class gets data from request for bad urls
 * 
 * @author Maksim Stepanov
 * 
 */
public class RequestErrorData {

	private int type;

	private String errorServer;

	public RequestErrorData(OMElement getUrl) {

		String type = ((OMElement) getUrl.getChildrenWithLocalName("in").next())
				.getText();

		this.type = Integer.parseInt(new String(type.substring(0, 3)));

		this.errorServer = new String(type.substring(3, type.length()));

	}

	public int getType() {

		return this.type;

	}

	public String getErrorServer() {

		return this.errorServer;

	}

}
