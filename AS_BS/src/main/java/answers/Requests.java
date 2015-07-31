package answers;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import ru.aplana.tools.GetData;
import tools.PropCheck;
import db.Saver;

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

	private static final Logger logger = LogManager
			.getFormatterLogger(Requests.class.getName());

	private static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss");

	private static SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");

	private Requests() {

		logger.info("Init " + Requests.class.getName());

	}

	private static String getRoot(String request) {

		XPath xPath = XPathFactory.newInstance().newXPath();

		String value = null;

		try {

			value = xPath.evaluate("name(*)", new InputSource(new StringReader(
					request)));

		} catch (XPathExpressionException e) {

			logger.error(e.getMessage(), e);

		} finally {

			xPath = null;

		}

		return value;

	}

	public static String getResponse(String request) {

		String response = "";

		GetData xmlData = GetData.getInstance(request);

		String root = getRoot(request);

		switch (root) {

		case "CalcInsurancePremiumRq":

			response = calcInsurance(xmlData);
			break;

		case "CreateInsuranceRq":

			response = createInsurance(xmlData);
			break;

		default:
			break;
		}

		return response;

	}

	/**
	 * 
	 * Calculating insurance amount
	 * 
	 * @param xmlData
	 * @return
	 */
	private static String calcInsurance(GetData xmlData) {

		String messageId = xmlData.getValueByName("MessageId");

		String appNum = Integer.valueOf(messageId.substring(0, 20)).toString();

		String creditAmount = xmlData.getValueByName("CreditAmount");

		StringBuilder sb = new StringBuilder();

		Date current = new Date();

		String dateTime = sdf.format(current);

		String insurancePremium = String.valueOf(Double
				.parseDouble(creditAmount) * 0.1);

		sb.append("<CalcInsurancePremiumRs>").append("<result_info>")
				.append("<code>").append("0").append("</code>")
				.append("<description>").append("Ok").append("</description>")
				.append("</result_info>").append("<business_info>")
				.append("<InsurancePremium>").append(insurancePremium)
				.append("</InsurancePremium>").append("<InsuranceAmount>")
				.append("1000000.0").append("</InsuranceAmount>")
				.append("<CreditAmount>").append(creditAmount)
				.append("</CreditAmount>").append("<InsUpdDtTm>")
				.append(dateTime).append("</InsUpdDtTm>")
				.append("<InsuranceFromDate>").append(sdfDate.format(current))
				.append("</InsuranceFromDate>").append("</business_info>")
				.append("<MessUID>").append("<MessageId>").append(messageId)
				.append("</MessageId>").append("<MessageDate>")
				.append(dateTime).append("</MessageDate>")
				.append("<FromAbonent>").append("АС БС")
				.append("</FromAbonent>").append("<ToAbonent>").append("TSM")
				.append("</ToAbonent>").append("</MessUID>")
				.append("</CalcInsurancePremiumRs>");

		ArrayList<String> data = new ArrayList<String>();

		data.add(messageId);

		data.add(appNum);

		data.add(creditAmount);

		data.add(insurancePremium);

		data.add("1000000.0");

		PropCheck.cacheSaverPool.execute(new Saver("calculated", data));

		return sb.toString();

	}

	/**
	 * 
	 * Calculating insurance amount
	 * 
	 * @param xmlData
	 * @return
	 */
	private static String createInsurance(GetData xmlData) {

		String messageId = xmlData.getValueByName("MessageId");

		String appNum = xmlData.getValueByName("number");

		String creditAmount = xmlData.getValueByName("CreditAmount");

		String insuranceAmount = "1000000.0";

		String insurancePremium = String.valueOf(Double
				.parseDouble(creditAmount) * 0.1);

		StringBuilder sb = new StringBuilder();

		Date current = new Date();

		String dateTime = sdf.format(current);

		sb.append("<CreateInsuranceRs>").append("<result_info>")
				.append("<code>").append("0").append("</code>")
				.append("<description>").append("Ok").append("</description>")
				.append("</result_info>").append("<business_info>")
				.append("<InsurancePremium>").append(insurancePremium)
				.append("</InsurancePremium>").append("<InsuranceAmount>")
				.append(insuranceAmount).append("</InsuranceAmount>")
				.append("<CreditAmount>").append(creditAmount)
				.append("</CreditAmount>").append("<InsUpdDtTm>")
				.append(dateTime).append("</InsUpdDtTm>")
				.append("<InsuranceFromDate>").append(sdfDate.format(current))
				.append("</InsuranceFromDate>").append("</business_info>")
				.append("<MessUID>").append("<MessageId>").append(messageId)
				.append("</MessageId>").append("<MessageDate>")
				.append(dateTime).append("</MessageDate>")
				.append("<FromAbonent>").append("АС БС")
				.append("</FromAbonent>").append("<ToAbonent>").append("TSM")
				.append("</ToAbonent>").append("</MessUID>")
				.append("</CreateInsuranceRs>");

		ArrayList<String> data = new ArrayList<String>();

		data.add(messageId);

		data.add(appNum);

		data.add(creditAmount);

		data.add(insurancePremium);

		PropCheck.cacheSaverPool.execute(new Saver("creation", data));

		return sb.toString();

	}
}
