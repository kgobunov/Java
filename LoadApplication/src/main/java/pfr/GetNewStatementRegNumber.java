package pfr;

import java.io.IOException;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import ru.aplana.tools.Common;

/**
 * 
 * Classname: GetNewStatementRegNumber
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * Get statement number
 * 
 * @author Maksim Stepanov
 *
 */

public class GetNewStatementRegNumber {

	private SOAPMessage soapMessage;

	private MessageFactory messageFactory;

	private SOAPPart soapPart;

	private SOAPEnvelope envelope;

	public GetNewStatementRegNumber(boolean debug) throws SOAPException,
			IOException {

		this.messageFactory = MessageFactory.newInstance();

		this.soapMessage = this.messageFactory.createMessage();

		this.soapPart = this.soapMessage.getSOAPPart();

		String serverURI = "http://aszapros.sberbank.ru/services/Pfr";

		// SOAP Envelope
		this.envelope = this.soapPart.getEnvelope();

		this.envelope.addNamespaceDeclaration("NS1", serverURI);

		/*
		 * 
		 * <soapenv:Envelope
		 * xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
		 * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"> <soapenv:Body>
		 * <NS1:GetNewStatementRegNumber
		 * xmlns:NS1="http://aszapros.sberbank.ru/services/Pfr"/>
		 * </soapenv:Body> </soapenv:Envelope>
		 */

		// SOAP Body
		SOAPBody soapBody = this.envelope.getBody();

		soapBody.addChildElement("GetNewStatementRegNumber", "NS1");

		MimeHeaders headers = this.soapMessage.getMimeHeaders();

		headers.addHeader("SOAPAction",
				"http://aszapros.sberbank.ru/services/Pfr/GetNewStatementRegNumber");
		
		this.soapMessage.saveChanges();

		String request = Common.convertSOAPResponse(this.soapMessage);

		if (debug) {

			PFR.loggerInfo.info("Request GetNewStatementRegNumber: " + request);

		}

	}

	public SOAPMessage getMessage() {

		return this.soapMessage;

	}

}
