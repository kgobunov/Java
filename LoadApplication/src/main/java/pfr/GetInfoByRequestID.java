package pfr;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import ru.aplana.tools.Common;

/**
 * 
 * Classname: GetInfoByRequestID
 * 
 * Version: 1.0
 * 
 *  
 * Get info by ID
 * 
 * @author Maksim Stepanov
 * 
 */
public class GetInfoByRequestID {

	private SOAPMessage soapMessage;

	private MessageFactory messageFactory;

	private SOAPPart soapPart;

	private SOAPEnvelope envelope;

	public GetInfoByRequestID(ArrayList<String> privateInfo, boolean debug)
			throws SOAPException, IOException {

		this.messageFactory = MessageFactory.newInstance();

		this.soapMessage = this.messageFactory.createMessage();

		this.soapPart = this.soapMessage.getSOAPPart();

		String serverURI = "http://aszapros.sberbank.ru/services/Pfr";

		// SOAP Envelope
		this.envelope = this.soapPart.getEnvelope();

		this.envelope.addNamespaceDeclaration("pfr", serverURI);

		// SOAP Body
		SOAPBody soapBody = this.envelope.getBody();

		SOAPElement GetInfoByRequestID = soapBody.addChildElement(
				"GetInfoByRequestID", "pfr");

		// ClientData
		SOAPElement requestID = GetInfoByRequestID.addChildElement("requestID",
				"pfr");

		requestID.addTextNode(privateInfo.get(5));

		MimeHeaders headers = this.soapMessage.getMimeHeaders();

		headers.addHeader("SOAPAction",
				"http://aszapros.sberbank.ru/services/Pfr/GetInfoByRequestID");

		this.soapMessage.saveChanges();

		// soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,
		// "windows-1251");
		String request = Common.convertSOAPResponse(this.soapMessage);

		if (debug) {

			PFR.loggerInfo.info("Request GetInfoByRequestID: " + request);

		}

	}

	public SOAPMessage getMessage() {

		return this.soapMessage;

	}

}
