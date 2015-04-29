package tools;

import java.io.UnsupportedEncodingException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

/**
 * 
 * Set action for script under0 potreb for some products
 * 
 * @author Maksim Stepanov
 * 
 */
public class ResponseAction {

	public static OMElement getResponse(String product)
			throws UnsupportedEncodingException {

		OMElement response = null;

		OMFactory factory = OMAbstractFactory.getOMFactory();

		response = factory.createOMElement(new QName("replyInfo"));

		OMElement action = factory.createOMElement(new QName("action"));

		String actionNumber = getAction(product);

		action.setText(actionNumber);

		response.addChild(action);

		return response;

	}

	/**
	 * get Action
	 * 
	 * @param action
	 * @return
	 */
	private static String getAction(String action) {

		int intAction = Integer.parseInt(action);

		String number = "";

		switch (intAction) {
		case 1:
			number = WebAppContext.getNext(intAction);
			break;
		case 2:
			number = WebAppContext.getNext(intAction);
			break;
		case 3:
			number = WebAppContext.getNext(intAction);
			break;
		case 4:
			number = WebAppContext.getNext(intAction);
			break;				
		case 5:
			number = WebAppContext.getNext(intAction);
			break;
		case 6:
			number = WebAppContext.getNext(intAction);
			break;
		case 7:
			number = WebAppContext.getNext(intAction);
			break;
		case 8:
			number = WebAppContext.getNext(intAction);
			break;
		case 9:
			number = WebAppContext.getNext(intAction);
			break;
		case 10:
			number = WebAppContext.getNext(intAction);
			break;
		case 11:
			number = WebAppContext.getNext(intAction);
			break;
		case 12:
			number = WebAppContext.getNext(intAction);
			break;
		case 13:
			number = WebAppContext.getNext(intAction);
			break;
		case 14:
			number = WebAppContext.getNext(intAction);
			break;
		case 15:
			number = WebAppContext.getNext(intAction);
			break;
		case 16:
			number = WebAppContext.getNext(intAction);
			break;
		case 17:
			number = WebAppContext.getNext(intAction);
			break;
		case 18:
			number = WebAppContext.getNext(intAction);
			break;
		case 19:
			number = WebAppContext.getNext(intAction);
			break;	
		case 20:
			number = WebAppContext.getNext(intAction);
			break;
		case 21:
			number = WebAppContext.getNext(intAction);
			break;	
		case 22:
			number = WebAppContext.getNext(intAction);
			break;	
		case 23:
			number = WebAppContext.getNext(intAction);
			break;
		case 24:
			number = WebAppContext.getNext(intAction);
			break;
		case 25:
			number = WebAppContext.getNext(intAction);
			break;			
		case 26:
			number = WebAppContext.getNext(intAction);
			break;			
		default:
			break;
		}

		return number;

	}

}
