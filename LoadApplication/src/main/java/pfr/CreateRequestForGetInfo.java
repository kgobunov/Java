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
 * 
 * Classname: CreateRequestForGetInfo
 * 
 * Version: 1.0
 * 
 * 
 * Get info about person
 * 
 * @author Maksim Stepanov
 *
 */
public class CreateRequestForGetInfo {

	private SOAPMessage soapMessage;

	private MessageFactory messageFactory;

	private SOAPPart soapPart;

	private SOAPEnvelope envelope;

	public CreateRequestForGetInfo(ArrayList<String> privateInfo, boolean debug)
			throws SOAPException, IOException {

		this.messageFactory = MessageFactory.newInstance();

		this.soapMessage = messageFactory.createMessage();

		this.soapPart = soapMessage.getSOAPPart();

		String serverURI = "http://aszapros.sberbank.ru/services/Pfr";

		// SOAP Envelope
		this.envelope = soapPart.getEnvelope();

		this.envelope.addNamespaceDeclaration("pfr", serverURI);

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();

		SOAPElement CreateRequestForGetInfo = soapBody.addChildElement(
				"CreateRequestForGetInfo", "pfr");

		SOAPElement statementID = CreateRequestForGetInfo.addChildElement(
				"statementID", "pfr");

		statementID.addTextNode(privateInfo.get(5));

		SOAPElement requestBodyXml = CreateRequestForGetInfo.addChildElement(
				"requestBodyXml", "pfr");

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		Date currentDate = new Date();

		requestBodyXml
				.addTextNode("[<ФайлПФР><ИмяФайла>PFR-700-Y-2008-ORG-087-705-015091-DCK-00000-DPT-000000-DCK-00000.XML</ИмяФайла><ЗаголовокФайла><ВерсияФормата>07.00</ВерсияФормата><ТипФайла>ВНЕШНИЙ</ТипФайла><ПрограммаПодготовкиДанных><НазваниеПрограммы>ОБМЕН ЧЕРЕЗ ПОРТАЛ</НазваниеПрограммы><Версия>02.01</Версия></ПрограммаПодготовкиДанных><ИсточникДанных>ПОРТАЛ</ИсточникДанных></ЗаголовокФайла><ПачкаВходящихДокументов Окружение=\"В составе файла\" Стадия=\"До обработки\"><ВХОДЯЩАЯ_ОПИСЬ><ТипВходящейОписи>ОПИСЬ ПАЧКИ</ТипВходящейОписи><СоставительПачки><НалоговыйНомер><ИНН>7707083839</ИНН><КПП>775001001</КПП></НалоговыйНомер><НаименованиеОрганизации>БАНК СБ РФ</НаименованиеОрганизации><РегистрационныйНомер>087-705-015091</РегистрационныйНомер></СоставительПачки><НомерПачки><Основной>00001</Основной></НомерПачки><СоставДокументов><Количество>1</Количество><НаличиеДокументов><ТипДокумента>ЗАПРОС_В_ПОРТАЛ</ТипДокумента><Количество>1</Количество></НаличиеДокументов></СоставДокументов><ДатаСоставления>"
						+ sdf.format(currentDate)
						+ "</ДатаСоставления></ВХОДЯЩАЯ_ОПИСЬ><ЗАПРОС_В_ПОРТАЛ><СтраховойНомер>"
						+ privateInfo.get(3)
						+ "</СтраховойНомер><ФИО><Фамилия>"
						+ privateInfo.get(1)
						+ "</Фамилия><Имя>"
						+ privateInfo.get(0)
						+ "</Имя><Отчество>"
						+ privateInfo.get(2)
						+ "</Отчество></ФИО><ТипВыписки>РАСШИРЕННАЯ</ТипВыписки><ДатаЗаполнения>"
						+ sdf.format(currentDate)
						+ "</ДатаЗаполнения></ЗАПРОС_В_ПОРТАЛ></ПачкаВходящихДокументов></ФайлПФР>");

		MimeHeaders headers = soapMessage.getMimeHeaders();

		headers.addHeader("SOAPAction",
				"http://aszapros.sberbank.ru/services/Pfr/CreateRequestForGetInfo");

		soapMessage.saveChanges();

		soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,
				"windows-1251");

		String request = Common.convertSOAPResponse(soapMessage);

		if (debug) {

			PFR.loggerInfo.info("Request CreateRequestForGetInfo: " + request);

		}

	}

	public SOAPMessage getMessage() {

		return this.soapMessage;

	}
}
