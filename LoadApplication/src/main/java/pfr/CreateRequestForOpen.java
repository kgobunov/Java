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
 * Classname: CreateRequestForOpen
 * 
 * Version: 1.0
 * 
 * 
 * Request for open statement
 * 
 * @author Maksim Stepanov
 * 
 */
public class CreateRequestForOpen {

	private SOAPMessage soapMessage;

	private MessageFactory messageFactory;

	private SOAPPart soapPart;

	private SOAPEnvelope envelope;

	public CreateRequestForOpen(ArrayList<String> privateInfo, boolean debug)
			throws SOAPException, IOException {

		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");

		SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

		Date currentDate = new Date();

		this.messageFactory = MessageFactory.newInstance();

		this.soapMessage = this.messageFactory.createMessage();

		this.soapPart = this.soapMessage.getSOAPPart();

		String serverURI = "http://aszapros.sberbank.ru/services/Pfr";

		// SOAP Envelope
		this.envelope = this.soapPart.getEnvelope();

		this.envelope.addNamespaceDeclaration("pfr", serverURI);

		// SOAP Body
		SOAPBody soapBody = this.envelope.getBody();

		SOAPElement CreateRequestForOpen = soapBody.addChildElement(
				"CreateRequestForOpen", "pfr");

		SOAPElement requestBodyXml = CreateRequestForOpen.addChildElement(
				"requestBodyXml", "pfr");

		requestBodyXml
				.addTextNode("<ФайлПФР><ИмяФайла>PFR-700-Y-2008-ORG-087-705-015091-DCK-00000-DPT-000000-DCK-00000.XML</ИмяФайла><ЗаголовокФайла><ВерсияФормата>07.00</ВерсияФормата><ТипФайла>ВНЕШНИЙ</ТипФайла><ПрограммаПодготовкиДанных><НазваниеПрограммы>ОБМЕН ЧЕРЕЗ ПОРТАЛ</НазваниеПрограммы><Версия>02.01</Версия></ПрограммаПодготовкиДанных><ИсточникДанных>ПОРТАЛ</ИсточникДанных></ЗаголовокФайла><ПачкаВходящихДокументов Окружение=\"В составе файла\" Стадия=\"До обработки\"><ВХОДЯЩАЯ_ОПИСЬ><ТипВходящейОписи>ОПИСЬ ПАЧКИ</ТипВходящейОписи><СоставительПачки><НалоговыйНомер><ИНН>7707083839</ИНН><КПП>775001001</КПП></НалоговыйНомер><НаименованиеОрганизации>БАНК СБ РФ</НаименованиеОрганизации><РегистрационныйНомер>087-705-015091</РегистрационныйНомер></СоставительПачки><НомерПачки><Основной>00001</Основной></НомерПачки><СоставДокументов><Количество>1</Количество><НаличиеДокументов><ТипДокумента>ЗАЯВЛЕНИЕ_О_РЕГИСТРАЦИИ_В_СИСТЕМЕ_ИНФОРМ_ОБМЕНА</ТипДокумента><Количество>1</Количество></НаличиеДокументов></СоставДокументов><ДатаСоставления>"
						+ sdf2.format(currentDate)
						+ "</ДатаСоставления></ВХОДЯЩАЯ_ОПИСЬ><ЗАЯВЛЕНИЕ_О_РЕГИСТРАЦИИ_В_СИСТЕМЕ_ИНФОРМ_ОБМЕНА><СтраховойНомер>"
						+ privateInfo.get(3)
						+ "</СтраховойНомер><ФИО><Фамилия>"
						+ privateInfo.get(1)
						+ "</Фамилия><Имя>"
						+ privateInfo.get(0)
						+ "</Имя><Отчество>"
						+ privateInfo.get(2)
						+ "</Отчество></ФИО><УдостоверяющийДокумент><ТипУдостоверяющего>ПАСПОРТ РОССИИ</ТипУдостоверяющего><Документ><НаименованиеУдостоверяющего>Паспорт гражданина России</НаименованиеУдостоверяющего><СерияРимскиеЦифры>77</СерияРимскиеЦифры><СерияРусскиеБуквы>99</СерияРусскиеБуквы><НомерУдостоверяющего>003711</НомерУдостоверяющего><ДатаВыдачи>2001-01-23</ДатаВыдачи><КемВыдан>ОВД Провиденского района Чукотского автономного округа</КемВыдан></Документ></УдостоверяющийДокумент><Гражданство>1</Гражданство><АдресРегистрации><ТипАдреса>НЕСТРУКТУРИРОВАННЫЙ</ТипАдреса><Индекс>689000</Индекс><НеструктурированныйАдрес><Адрес>689000, Чукотский, г Анадырь, ул Ленина, д. 32, кв. 38</Адрес></НеструктурированныйАдрес></АдресРегистрации><ИНН/><Организация><НалоговыйНомер><ИНН>7707083839</ИНН><КПП>775001001</КПП></НалоговыйНомер><НаименованиеОрганизации>БАНК СБ РФ</НаименованиеОрганизации><РегистрационныйНомер>087-705-015091</РегистрационныйНомер></Организация><ДатаЗаполнения>"
						+ sdf.format(currentDate)
						+ "</ДатаЗаполнения><ДатаРегистрацииДокумента>"
						+ sdf.format(currentDate)
						+ "</ДатаРегистрацииДокумента><РегистрационныйНомерЗаявления>"
						+ privateInfo.get(4)
						+ "</РегистрационныйНомерЗаявления></ЗАЯВЛЕНИЕ_О_РЕГИСТРАЦИИ_В_СИСТЕМЕ_ИНФОРМ_ОБМЕНА></ПачкаВходящихДокументов></ФайлПФР>");

		MimeHeaders headers = this.soapMessage.getMimeHeaders();

		headers.addHeader("SOAPAction",
				"http://aszapros.sberbank.ru/services/Pfr/CreateRequestForOpen");

		this.soapMessage.saveChanges();

		this.soapMessage.setProperty(SOAPMessage.CHARACTER_SET_ENCODING,
				"windows-1251");

		String request = Common.convertSOAPResponse(this.soapMessage);

		if (debug) {

			PFR.loggerInfo.info("Request CreateRequestForOpen: " + request);

		}

	}

	public SOAPMessage getMessage() {

		return this.soapMessage;

	}

}
