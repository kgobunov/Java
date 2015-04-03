package answers;

import static ru.aplana.tools.Common.generateNumber;
import static ru.aplana.tools.Common.generateTaskId;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import ru.aplana.tools.GetData;
import ru.aplana.tools.RandomCreditCardNumberGenerator;

/**
 * 
 * All requests
 * 
 * Classname: Requests
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * @author Maksim Stepanov
 * 
 */
public class Requests {

	// mod 3 must go to return credit inspector.
	private static AtomicInteger countCalls = new AtomicInteger(0);

	private Requests() {

	}

	public static SOAPMessage getSOAPMessage(ArrayList<String> dataRq) {

		SOAPMessage soapMessage = null;

		MessageFactory messageFactory = null;

		SOAPPart soapPart = null;

		SOAPEnvelope envelope = null;

		// Data for request
		String surnameText = dataRq.get(0);

		String nameText = dataRq.get(1);

		String middlenameText = dataRq.get(2);

		String birthDateText = dataRq.get(3);

		String serialIdText = dataRq.get(4);

		String idNumText = dataRq.get(5);

		String idIssueDateText = dataRq.get(6);

		String messageIdText = dataRq.get(7);

		String messageNumberText = dataRq.get(8);

		String messageDateText = dataRq.get(9);

		try {
			messageFactory = MessageFactory.newInstance();

			soapMessage = messageFactory.createMessage();

			soapPart = soapMessage.getSOAPPart();

			String serverURI = "urn:sap-com:document:sap:soap:functions:mc-style";

			// SOAP Envelope
			envelope = soapPart.getEnvelope();

			envelope.addNamespaceDeclaration("urn", serverURI);

			/*
			 * Constructed SOAP Request Message: <soapenv:Envelope
			 * <soapenv:Envelope
			 * xmlns:soapenv="http://schemas.xmlsoap.org/soap/envelope/"
			 * xmlns:urn="urn:sap-com:document:sap:soap:functions:mc-style">
			 * <soapenv:Header/> <soapenv:Body> <urn:zsbIntSbolEmployeeRequest>
			 * <ClientData> <Surname>Surname</Surname> <Name>Name</Name>
			 * <Middlename>Middlename</Middlename>
			 * <Birthdate>13.09.1989</Birthdate> <Passseria>4025</Passseria>
			 * <Passnum>123456</Passnum>
			 * <Passissuedate>05.02.2012</Passissuedate> </ClientData>
			 * <MessageData> <Messageid>66666666666</Messageid>
			 * <Messagenumber>456</Messagenumber>
			 * <Messagedate>10.01.2012</Messagedate> </MessageData>
			 * </urn:zsbIntSbolEmployeeRequest> </soapenv:Body>
			 * </soapenv:Envelope>
			 */

			// SOAP Body
			SOAPBody soapBody = envelope.getBody();

			SOAPElement zsbIntSbolEmployeeRequest = soapBody.addChildElement(
					"zsbIntSbolEmployeeRequest", "urn");

			// ClientData
			SOAPElement ClientData = zsbIntSbolEmployeeRequest
					.addChildElement("ClientData");

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

			SOAPElement Passissuedate = ClientData
					.addChildElement("Passissuedate");

			Passissuedate.addTextNode(idIssueDateText);

			// MessageData
			SOAPElement MessageData = zsbIntSbolEmployeeRequest
					.addChildElement("MessageData");

			SOAPElement Messageid = MessageData.addChildElement("Messageid");

			Messageid.addTextNode(messageIdText);

			SOAPElement Messagenumber = MessageData
					.addChildElement("Messagenumber");

			Messagenumber.addTextNode(messageNumberText);

			SOAPElement Messagedate = MessageData
					.addChildElement("Messagedate");

			Messagedate.addTextNode(messageDateText);

			MimeHeaders headers = soapMessage.getMimeHeaders();

			headers.addHeader("SOAPAction", "ZsbIntSbolEmployeeRequest");

			soapMessage.saveChanges();

		} catch (SOAPException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return soapMessage;
	}

	public static String getRequestToERIB(ArrayList<String> data) {

		String response = "";

		String responseFinal = "";

		String status = data.get(3);

		if (status.equalsIgnoreCase("-1") || status.equalsIgnoreCase("0")) {

			response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<StatusLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"
					+ "<RqUID>"
					+ data.get(0)
					+ "</RqUID>"
					+ "<OperUID>"
					+ data.get(2)
					+ "</OperUID>"
					+ "<SPName>BP_ES</SPName>"
					+ "<SrcRq>"
					+ "<RqUID>"
					+ data.get(0)
					+ "</RqUID>"
					+ "<RqTm>"
					+ data.get(1)
					+ "</RqTm>"
					+ "</SrcRq>"
					+ "<ApplicationStatus>"
					+ "<Status>"
					+ "<StatusCode>"
					+ status
					+ "</StatusCode>"
					+ "<ApplicationNumber>"
					+ data.get(4)
					+ "</ApplicationNumber>"
					+ "<Error>"
					+ "<ErrorCode>"
					+ data.get(5)
					+ "</ErrorCode>"
					+ "<Message>"
					+ data.get(6)
					+ "</Message>"
					+ "</Error>"
					+ "</Status>"
					+ "</ApplicationStatus>"
					+ "</StatusLoanApplicationRq>";

		} else if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("4")) {

			response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<StatusLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"
					+ "<RqUID>"
					+ data.get(0)
					+ "</RqUID>"
					+ "<RqTm>"
					+ data.get(1)
					+ "</RqTm>"
					+ "<OperUID>"
					+ data.get(2)
					+ "</OperUID>"
					+ "<SPName>BP_ES</SPName>"
					+ "<SrcRq>"
					+ "<RqUID>"
					+ data.get(0)
					+ "</RqUID>"
					+ "<RqTm>"
					+ data.get(1)
					+ "</RqTm>"
					+ "</SrcRq>"
					+ "<ApplicationStatus>"
					+ "<Status>"
					+ "<StatusCode>"
					+ status
					+ "</StatusCode>"
					+ "<ApplicationNumber>"
					+ data.get(4)
					+ "</ApplicationNumber>"
					+ "</Status>"
					+ "</ApplicationStatus>" + "</StatusLoanApplicationRq>";

		} else {

			response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
					+ "<StatusLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"
					+ "<RqUID>"
					+ data.get(0)
					+ "</RqUID>"
					+ "<RqTm>"
					+ data.get(1)
					+ "</RqTm>"
					+ "<OperUID>"
					+ data.get(2)
					+ "</OperUID>"
					+ "<SPName>BP_ES</SPName>"
					+ "<SrcRq>"
					+ "<RqUID>"
					+ data.get(0)
					+ "</RqUID>"
					+ "<RqTm>"
					+ data.get(1)
					+ "</RqTm>"
					+ "</SrcRq>"
					+ "<ApplicationStatus>"
					+ "<Status>"
					+ "<StatusCode>"
					+ status
					+ "</StatusCode>"
					+ "<ApplicationNumber>"
					+ data.get(4)
					+ "</ApplicationNumber>"
					+ "</Status>"
					+ "<Approval>"
					+ "<PeriodM>"
					+ data.get(5)
					+ "</PeriodM>"
					+ "<Amount>"
					+ data.get(6)
					+ "</Amount>"
					+ "<InterestRate>"
					+ data.get(7)
					+ "</InterestRate>"
					+ "</Approval>"
					+ "</ApplicationStatus>" + "</StatusLoanApplicationRq>";

		}

		try {

			responseFinal = new String(response.getBytes(), "UTF-8");

		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}

		return responseFinal;
	}

	public static String getRequestToTSMFromSPOOBK(ArrayList<String> data) {

		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSSSSS");

		Date currentDate = new Date();

		String messageId = null;

		String messageDate = null;

		String fromAbonent = null;

		messageId = new String(data.get(0));

		messageDate = new String(data.get(1));

		fromAbonent = new String(data.get(2));

		String response = "<cap:SrvCardApproveInfoRs xmlns:cap=\"http://CreditFactory/ru/sbrf/sbt/tsm/cardapprove\" xmlns:gen=\"http://CreditFactory/ru/sbrf/sbt/tsm/general\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<MessUID>" + "<MessageId>"
				+ messageId
				+ "</MessageId>"
				+ "<MessageDT>"
				+ messageDate
				+ "</MessageDT>"
				+ "<FromAbonent>"
				+ fromAbonent
				+ "</FromAbonent>"
				+ "</MessUID>"
				+ "<result_info>"
				+ "<code>0</code>"
				+ "<description>OK</description>"
				+ "<systemID>SPOOBK</systemID>"
				+ "</result_info>"
				+ "<business_info>"
				+ "<srcMessageId>"
				+ messageId.substring(6, 15)
				+ "</srcMessageId>"
				+ "<date>"
				+ sdf.format(currentDate)
				+ "</date>"
				+ "<status>0</status>"
				+ "<statusDesc>Ok</statusDesc>"
				+ "</business_info>"
				+ "</cap:SrvCardApproveInfoRs>";

		return response;

	}

	/**
	 * Request to FMS
	 * 
	 * @param data
	 *            - array with data request from ETSM
	 * @param type_request
	 *            - type FMSBookRequest or FMSResultRequest
	 */
	public static String getRequestToTSMFromFMS(ArrayList<String> data,
			String type_request) {

		String response = "";

		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSSSSS");
		Date currentDate = new Date();

		String messageId = null;

		String messageDate = null;

		String fromAbonent = null;

		messageId = new String(data.get(0));

		messageDate = new String(data.get(1));

		fromAbonent = new String(data.get(2));

		if (type_request.equalsIgnoreCase("FMSBookRequest")) {

			// generating taskID
			String taskId = generateTaskId();

			response = "<ns2:FMSBookResponse xmlns:ns2=\"http://CreditFactory/ru/sbrf/sbt/tsm/fms\">"
					+ "<result_info>"
					+ "<code>0</code>"
					+ "<description>OK</description>"
					+ "<systemID>FMS</systemID>"
					+ "</result_info>"
					+ "<general_info>" + "<transactRequestDateTime>"
					+ sdf.format(currentDate)
					+ "</transactRequestDateTime>"
					+ "</general_info>"
					+ "<business_info>"
					+ "<taskId>"
					+ taskId
					+ "</taskId>"
					+ "<fmsId>"
					+ taskId
					+ "</fmsId>"
					+ "</business_info>"
					+ "<MessUID>"
					+ "<MessageId>"
					+ messageId
					+ "</MessageId>"
					+ "<MessageDate>"
					+ messageDate
					+ "</MessageDate>"
					+ "<FromAbonent>"
					+ fromAbonent
					+ "</FromAbonent>"
					+ "</MessUID>"
					+ "</ns2:FMSBookResponse>";

		}

		else {

			response = "<ns2:FMSResultResponse xmlns:ns2=\"http://CreditFactory/ru/sbrf/sbt/tsm/fms\">"
					+ "<result_info>"
					+ "<code>0</code>"
					+ "<description>OK</description>"
					+ "<systemID>FMS</systemID>"
					+ "</result_info>"
					+ "<general_info>"
					+ "<transactRequestDateTime>"
					+ sdf.format(currentDate)
					+ "</transactRequestDateTime>"
					+ "</general_info>"
					+ "<business_info>"
					+ "<TaskResult>300</TaskResult>"
					+ // паспорт действителен
					"</business_info>"
					+ "<MessUID>"
					+ "<MessageId>"
					+ messageId
					+ "</MessageId>"
					+ "<MessageDate>"
					+ messageDate
					+ "</MessageDate>"
					+ "<FromAbonent>"
					+ fromAbonent
					+ "</FromAbonent>"
					+ "</MessUID>"
					+ "</ns2:FMSResultResponse>";

		}

		return response;

	}

	public static String getRequestToSAP(ArrayList<String> data) {

		String response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<ech:SrvSBEmployeeCheckRs xmlns:ech=\"http://CreditFactory/ru/sbrf/sbt/tsm/employeecheck\" xmlns:gen=\"http://CreditFactory/ru/sbrf/sbt/tsm/general\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://CreditFactory/ru/sbrf/sbt/tsm/employeecheck SrvSBEmployeeCkeckRq.xsd \">"
				+ "<ech:SrcMessageData>" + "<MessageId>"
				+ data.get(0)
				+ "</MessageId>"
				+ "<MessageDT>"
				+ data.get(1)
				+ "</MessageDT>"
				+ "<FromAbonent>SAP</FromAbonent>"
				+ "</ech:SrcMessageData>"
				+ // TSMId
				"<ech:TSMId>"
				+ data.get(2)
				+ "</ech:TSMId>"
				+ "<ech:result_info>"
				+ "<code>"
				+ data.get(3)
				+ "</code>"
				+ "<description>"
				+ data.get(4)
				+ "</description>"
				+ "<systemID>"
				+ data.get(5)
				+ "</systemID>"
				+ "</ech:result_info>"
				+ "<ech:business_info>"
				+ "<ech:SberbankEmployeeFlag>true</ech:SberbankEmployeeFlag>"
				+ "<ech:ChartNumber>"
				+ data.get(7)
				+ "</ech:ChartNumber>"
				+ "<ech:StartDate>2012-01-01</ech:StartDate>"
				+ "<ech:EndDate>2015-01-01</ech:EndDate>"
				+ "</ech:business_info>" + "</ech:SrvSBEmployeeCheckRs>";

		return response;

	}

	public static String getRequestToETSM(ArrayList<String> data) {

		String response = "";

		String incoming = "";

		String incomingFamily = "";

		String flat = "";

		String vehicle = "";

		if ((countCalls.getAndIncrement()) % 3 == 0) {

			incoming = "1";

			incomingFamily = "1";

			flat = "false";

			vehicle = "false";

		} else {

			incoming = "150000";

			incomingFamily = "150000";

			flat = "true";

			vehicle = "true";

		}

		// generating ID and serial
		String id = generateNumber(6);

		String serial = generateNumber(4);

		String city = "";

		String street = "";

		String ufms = "";

		String sberFull = "";

		String mother = "";

		String dep = "";

		String pos = "";

		String autoNum = "";

		String firstname = "";

		String lastname = "";

		String middlename = "";

		String birthday = "";

		String dateId = "";

		String cardNum = "";

		String sbflag = "";

		String unit = "";

		String channel = "";

		city = "Москва";

		street = "Ленина";

		ufms = "УФМС Москвы";

		sberFull = "ЗАО Сбербанк";

		mother = "Мама";

		dep = "УВИСАС";

		pos = "Инженер";

		autoNum = "а345вв";

		firstname = data.get(7);

		lastname = data.get(6);

		sbflag = data.get(12);

		middlename = data.get(8);

		birthday = data.get(9);

		dateId = data.get(10);

		unit = data.get(13);

		channel = data.get(14);

		cardNum = RandomCreditCardNumberGenerator.generateVisaCardNumber();

		response = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<ChargeLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplicationTSM.xsd\">"
				+ "<MessageData>" + "<RqUID>"
				+ data.get(0)
				+ "</RqUID>"
				+ "<RqTm>"
				+ data.get(1)
				+ "</RqTm>"
				+ "<FromAbonent>"
				+ data.get(3)
				+ "</FromAbonent>"
				+ "</MessageData>"
				+ "<Application>"
				+ "<SrcObjID>"
				+ data.get(2)
				+ "</SrcObjID>"
				+ "<Unit>"
				+ unit
				+ "</Unit>"
				+ "<CompleteAppFlag>true</CompleteAppFlag>"
				+ "<Channel>"
				+ channel
				+ "</Channel>"
				+ "<ChannelCBRegAApprove>2</ChannelCBRegAApprove>"
				+ "<ChannelPFRRegAApprove>2</ChannelPFRRegAApprove>"
				+ "<Product>"
				+ "<Type>1</Type>"
				+ "<Code>"
				+ data.get(4)
				+ "</Code>"
				+ "<SubProductCode>"
				+ data.get(5)
				+ "</SubProductCode>"
				+ "<Amount>430000</Amount>"
				+ "<Period>24</Period>"
				+ "<Currency>RUB</Currency>"
				+ "<PaymentType>Annuity</PaymentType>"
				+ "<InterestRate>17</InterestRate>"
				+ "</Product>"
				+ "<Applicant>"
				+ "<Type>MainDebitor</Type>"
				+ "<PersonInfo>"
				+ "<PersonName>"
				+ "<LastName>"
				+ lastname
				+ "</LastName>"
				+ "<FirstName>"
				+ firstname
				+ "</FirstName>"
				+ "<MiddleName>"
				+ middlename
				+ "</MiddleName>"
				+ "</PersonName>"
				+ "<NameChangedFlag>false</NameChangedFlag>"
				+ "<Sex>0</Sex>"
				+ "<Birthday>"
				+ birthday
				+ "</Birthday>"
				+ "<BirthPlace>"
				+ city
				+ "</BirthPlace>"
				+ "<Education>"
				+ "<Status>5</Status>"
				+ "<UnfinishedCourse>0</UnfinishedCourse>"
				+ "</Education>"
				+ "<Contact>"
				+ "<EmailAddr>EmailAddr@dddd.ru</EmailAddr>"
				+ "<PhoneList>"
				+ "<Phone>"
				+ "<Type>1</Type>"
				+ "<CountryPrefix>7</CountryPrefix>"
				+ "<Prefix>916</Prefix>"
				+ "<Number>3777441</Number>"
				+ "</Phone>"
				+ "<Phone>"
				+ "<Type>2</Type>"
				+ "<CountryPrefix>7</CountryPrefix>"
				+ "<Prefix>495</Prefix>"
				+ "<Number>2659898</Number>"
				+ "</Phone>"
				+ "<Phone>"
				+ "<Type>3</Type>"
				+ "<CountryPrefix>7</CountryPrefix>"
				+ "<Prefix>495</Prefix>"
				+ "<Number>2659898</Number>"
				+ "</Phone>"
				+ "<Phone>"
				+ "<Type>4</Type>"
				+ "<CountryPrefix>7</CountryPrefix>"
				+ "<Prefix>495</Prefix>"
				+ "<Number>2645698</Number>"
				+ "</Phone>"
				+ "</PhoneList>"
				+ "<AddressList>"
				+ "<ResidenceEqualFlag>true</ResidenceEqualFlag>"
				+ "<CityResidencePeriod>10</CityResidencePeriod>"
				+ "<ActResidencePeriod>10</ActResidencePeriod>"
				+ "<ResidenceRight>0</ResidenceRight>"
				+ "<Address>"
				+ "<ManualInputFlag>false</ManualInputFlag>"
				+ "<AddrType>1</AddrType>"
				+ "<PostalCode>143350</PostalCode>"
				+ "<RegionCode>0077</RegionCode>"
				+ "<Area></Area>"
				+ "<CityType>301</CityType>"
				+ "<City>"
				+ city
				+ "</City>"
				+ "<SettlementType>495</SettlementType>"
				+ "<Settlement>"
				+ city
				+ "</Settlement>"
				+ "<StreetType>518</StreetType>"
				+ "<Street>"
				+ street
				+ "</Street>"
				+ "<HouseNum>1</HouseNum>"
				+ "<HouseExt>1</HouseExt>"
				+ "<Unit></Unit>"
				+ "<UnitNum>23</UnitNum>"
				+ "</Address>"
				+ "<Address>"
				+ "<ManualInputFlag>false</ManualInputFlag>"
				+ "<AddrType>2</AddrType>"
				+ "<PostalCode>143350</PostalCode>"
				+ "<RegionCode>0077</RegionCode>"
				+ "<Area></Area>"
				+ "<CityType>301</CityType>"
				+ "<City>"
				+ city
				+ "</City>"
				+ "<SettlementType>495</SettlementType>"
				+ "<Settlement>"
				+ city
				+ "</Settlement>"
				+ "<StreetType>518</StreetType>"
				+ "<Street>"
				+ street
				+ "</Street>"
				+ "<HouseNum>1</HouseNum>"
				+ "<HouseExt>1</HouseExt>"
				+ "<Unit></Unit>"
				+ "<UnitNum>23</UnitNum>"
				+ "</Address>"
				+ "</AddressList>"
				+ "</Contact>"
				+ "<Citizenship>RUSSIA</Citizenship>"
				+ "<IdentityCard>"
				+ "<IdType>21</IdType>"
				+ "<IdSeries>"
				+ serial
				+ "</IdSeries>"
				+ "<IdNum>"
				+ id
				+ "</IdNum>"
				+ "<IssuedBy>"
				+ ufms
				+ "</IssuedBy>"
				+ "<IssuedCode>770-079</IssuedCode>"
				+ "<IssueDt>"
				+ dateId
				+ "</IssueDt>"
				+ "<PrevIDInfoFlag>false</PrevIDInfoFlag>"
				+ "</IdentityCard>"
				+ "<ExtPassportExFlag>true</ExtPassportExFlag>"
				+ "</PersonInfo>"
				+ "<MaritalCondition>"
				+ "<Status>0</Status>"
				+ "<MarriageContractFlag>false</MarriageContractFlag>"
				+ "<ChildrenFlag>false</ChildrenFlag>"
				+ "</MaritalCondition>"
				+ "<RelativeList>"
				+ "<Relative>"
				+ "<Type>1</Type>"
				+ "<PersonName>"
				+ "<LastName>"
				+ mother
				+ "</LastName>"
				+ "<FirstName>"
				+ mother
				+ "</FirstName>"
				+ "<MiddleName>"
				+ mother
				+ "</MiddleName>"
				+ "</PersonName>"
				+ "<Birthday>1968-05-04</Birthday>"
				+ "<DependentFlag>false</DependentFlag>"
				+ "<SBCreditFlag>0</SBCreditFlag>"
				+ "<SBEmployeeFlag>false</SBEmployeeFlag>"
				+ "</Relative>"
				+ "</RelativeList>"
				+ "<EmploymentHistory>"
				+ "<Status>2</Status>"
				+ "<OrgInfo>"
				+ "<OrgCode>001</OrgCode>"
				+ "</OrgInfo>"
				+ "<OrgInfoExt>"
				+ "<FullName>"
				+ sberFull
				+ "</FullName>"
				+ "<TaxId>2448352136</TaxId>"
				+ "<IndustSector>"
				+ "<Code>1</Code>"
				+ "</IndustSector>"
				+ "<NumEmployeesCode>5</NumEmployeesCode>"
				+ "</OrgInfoExt>"
				+ "<SBEmployeeFlag>"
				+ sbflag
				+ "</SBEmployeeFlag>"
				+ "<SBEmployee>"
				+ "<RegionId>99</RegionId>"
				+ "<FullName>"
				+ dep
				+ "</FullName>"
				+ "<JobType>1</JobType>"
				+ "</SBEmployee>"
				+ "<EmployeeInfo>"
				+ "<JobType>1</JobType>"
				+ "<JobTitle>"
				+ pos
				+ "</JobTitle>"
				+ "<ExperienceCode>5</ExperienceCode>"
				+ "<WorkPlacesNum>0</WorkPlacesNum>"
				+ "</EmployeeInfo>"
				+ "</EmploymentHistory>"
				+ "<Income>"
				+ "<BasicIncome6M>"
				+ incoming
				+ "</BasicIncome6M>"
				+ "<AddIncome6M>0.0</AddIncome6M>"
				+ "<FamilyIncome6M>"
				+ incomingFamily
				+ "</FamilyIncome6M>"
				+ "<Expenses6M>0.0</Expenses6M>"
				+ "</Income>"
				+ "<RealEstateFlag>"
				+ flat
				+ "</RealEstateFlag>"
				+ "<RealEstateList>"
				+ "<RealEstate>"
				+ "<Type>2</Type>"
				+ "<Address>"
				+ street
				+ "</Address>"
				+ "<PurchaseYear>2005</PurchaseYear>"
				+ "<Area>100.00</Area>"
				+ "<Units>1</Units>"
				+ "<Cost>100000.00</Cost>"
				+ "</RealEstate>"
				+ "</RealEstateList>"
				+ "<VehicleFlag>"
				+ vehicle
				+ "</VehicleFlag>"
				+ "<VehicleList>"
				+ "<Vehicle>"
				+ "<Type>1</Type>"
				+ "<RegNumber>"
				+ autoNum
				+ "</RegNumber>"
				+ "<PurchaseYear>2013</PurchaseYear>"
				+ "<BrandName>Audi</BrandName>"
				+ "<AgeInYears>1</AgeInYears>"
				+ "<Cost>50000.00</Cost>"
				+ "</Vehicle>"
				+ "</VehicleList>"
				+ "<LoanFlag>false</LoanFlag>"
				+ "<AddData>"
				+ "<LoanIssue>"
				+ "<Type>3</Type>"
				+ "<AcctId></AcctId>"
				+ "<CardNum>"
				+ cardNum
				+ "</CardNum>"
				+ "</LoanIssue>"
				+ "<InsuranceFlag>false</InsuranceFlag>"
				+ "<CBReqApprovalFlag>true</CBReqApprovalFlag>"
				+ "<CBSendApprovalFlag>true</CBSendApprovalFlag>"
				+ "<CBCode>CBCode0</CBCode>"
				+ "<PFRReqApprovalFlag>true</PFRReqApprovalFlag>"
				+ "<InsuranseNumber>123-123-123 12</InsuranseNumber>"
				+ "<CCardGetApprovalFlag>false</CCardGetApprovalFlag>"
				+ "<SpecialAttentionFlag>false</SpecialAttentionFlag>"
				+ "<SBSalaryCardFlag>true</SBSalaryCardFlag>"
				+ "<SalaryCardList>"
				+ "<SalaryCard>"
				+ "<Type>0</Type>"
				+ "<CardNum>"
				+ cardNum
				+ "</CardNum>"
				+ "</SalaryCard>"
				+ "</SalaryCardList>"
				+ "<SBSalaryDepFlag>false</SBSalaryDepFlag>"
				+ "<SBShareHolderFlag>false</SBShareHolderFlag>"
				+ "<PersonVerify>"
				+ "<VerifyQuestionInfo>"
				+ "<VerifyQuestionNumber>1</VerifyQuestionNumber>"
				+ "<VerifyQuestionText>test</VerifyQuestionText>"
				+ "<VerifyQuestionAnswer>test</VerifyQuestionAnswer>"
				+ "</VerifyQuestionInfo>"
				+ "</PersonVerify>"
				+ "<SigningDate>"
				+ dateId
				+ "</SigningDate>"
				+ "</AddData>"
				+ "</Applicant>"
				+ "</Application>" + "</ChargeLoanApplicationRq>";

		return response;

	}

	public static String mdmResponse(String request) {

		String response = "";

		GetData xmlData = GetData.getInstance(request);

		String rqUID = xmlData.getValueByName("RqUID");

		String lastName = xmlData.getValueByName("LastName");

		String firstName = xmlData.getValueByName("FirstName");

		String middleName = xmlData.getValueByName("MiddleName");

		String birthDay = xmlData.getValueByName("Birthday");

		String gender = xmlData.getValueByName("Gender");

		String idType = xmlData.getValueByName("IdType");

		String idSeries = xmlData.getValueByName("IdSeries");

		String idNum = xmlData.getValueByName("IdNum");

		@SuppressWarnings("unused")
		String searchType = xmlData.getValueByName("SearchType");

		StringBuilder sb = new StringBuilder();

		sb.append("<CustInqRs>").append("<Status>").append("<StatusCode>")
				.append("0").append("</StatusCode>").append("<Severity>")
				.append("Info").append("</Severity>").append("</Status>")
				.append("<RqUID>").append(rqUID).append("</RqUID>")
				.append("<CustRec>").append("<CustId>").append("<SPName>")
				.append("MDM").append("</SPName>").append("<CustPermId>")
				.append("113407506868642424").append("</CustPermId>")
				.append("</CustId>").append("<CustInfo>")
				.append("<PersonInfo>").append("<PersonName>")
				.append("<LastName>").append(lastName).append("</LastName>")
				.append("<FirstName>").append(firstName).append("</FirstName>")
				.append("<MiddleName>").append(middleName)
				.append("</MiddleName>").append("</PersonName>")
				.append("<Gender>").append(gender).append("</Gender>")
				.append("<Birthday>").append(birthDay).append("</Birthday>")
				.append("<IdentityCard>").append("<IdType>").append(idType)
				.append("</IdType>").append("<IdSeries>").append(idSeries)
				.append("</IdSeries>").append("<IdNum>").append(idNum)
				.append("</IdNum>").append("<IdStatus>").append("1")
				.append("</IdStatus>").append("</IdentityCard>")
				.append("</PersonInfo>").append("</CustInfo>")
				.append("</CustRec>").append("</CustInqRs>");

		response = sb.toString();

		return response;

	}
}
