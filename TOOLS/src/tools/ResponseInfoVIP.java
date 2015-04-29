package tools;

import java.io.UnsupportedEncodingException;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;

import ru.aplana.tools.RandomCreditCardNumberGenerator;

/**
 * 
 * Generating card number
 * 
 * @author Maksim Stepanov
 * 
 */
public class ResponseInfoVIP {

	private OMElement response = null;

	public ResponseInfoVIP() throws UnsupportedEncodingException {

		OMFactory factory = OMAbstractFactory.getOMFactory();

		this.response = factory.createOMElement(new QName("replyInfo"));

		OMElement card = factory.createOMElement(new QName("Card"));

		card.setText(RandomCreditCardNumberGenerator.generateVisaCardNumber());

		this.response.addChild(card);

	}

	public final OMElement getResponse() {

		return this.response;

	}

}
