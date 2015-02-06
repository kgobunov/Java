package pfr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

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
 * Classname: CreateOrUpdatePersonWithStatement
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * Create or update statement
 * 
 * @author Maksim Stepanov
 * 
 */
public class CreateOrUpdatePersonWithStatement {

	private SOAPMessage soapMessage;

	private MessageFactory messageFactory;

	private SOAPPart soapPart;

	private SOAPEnvelope envelope;

	public CreateOrUpdatePersonWithStatement(ArrayList<String> privateInfo,
			boolean debug) throws SOAPException, IOException {

		this.messageFactory = MessageFactory.newInstance();

		this.soapMessage = messageFactory.createMessage();

		this.soapPart = soapMessage.getSOAPPart();

		String serverURI = "http://aszapros.sberbank.ru/services/Pfr";

		// SOAP Envelope
		this.envelope = this.soapPart.getEnvelope();

		this.envelope.addNamespaceDeclaration("NS1", serverURI);

		/*
		 * Constructed SOAP Request Message: <soapenv:Envelope
		 * 
		 * 
		 * <soapenv:Envelope
		 * xmlns:soapenc="http://schemas.xmlsoap.org/soap/encoding/"
		 * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
		 * xmlns:xsd="http://www.w3.org/2001/XMLSchema"
		 * xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"> <soapenv:Body>
		 * <NS1:FindPerson xmlns:NS1="http://aszapros.sberbank.ru/services/Pfr">
		 * <
		 * NS1:filterXml><![CDATA[<FindPersonFilter><LastName>�������</LastName>
		 * <FirstName>������</FirstName><MiddleName>��������</MiddleName><
		 * AccountNumber>020-102-726
		 * 61</AccountNumber><ShowClosed>Yes</ShowClosed
		 * ></FindPersonFilter>]]></NS1:filterXml> </NS1:FindPerson>
		 * </soapenv:Body> </soapenv:Envelope>
		 */

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		Date currentDate = new Date();

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();

		SOAPElement CreateOrUpdatePersonWithStatement = soapBody
				.addChildElement("CreateOrUpdatePersonWithStatement", "NS1");

		// ClientData
		SOAPElement personXml = CreateOrUpdatePersonWithStatement
				.addChildElement("personXml", "NS1");

		personXml
				.addTextNode("<Person id=\"-1\"><RegDate>"
						+ sdf.format(currentDate)
						+ "</RegDate><LastName>"
						+ privateInfo.get(1)
						+ "</LastName><FirstName>"
						+ privateInfo.get(0)
						+ "</FirstName><MiddleName>"
						+ privateInfo.get(2)
						+ "</MiddleName><Birthday>01.01.1990</Birthday><Birthplace>Москва</Birthplace><Sex>М</Sex><DocName>Паспорт гражданина России</DocName><DocSeries>9307</DocSeries><DocNumber>335348</DocNumber><DocIssueDate>15.10.2007</DocIssueDate><DocIssueOrg>отделением офмс россии по республике тыва в гор. кызыле</DocIssueOrg><Citizenship>1</Citizenship><RegAddressPostCode>667000</RegAddressPostCode><RegAddress>667000, Тыва Респ, г кызыл, ул безымянная, д. 101</RegAddress><Statements><Statement id=\"-1\"><RegDate>18.04.2014</RegDate><RegNumber>"
						+ privateInfo.get(4)
						+ "</RegNumber><AccountNumber>"
						+ privateInfo.get(3)
						+ "</AccountNumber><Counteragent id=\"1\"><Name>1</Name></Counteragent></Statement></Statements></Person>");

		MimeHeaders headers = soapMessage.getMimeHeaders();

		headers.addHeader("SOAPAction",
				"http://aszapros.sberbank.ru/services/Pfr/CreateOrUpdatePersonWithStatement");

		this.soapMessage.saveChanges();

		this.soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,
				"windows-1251");

		String request = Common.convertSOAPResponse(this.soapMessage);

		if (debug) {

			PFR.loggerInfo.info("Request CreateOrUpdatePersonWithStatement: "
					+ request);

		}

	}

	public SOAPMessage getMessage() {

		return this.soapMessage;

	}

}
