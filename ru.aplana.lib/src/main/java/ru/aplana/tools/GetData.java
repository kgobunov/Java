package ru.aplana.tools;

import java.io.StringReader;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.xml.sax.InputSource;

/**
 * 
 * Getting data from xml by xpath and requirement value (for value: don't count
 * dublicates - return first found )
 * 
 * @author Maksim Stepanov
 * 
 */
public class GetData {

	private final String request;

	private GetData(String request) {

		this.request = request;

	}

	public static GetData getInstance(String request) {

		return new GetData(request);

	}

	/**
	 * Finds value field from xml by name
	 * 
	 * @param name
	 *            the value for search in xml
	 * @return value for requested field
	 * @author Maksim Stepanov
	 * 
	 */
	public String getValueByName(String name) {

		String value = "";

		int i1 = this.request.indexOf("<" + name + ">")
				+ ("<" + name + ">").length();

		int i2 = this.request.indexOf("</" + name + ">", i1);

		if (i1 <= 0 || i2 <= 0 || i1 >= i2) {

			value = "";

		} else {

			value = this.request.substring(i1, i2);
		}

		return value;
	}

	/**
	 * Finds value in xml by xpath expression
	 * 
	 * @param xpathExpr
	 *            - xpath expression
	 * @return value for field
	 * @author Maksim Stepanov
	 * 
	 */
	public String getValueByXpath(String xpathExpr) {

		String value = "";

		String xpath = xpathExpr;

		XPath xPath = XPathFactory.newInstance().newXPath();

		try {

			value = xPath.evaluate(xpath, new InputSource(new StringReader(
					this.request)));

		} catch (XPathExpressionException e) {

			e.printStackTrace();
		}

		return value;

	}

	public String getRequest() {

		return this.request;

	}

	@Override
	public String toString() {

		return new String("Request: " + this.request);

	}

}
