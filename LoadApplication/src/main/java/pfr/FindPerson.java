package pfr;

import java.util.ArrayList;
import java.util.Random;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;


import ru.aplana.tools.Common;

/**
 * 
 * Classname: FindPerson
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * Find person
 * 
 * @author Maksim Stepanov
 * 
 */
public class FindPerson {

	private SOAPMessage soapMessage;

	private MessageFactory messageFactory;

	private SOAPPart soapPart;

	private SOAPEnvelope envelope;

	private String firstname = null;

	private String lastname = null;

	private String middlename = null;

	private String snil = null;

	public FindPerson(String type, boolean debug) throws Exception {

		this.messageFactory = MessageFactory.newInstance();

		this.soapMessage = this.messageFactory.createMessage();

		this.soapPart = this.soapMessage.getSOAPPart();

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

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();

		SOAPElement FindPerson = soapBody.addChildElement("FindPerson", "NS1");

		// ClientData
		SOAPElement filterXml = FindPerson.addChildElement("filterXml", "NS1");

		if (type.equalsIgnoreCase("search")) {

			// generating FIO

			this.firstname = new String(generateFIO(7));

			this.lastname = new String(generateFIO(9));

			this.middlename = new String(generateFIO(8));

			// Generating snils
			this.snil = new String(generateSNILS());

			filterXml
					.addTextNode("<FindPersonFilter><LastName>"
							+ this.lastname
							+ "</LastName><FirstName>"
							+ this.firstname
							+ "</FirstName><MiddleName>"
							+ this.middlename
							+ "</MiddleName><AccountNumber>"
							+ this.snil
							+ "</AccountNumber><ShowClosed>Yes</ShowClosed></FindPersonFilter>");

		} else {

			filterXml.addTextNode("<FindPersonFilter><StatementRegNumber>"
					+ type + "</StatementRegNumber></FindPersonFilter>");

		}

		MimeHeaders headers = this.soapMessage.getMimeHeaders();

		headers.addHeader("SOAPAction",
				"http://aszapros.sberbank.ru/services/Pfr/FindPerson");

		this.soapMessage.saveChanges();

		this.soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,
				"windows-1251");

		String request = Common.convertSOAPResponse(this.soapMessage);

		if (debug) {

			PFR.loggerInfo.info(type + " request FindPerson: " + request);

		}

	}

	public SOAPMessage getMessage() {

		return this.soapMessage;

	}

	/**
	 * add fio to ArayList
	 * 
	 * 
	 * @return
	 */
	public ArrayList<String> getFIO() {

		ArrayList<String> fio = new ArrayList<String>(4);

		fio.add(this.firstname);

		fio.add(this.lastname);

		fio.add(this.middlename);

		fio.add(this.snil);

		return fio;

	}

	/**
	 * 
	 * Generating string for requested length
	 * 
	 * @param length
	 * @return Russian string
	 */
	private String generateFIO(int length) {

		Random rng = new Random();

		String characters = null;

		characters = new String("йцукенгшщзхъэждлорпавыфячсмитьбюё");

		char[] text = new char[length];

		for (int i = 0; i < length; i++) {

			text[i] = characters.charAt(rng.nextInt(characters.length()));

		}
		return new String(text);
	}

	/**
	 * 
	 * Sample - 020-102-726 61
	 * 
	 * Generating snils
	 * 
	 * @return snils
	 */
	private String generateSNILS() {

		String snils = "";

		for (int i = 0; i < 14; i++) {

			int value = (int) (Math.random() * 10);

			switch (i) {
			case 3:
				snils += "-";
				break;
			case 7:
				snils += "-";
				break;
			case 11:
				snils += " ";
				break;
			default:
				snils += value;
				break;
			}

		}

		return snils;

	}

}
