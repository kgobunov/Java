package tsm_methods;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.axiom.om.OMElement;

/**
 * Parsing incoming request
 * 
 * @author Maksim Stepanov
 *
 */
public class RequestDataCard {
	
	private ArrayList<String> dataArray;

	public RequestDataCard(OMElement requestSalaryForCard) {

		this.dataArray = new ArrayList<String>(4);
		
		@SuppressWarnings("rawtypes")
		Iterator iter = requestSalaryForCard.getChildrenWithLocalName("salaryForCardRequest");
		
		if(iter.hasNext()){
			
			OMElement salaryForCardRequest = (OMElement) iter.next();
			
			this.dataArray.add(((OMElement)salaryForCardRequest.getChildrenWithLocalName("card").next()).getText());
			
			iter = salaryForCardRequest.getChildrenWithLocalName("MessUID");
			
			if ( iter.hasNext() ) {
				
				OMElement MessUID = (OMElement) iter.next();
				this.dataArray.add(((OMElement)MessUID.getChildrenWithLocalName("MessageId").next()).getText());
				this.dataArray.add(((OMElement)MessUID.getChildrenWithLocalName("MessageDate").next()).getText());
			}
			
			iter = salaryForCardRequest.getChildrenWithLocalName("application");
			
			if(iter.hasNext()){
			
				OMElement application = (OMElement) iter.next();
				this.dataArray.add(((OMElement)application.getChildrenWithLocalName("number").next()).getText());
				
							
			}
			
		}
	}

	public ArrayList<String> getArrayList() {

		return this.dataArray;
	}

}
