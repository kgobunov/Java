package ru.aplana.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * Specific project methods (project TSM Retail)
 * 
 * @author Maksim Stepanov
 */
public class Common {

	/**
	 * non -instanceable class
	 * 
	 */
	private Common() {
	}


	/**
	 * 
	 * Convert a SOAP message to String
	 * 
	 * @param soapResponse
	 *            - soap message
	 * @return String representation of SOAP message
	 * @author Maksim Stepanov
	 */
	public static String convertSOAPResponse(SOAPMessage soapResponse) {

		ByteArrayOutputStream out = new ByteArrayOutputStream();

		try {

			soapResponse.writeTo(out);

		} catch (SOAPException e) {

			e.printStackTrace();

		} catch (IOException e) {

			e.printStackTrace();

		}

		String resp = new String(out.toString());

		return resp;

	}

	/**
	 * Generating random task_id
	 * 
	 * 
	 * @return String at format 442FBA62-156E-185F-34A0-CD82B7A1F7A9
	 * @author Maksim Stepanov
	 * 
	 */
	public static String generateTaskId() {

		String taskid = "";

		String characters = "QWERTYUIOPASDFGHJKLZXCVBNM";

		Random rng = new Random();

		for (int i = 0; i < 16; i++) {

			int value = (int) (Math.random() * 10);

			taskid += value;

			String ch = String.valueOf(characters.charAt(rng.nextInt(characters
					.length())));

			taskid += ch;

			switch (taskid.length()) {

			case 8:
				taskid += "-";
				break;

			case 13:
				taskid += "-";
				break;

			case 18:
				taskid += "-";
				break;

			case 23:
				taskid += "-";
				break;
			}

		}

		return taskid;
	}

	/**
	 * Generating random number requirement length
	 * 
	 * @param length
	 *            - requirement length
	 * 
	 * @return Random string of numbers. If length = 0 return empty string
	 * @author Maksim Stepanov
	 */
	public static String generateNumber(int length) {

		String id = "";

		for (int i = 0; i < length; i++) {

			int value = (int) (1 + (Math.random() * 9));
			id += value;

		}

		return id;
	}

	/**
	 * Convert MQ message to String
	 * 
	 * @param inputMsg
	 *            - message from MQ
	 * @return string representation MQ message
	 * @autor Timur Laishev
	 */
	public static String parseMessMQ(Message inputMsg) {

		String request = "";

		if (inputMsg instanceof TextMessage) {

			try {

				request = ((TextMessage) inputMsg).getText();

			} catch (JMSException e) {

				e.printStackTrace();
			}

		} else if (inputMsg instanceof BytesMessage) {

			try {

				int l = (int) ((BytesMessage) inputMsg).getBodyLength();

				byte[] buff = new byte[l];

				((BytesMessage) inputMsg).readBytes(buff);

				request = new String(buff);

			} catch (JMSException e) {

				e.printStackTrace();

			}

		}

		return request;
	}

	/**
	 * Generating date of birthday
	 * 
	 * @param yearStart
	 *            - start year, offset - offset relatively yearStart
	 * 
	 * @return Date of birthday at format yyyy-mm-dd
	 * 
	 * @author Maksim Stepanov
	 */
	public static String generateDOB(int yearStart, int offset) {

		String dob = "";

		int valueMonth = (int) (1 + (Math.random() * 12));

		int valueDay = (int) (1 + (Math.random() * 28));

		int valueYear = (int) (yearStart + (Math.random() * offset));

		String year = String.valueOf(valueYear);

		String day = String.valueOf(valueDay);

		String month = String.valueOf(valueMonth);

		if (day.length() == 1) {

			day = "0" + day;

		}

		if (month.length() == 1) {

			month = "0" + month;

		}

		dob = year + "-" + month + "-" + day;

		return dob;

	}

	/**
	 * Generating RQUID
	 * 
	 * @return String of digits with length 32 signs
	 * 
	 * @author Maksim Stepanov
	 */
	public static String generateRqUID() {

		String RqUID = "";

		for (int i = 0; i < 32; i++) {

			int value = (int) (Math.random() * 10);

			RqUID += value;

		}

		return RqUID;

	}

	/**
	 * Generating random Name requirement length
	 * 
	 * @param length
	 *            - length for value
	 * 
	 * @return Random string requirement length
	 * 
	 * @author Maksim Stepanov
	 */
	public static String generateName(int length) {

		Random rng = new Random();

		String characters = null;

		characters = "йцукенгшщзхъэждлорпавыфячсмитьбю";

		char[] text = new char[length];

		for (int i = 0; i < length; i++) {
			text[i] = characters.charAt(rng.nextInt(characters.length()));
		}

		return new String(text);
	}

	/**
	 * 
	 * Convert to translit
	 * 
	 * HashMap<Character, String> relate = new HashMap<Character, String>(40);
	 * 
	 * relate.put('�', "A"); relate.put('�', "B"); relate.put('�', "V");
	 * relate.put('�', "G"); relate.put('�', "D"); relate.put('�', "E");
	 * relate.put('�', "Jo"); relate.put('�', "J"); relate.put('�', "Zh");
	 * relate.put('�', "I"); relate.put('�', "I"); relate.put('�', "K");
	 * relate.put('�', "L"); relate.put('�', "M"); relate.put('�', "N");
	 * relate.put('�', "O"); relate.put('�', "P"); relate.put('�', "R");
	 * relate.put('�', "S"); relate.put('�', "T"); relate.put('�', "U");
	 * relate.put('�', "F"); relate.put('�', "H"); relate.put('�', "Ch");
	 * relate.put('�', "C"); relate.put('�', "Sh"); relate.put('�', "Csh");
	 * relate.put('�', "E"); relate.put('�', "Ju"); relate.put('�', "Ja");
	 * relate.put('�', "Y"); relate.put('�', "`"); relate.put('�', "'");
	 * 
	 * }
	 * 
	 * @param word
	 *            - Russian word
	 * @param relate
	 *            relation
	 * @return Translite representation of Russian word
	 * 
	 */
	public static String convertToTranslite(String russianWord,
			HashMap<String, String> relate) {

		String result = "";

		int length = russianWord.length();

		for (int i = 0; i < length; i++) {

			result += relate.get(russianWord.charAt(i));

		}

		return result;

	}

}
