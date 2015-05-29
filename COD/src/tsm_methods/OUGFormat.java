package tsm_methods;



import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Wrapping response for OSGI
 * 
 * @author Maksim Stepanov
 *
 */
public class OUGFormat  {

	public static OMElement createElement (String workflowCall, String busResponse) {
		
		OMFactory factory = OMAbstractFactory.getOMFactory();
		
   	 	OMNamespace ns = factory.createOMNamespace("http://ougwebcomponent.components.oug.osgi.scorex.com/", "oug");	
   	 	
   	 	OMElement ougWS = factory.createOMElement("ougWS", ns);
   	 	
   	 	OMElement workflowName = factory.createOMElement(
   	 			new QName ("workflow_name"));
   	 	
   	 	workflowName.setText(workflowCall);
   	 	
   	 	ougWS.addChild(workflowName);
	 	
   	 	OMElement dataFlux = factory.createOMElement(
   	 			new QName ("data_flux")); 
   	 	
   	 	OMElement item = factory.createOMElement(new QName ("item"));
   	 	
   	 	item.setText("data");
   	 	
   	 	OMElement itemSecond = factory.createOMElement(new QName ("item"));
   	 	
   	 	itemSecond.setText(busResponse);
   	 	
   	 	dataFlux.addChild(item);
   	 	
   	 	dataFlux.addChild(itemSecond);
   	 	
   	 	ougWS.addChild(dataFlux);
		
		return ougWS;
	}
	
}
