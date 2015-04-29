package tools;

import org.apache.axiom.om.OMElement;

/**
 * 
 * get product
 * 
 * @author Maksim Stepanov
 *
 */
public class ActionParse {
	
	
	public static String getProduct(OMElement getAction) {
		
		String product = "";
	
		product =  ((OMElement)(getAction.getChildrenWithLocalName("product").next())).getText();

		
		return product;
		
	}
	

}
