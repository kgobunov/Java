package tools;



import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Wrapping bus response to osgi format
 * 
 * @author Maksim Stepanov
 *
 */
public class OUGFormat  {

	/**
	 * 
	 * @param workflowNameText - system name
	 * @param item1Text - bus response
	 * @return
	 */
	public static OMElement createElement (String workflowNameText, String item1Text) {
		
		OMFactory factory = OMAbstractFactory.getOMFactory();
		
   	 	OMNamespace ns = factory.createOMNamespace("http://ougwebcomponent.components.oug.osgi.scorex.com/", "oug");	
   	 	
   	 	OMElement ougWS = factory.createOMElement("ougWS", ns);
   	 	
   	 	OMElement workflow_name = factory.createOMElement(
   	 			new QName ("workflow_name"));
   	 	
   	 	workflow_name.setText(workflowNameText);
   	 	
   	 	ougWS.addChild(workflow_name);
	 	
   	 	OMElement data_flux = factory.createOMElement(
   	 			new QName ("data_flux")); 
   	 	
   	 	OMElement item_0 = factory.createOMElement(new QName ("item"));
   	 	item_0.setText("data");
   	 	
   	 	OMElement item_1 = factory.createOMElement(new QName ("item"));
   	 	
   	 	item_1.setText(item1Text);
   	 	
   	 	data_flux.addChild(item_0);
   	 	
   	 	data_flux.addChild(item_1);
   	 	
   	 	ougWS.addChild(data_flux);
		
		return ougWS;
	}
	
}
