package tools;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

import ru.aplana.app.Initialization;

/**
 * Classname: Validation
 * 
 * Version: 1.0
 * 
 * Copyright: OOO Aplana
 * 
 * 
 * Validating config
 * 
 * @author Maksim Stepanov
 *
 */
public class Validation {

	@SuppressWarnings("static-access")
	public static boolean validating() {

		Source xmlFile = new StreamSource(new File("Settings.xml"));
		
		InputStream in = ClassLoader.getSystemClassLoader().getSystemResourceAsStream("Settings.xsd");
		
		Source xsdFile = new StreamSource(in);

		SchemaFactory schemaFactory = SchemaFactory
				.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		Schema schema = null;

		boolean validFlag = false;

		try {

			schema = schemaFactory.newSchema(xsdFile);

		} catch (SAXException e1) {

			e1.printStackTrace();

		}

		Validator validator = schema.newValidator();

		try {

			validator.validate(xmlFile);

			validFlag = true;

		} catch (SAXException e) {

			Initialization.severe.severe(xmlFile.getSystemId()
					+ " is not valid");

			Initialization.severe.severe("Reason: " + e.getLocalizedMessage());

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (validFlag) {

				Initialization.info.info(xmlFile.getSystemId() + " is valid");

			}

		}

		return validFlag;

	}
}
