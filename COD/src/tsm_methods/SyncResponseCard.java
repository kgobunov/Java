package tsm_methods;

import java.util.ArrayList;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

/**
 * Building sync response to TSM
 * 
 * @author Maksim Stepanov
 *
 */
public class SyncResponseCard {

	private OMElement requestSalaryForCardResponse;

	public SyncResponseCard(ArrayList<String> dataArray) {


		String MessageID_text = dataArray.get(1);

		OMFactory factory = OMAbstractFactory.getOMFactory();
		
		OMNamespace NS1 = factory.createOMNamespace(
				"http://sbrf/ru/neoflex/sbrf/intf/CreditFactoryService", "NS1");

		this.requestSalaryForCardResponse = factory.createOMElement(
				"requestSalaryForCardResponse", NS1);

		OMElement replyInfo = factory.createOMElement(new QName("replyInfo"));

		OMElement code = factory.createOMElement(new QName("code"));
		
		code.setText("0");
		
		replyInfo.addChild(code);

		OMElement description = factory
				.createOMElement(new QName("description"));
		
		description.setText("OK");
		
		replyInfo.addChild(description);
		
		OMElement systemID = factory
				.createOMElement(new QName("systemID"));
		
		systemID.setText("BUS");
		
		replyInfo.addChild(systemID);

		OMElement processID = factory.createOMElement(new QName("processID"));
		
		processID.setText(MessageID_text);
		
		replyInfo.addChild(processID);

		this.requestSalaryForCardResponse.addChild(replyInfo);

	}

	public OMElement get() {

		return this.requestSalaryForCardResponse;
	}

}
