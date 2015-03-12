package requests;

import static ru.aplana.tools.Common.generateDOB;
import static ru.aplana.tools.Common.generateName;
import static ru.aplana.tools.Common.generateNumber;
import static ru.aplana.tools.Common.generateRqUID;
import static tools.PropCheck.common;
import static tools.PropCheck.crm;
import static tools.PropCheck.debug;
import static tools.PropCheck.loggerInfo;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import ru.aplana.app.Request;
import db.DbOperation;

/**
 * Creates CRM app
 * 
 * @author Maksim Stepanov
 * 
 */
public class RqToESB {

	private String response = null;

	private String ki = null;

	private String codeProduct = null;

	private String subProductCode = null;

	private ArrayList<String> dataArray = null;

	public static AtomicInteger count = new AtomicInteger(-1);

	public static AtomicInteger countUser = new AtomicInteger(10);

	// max id for user login
	private int maxUserKI;

	private String[] settings;

	public RqToESB(String[] settings) throws UnsupportedEncodingException {

		if (common.getChildText("testType").equalsIgnoreCase("step")) {

			this.settings = settings;

			this.maxUserKI = Integer.parseInt(this.settings[3]);

		} else {

			this.maxUserKI = Integer.parseInt(crm.getChildText("maxUserKICRM"));

		}

		// generate RqUID
		String RqUID = generateRqUID();

		// set product type
		setProductTypeandKI();

		// generating FIO
		String firstname = new String(generateName(7));

		String lastname = new String(generateName(9));

		// generating ID
		String id = new String(generateNumber(6));

		// generating serial ID
		String serial = new String(generateNumber(4));

		String middlename = new String("срмович");

		String birthday = new String(generateDOB(1975, 6));

		// date get id
		Date current = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String dateTemp = sdf.format(current);

		String dateId = new String(dateTemp);

		this.response = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><ChargeLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\"><MessageData><RqUID>")
				.append(RqUID)
				.append("</RqUID><RqTm>")
				.append(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S")
						.format(new Date()))
				.append("</RqTm><FromAbonent>CRM</FromAbonent></MessageData><Application><SrcObjID>1-12OZ3L</SrcObjID><Unit>99010300000</Unit><LoginKI>")

				.append(this.ki)
				.append("</LoginKI><Product><Type>1</Type><Code>")
				.append(this.codeProduct)
				.append("</Code><SubProductCode>")
				.append(this.subProductCode)
				.append("</SubProductCode><Amount>70000</Amount><Period>60</Period><Currency>RUB</Currency><PaymentType>Annuity</PaymentType><InterestRate>17</InterestRate></Product><Applicant><PersonInfo><PersonName><LastName>")
				.append(lastname)
				.append("</LastName><FirstName>")
				.append(firstname)
				.append("</FirstName><MiddleName>")
				.append(middlename)
				.append("</MiddleName></PersonName><Sex>1</Sex><Birthday>")
				.append(birthday)
				.append("</Birthday><Citizenship>RUSSIA</Citizenship><IdentityCard><IdType>21</IdType><IdSeries>")
				.append(serial)
				.append("</IdSeries><IdNum>")
				.append(id)
				.append("</IdNum><IssuedBy>УФМС Москвы</IssuedBy><IssuedCode>770-079</IssuedCode><IssueDt>")
				.append(dateId)
				.append("</IssueDt><PrevIDInfoFlag>false</PrevIDInfoFlag></IdentityCard></PersonInfo></Applicant></Application> </ChargeLoanApplicationRq>")
				.toString();

		this.dataArray = new ArrayList<String>(5);

		this.dataArray.add(RqUID);

		this.dataArray.add(firstname);

		this.dataArray.add(lastname);

		this.dataArray.add(middlename);

		this.dataArray.add(birthday);

		DbOperation.getInstance().evalOperation(1, this.dataArray);
	}

	public String getRq() {

		return this.response;
	}

	/**
	 * Methods set username for processing application
	 */
	private final void setKI() {

		if (debug) {

			loggerInfo.info("COUNT USERS: " + countUser.get());

		}

		String user = "user_crm" + countUser.get();

		countUser.getAndIncrement();

		// if test type equal step - must up id users
		if (common.getChildText("testType").equalsIgnoreCase("step")) {

			if (Request.flagNewStep) {

				this.maxUserKI = Integer.parseInt(this.settings[3]);

				loggerInfo.info("New max login: " + this.maxUserKI);

				Request.flagNewStep = false;

			}

			if (countUser.get() > this.maxUserKI) {

				countUser.set(10);
			}

		} else {

			if (countUser.get() > this.maxUserKI) {

				countUser.set(10);
			}

		}

		this.ki = user;

	}

	/**
	 * Methods set product type for application
	 */
	private final void setProductTypeandKI() {

		// set potred or doverie

		if (count.get() < 99)

			count.getAndIncrement();

		else {

			loggerInfo.info("Count before reset: " + count.get());

			count.set(0);

			loggerInfo.info("Reset count! Count: " + count.get());
		}

		setKI();

		this.codeProduct = "10";

		double[] dataSecond = { 14.278897, 71.34094, 13.273826, 5.075161,
				9.37503, 27.342836, 27.060368, 79.212006, 93.264565, 88.54575,
				96.91934, 46.04552, 45.633633, 35.08396, 89.28448, 40.562946,
				11.964361, 97.31199, 84.90086, 4.495965, 79.37408, 76.865875,
				19.22438, 85.24698, 25.105642, 14.99539, 43.707916, 14.396053,
				68.682236, 52.545284, 80.051834, 94.63323, 90.51613, 10.982599,
				45.943993, 4.5261283, 27.631319, 57.729015, 42.856888,
				39.44201, 3.1235723, 27.763483, 37.10509, 73.19653, 40.03916,
				21.010075, 46.59571, 88.597855, 12.504898, 70.41968, 13.527823,
				14.995357, 5.187164, 95.89128, 30.713827, 82.22644, 80.49658,
				55.259796, 60.20638, 13.982635, 56.38752, 16.502134, 73.51911,
				98.58811, 19.127964, 13.081752, 19.92754, 87.499, 44.03554,
				6.9642625, 34.128613, 8.491132, 1.8891845, 81.29272, 74.92296,
				47.506763, 63.533676, 18.010973, 88.56361, 93.47754, 75.09555,
				40.867138, 81.98794, 88.13132, 73.2241, 29.216356, 13.316907,
				76.82466, 92.85995, 59.87329, 80.76593, 53.820934, 38.21641,
				21.93942, 44.466827, 79.155174, 38.93509, 73.04887, 63.64793,
				21.292894 };

		if (debug) {

			loggerInfo.info("COUNT Potreb: " + count.get());

		}

		double val = (double) dataSecond[count.get()];

		if (val > 60) {

			this.subProductCode = "9911";

		} else {

			this.subProductCode = "9";

		}

	}
}
