package tools;

import org.apache.axiom.om.OMElement;

/**
 * Class gets data from request.
 * 
 * @author Maksim Stepanov
 *
 */
public class RequestData {

	private int type;
	
	public RequestData(OMElement getUrl) {

		
		String type = ((OMElement) getUrl
					  .getChildrenWithLocalName("in").next()).getText();
		
		this.type = Integer.parseInt(type);
		
		
	}

	public int getType() {
		
		return this.type;
		
	}
	

}
