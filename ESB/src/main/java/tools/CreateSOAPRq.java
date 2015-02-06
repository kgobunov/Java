package tools;

import java.util.ArrayList;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

/**
 * Create soap request to web service
 * 
 * @author Maksim Stepanov
 *
 */
@SuppressWarnings("unused")
public class CreateSOAPRq {
	
	private SOAPMessage soapMessage;
	
	private MessageFactory messageFactory;
	
	private SOAPPart soapPart;
	
	private SOAPEnvelope envelope;
	
	private ArrayList<String> dataRq;

	public CreateSOAPRq(ArrayList<String> dataRq) throws Exception {

		this.dataRq = dataRq;
		
		// Data for request
		String surnameText = new String(dataRq.get(0));
		
		String nameText = new String(dataRq.get(1));
		
		String middlenameText = new String(dataRq.get(2));
		
		String birthDateText = new String(dataRq.get(3));
		
		String serialIdText = new String(dataRq.get(4));
		
		String idNumText = new String(dataRq.get(5));
		
		String idIssueDateText = new String(dataRq.get(6));
		
		String messageIdText = new String(dataRq.get(7));
		
		String messageNumberText = new String(dataRq.get(8));
		
		String messageDateText = new String(dataRq.get(9));
		
		this.messageFactory = MessageFactory.newInstance();
		
		this.soapMessage = messageFactory.createMessage();
		
		this.soapPart = soapMessage.getSOAPPart();

		String serverURI = "urn:sap-com:document:sap:soap:functions:mc-style";

		// SOAP Envelope
		this.envelope = this.soapPart.getEnvelope();
		
		this.envelope.addNamespaceDeclaration("urn", serverURI);

		/*
		 * Constructed SOAP Request Message: <soapenv:Envelope
			<soapenv:Envelope xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/" xmlns:urn="urn:sap-com:document:sap:soap:functions:mc-style">
			   <soapenv:Header/>
			   <soapenv:Body>
			      <urn:zsbIntSbolEmployeeRequest>
			         <ClientData>
			            <Surname>Surname</Surname>
			            <Name>Name</Name>
			            <Middlename>Middlename</Middlename>
			            <Birthdate>13.09.1989</Birthdate>
			            <Passseria>4025</Passseria>
			            <Passnum>123456</Passnum>
			            <Passissuedate>05.02.2012</Passissuedate>
			         </ClientData>
			         <MessageData>
			            <Messageid>66666666666</Messageid>
			            <Messagenumber>456</Messagenumber>
			            <Messagedate>10.01.2012</Messagedate>
			         </MessageData>
			      </urn:zsbIntSbolEmployeeRequest>
			   </soapenv:Body>
			</soapenv:Envelope>
		 */

		// SOAP Body
		SOAPBody soapBody = this.envelope.getBody();
		
		SOAPElement zsbIntSbolEmployeeRequest = soapBody.addChildElement("zsbIntSbolEmployeeRequest", "urn");

		// ClientData
		SOAPElement ClientData = zsbIntSbolEmployeeRequest.addChildElement("ClientData");

		SOAPElement Surname = ClientData.addChildElement("Surname");
		
		Surname.addTextNode(surnameText);

		SOAPElement Name = ClientData.addChildElement("Name");
		
		Name.addTextNode(nameText);

		SOAPElement Middlename = ClientData.addChildElement("Middlename");
		
		Middlename.addTextNode(middlenameText);

		SOAPElement Birthdate = ClientData.addChildElement("Birthdate");
		
		Birthdate.addTextNode(birthDateText);

		SOAPElement Passseria = ClientData.addChildElement("Passseria");
		
		Passseria.addTextNode(serialIdText);

		SOAPElement Passnum = ClientData.addChildElement("Passnum");
		
		Passnum.addTextNode(idNumText);

		SOAPElement Passissuedate = ClientData.addChildElement("Passissuedate");
		
		Passissuedate.addTextNode(idIssueDateText);

		// MessageData
		SOAPElement MessageData = zsbIntSbolEmployeeRequest.addChildElement("MessageData");

		SOAPElement Messageid = MessageData.addChildElement("Messageid");
		
		Messageid.addTextNode(messageIdText);

		SOAPElement Messagenumber = MessageData.addChildElement("Messagenumber");
		
		Messagenumber.addTextNode(messageNumberText);

		SOAPElement Messagedate = MessageData.addChildElement("Messagedate");
		
		Messagedate.addTextNode(messageDateText);
		
		MimeHeaders headers = this.soapMessage.getMimeHeaders();
		
		headers.addHeader("SOAPAction", "ZsbIntSbolEmployeeRequest");

		this.soapMessage.saveChanges();
	}
	
	public SOAPMessage getRs() {
		
		return this.soapMessage;
		
	}
}
