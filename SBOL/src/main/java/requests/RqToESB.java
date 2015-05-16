package requests;

import static ru.aplana.tools.Common.generateDOB;
import static ru.aplana.tools.Common.generateName;
import static ru.aplana.tools.Common.generateNumber;
import static ru.aplana.tools.Common.generateRqUID;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.DbOperation;

/**
 * Creates ERIB app
 * 
 * @author Maksim Stepanov
 * 
 */
public class RqToESB {

	private static AtomicInteger count = new AtomicInteger(-1);

	private static final Logger logger = LogManager
			.getFormatterLogger(RqToESB.class.getName());

	private String response = "";

	private boolean sbflag;

	private String codeProduct = "";

	private String subProductCode = "";

	private String firstname = "";

	private RqToESB() {

		// generate RqUID
		String RqUID = generateRqUID();

		String operUID = generateNumber(18);

		// set product type
		setProductTypeandSBFlag();

		// generating FIO

		String lastname = generateName(9);

		// ЗНИ если клиент банка , то кредит не срочный
		// this.sbflag = false;
		// this.codeProduct = "5";
		// this.subProductCode = "1";
		// this.firstname = new String(generateName(2) + "оферта");

		String middlename = "";

		if ((this.sbflag == false) && (this.codeProduct.equalsIgnoreCase("10"))) {

			middlename = "сболыч";

		} else if ((this.sbflag == true)
				&& (this.codeProduct.equalsIgnoreCase("10"))) {

			middlename = "клиентбанка";

		} else {

			middlename = "сболик";

		}

		String birthday = generateDOB(1975, 15);

		// date get id
		Date current = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String dateTemp = sdf.format(current);

		String dateId = dateTemp;

		this.response = new StringBuilder(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><ChargeLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\"><RqUID>")
				.append("")
				.append(RqUID)
				.append("</RqUID><RqTm>")
				.append(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S")
						.format(new Date()))
				.append("</RqTm>")
				.append("<OperUID>")
				.append(operUID)
				.append("</OperUID>")
				.append("<FromAbonent>SBOL</FromAbonent><Application><Unit>99010300000</Unit><Channel>08</Channel><Product><Type>1</Type><Code>")
				.append(this.codeProduct)
				.append("</Code><SubProductCode>")
				.append(this.subProductCode)
				.append("</SubProductCode><Amount>700000</Amount><Period>60</Period><Currency>RUB</Currency><PaymentType>Annuity</PaymentType></Product><Applicant><Type>MainDebitor</Type><PersonInfo><PersonName><LastName>")
				.append(lastname)
				.append("</LastName><FirstName>")
				.append(this.firstname)
				.append("</FirstName><MiddleName>")
				.append(middlename)
				.append("</MiddleName></PersonName><NameChangedFlag>false</NameChangedFlag><Sex>0</Sex><Birthday>")
				.append(birthday)
				.append("</Birthday><BirthPlace>Москва</BirthPlace><Education><Status>5</Status><UnfinishedCourse>0</UnfinishedCourse></Education><Contact><EmailAddr>EmailAddr@dddd.ru</EmailAddr><PhoneList><Phone><Type>1</Type><CountryPrefix>7</CountryPrefix><Prefix>916</Prefix><Number>3777441</Number></Phone><Phone><Type>2</Type><CountryPrefix>7</CountryPrefix><Prefix>495</Prefix><Number>2659898</Number></Phone><Phone><Type>3</Type><CountryPrefix>7</CountryPrefix><Prefix>495</Prefix><Number>2659898</Number></Phone><Phone><Type>4</Type><CountryPrefix>7</CountryPrefix><Prefix>495</Prefix><Number>2645698</Number></Phone></PhoneList><AddressList><ResidenceEqualFlag>true</ResidenceEqualFlag><CityResidencePeriod>10</CityResidencePeriod><ActResidencePeriod>10</ActResidencePeriod><ResidenceRight>0</ResidenceRight><Address><ManualInputFlag>false</ManualInputFlag><AddrType>1</AddrType><PostalCode>109507</PostalCode><RegionCode>0077</RegionCode><AreaType></AreaType><Area>Area</Area><CityType>город</CityType><City>Москва</City><SettlementType>495</SettlementType><Settlement>Москва</Settlement><StreetType>518</StreetType><Street>Маяковская</Street><HouseNum>10</HouseNum><HouseExt>1</HouseExt><Unit></Unit><UnitNum>13</UnitNum></Address></AddressList></Contact><Citizenship>RUSSIA</Citizenship><IdentityCard><IdType>21</IdType><IdSeries>4623</IdSeries><IdNum>936534</IdNum><IssuedBy>УФМС Москвы</IssuedBy><IssuedCode>770-079</IssuedCode><IssueDt>")
				.append(dateId)
				.append("</IssueDt><PrevIDInfoFlag>false</PrevIDInfoFlag></IdentityCard><ExtPassportExFlag>true</ExtPassportExFlag></PersonInfo><MaritalCondition><Status>0</Status><MarriageContractFlag>false</MarriageContractFlag><ChildrenFlag>false</ChildrenFlag></MaritalCondition><RelativeList><Relative><Type>1</Type><PersonName><LastName>мама</LastName><FirstName>мама</FirstName><MiddleName>мама</MiddleName></PersonName><Birthday>1968-05-04</Birthday><DependentFlag>false</DependentFlag><SBCreditFlag>0</SBCreditFlag><SBEmployeeFlag>false</SBEmployeeFlag></Relative></RelativeList><EmploymentHistory><Status>2</Status><OrgInfo><Name>сбербанк</Name><OrgCode>001</OrgCode></OrgInfo><OrgInfoExt><FullName>ЗАО Сбербанк</FullName><TaxId>77347878</TaxId><IndustSector><Code>1</Code></IndustSector><NumEmployeesCode>1000</NumEmployeesCode></OrgInfoExt><SBEmployeeFlag>")
				.append(this.sbflag)
				.append("</SBEmployeeFlag><SBEmployee><RegionId>99</RegionId><FullName>УВИСАС</FullName><JobType>1</JobType></SBEmployee><EmployeeInfo><JobType>1</JobType><JobTitle>начальник</JobTitle><ExperienceCode>5</ExperienceCode><WorkPlacesNum>0</WorkPlacesNum></EmployeeInfo></EmploymentHistory><Income><BasicIncome6M>150000</BasicIncome6M><AddIncome6M>0.0</AddIncome6M><FamilyIncome6M>150000</FamilyIncome6M><Expenses6M>50000</Expenses6M></Income><RealEstateFlag>true</RealEstateFlag><RealEstateList><RealEstate><Type>2</Type><Address>Ленина</Address><PurchaseYear>2005</PurchaseYear><Area>100.00</Area><Units>1</Units><Cost>100000.00</Cost></RealEstate></RealEstateList><VehicleFlag>true</VehicleFlag><VehicleList><Vehicle><Type>1</Type><RegNumber>E881KH99RUS</RegNumber><PurchaseYear>2013</PurchaseYear><BrandName>Audi</BrandName><AgeInYears>1</AgeInYears><Cost>50000.00</Cost></Vehicle></VehicleList><LoanFlag>false</LoanFlag><AddData><LoanIssue><Type>1</Type><AcctId>AcctId0</AcctId><CardNum>1234598765986214</CardNum></LoanIssue><InsuranceFlag>false</InsuranceFlag><InsuranseNumber>000-000-000 00</InsuranseNumber><CBReqApprovalFlag>true</CBReqApprovalFlag><CBSendApprovalFlag>true</CBSendApprovalFlag><PFRReqApprovalFlag>true</PFRReqApprovalFlag><CBCode>CBCode0</CBCode><CCardGetApprovalFlag>false</CCardGetApprovalFlag><SpecialAttentionFlag>false</SpecialAttentionFlag><SBSalaryCardFlag>true</SBSalaryCardFlag><SalaryCardList><SalaryCard><Type>0</Type><CardNum>0000000000000</CardNum></SalaryCard></SalaryCardList><SBSalaryDepFlag>false</SBSalaryDepFlag><SBShareHolderFlag>false</SBShareHolderFlag><SigningDate>")
				.append(dateId)
				.append("</SigningDate></AddData></Applicant></Application></ChargeLoanApplicationRq>")
				.toString();

		ArrayList<String> dataArray = new ArrayList<String>(5);

		dataArray.add(RqUID);

		dataArray.add(this.firstname);

		dataArray.add(lastname);

		dataArray.add(middlename);

		dataArray.add(birthday);

		DbOperation.getInstance().evalOperation(1, dataArray);
	}

	public static RqToESB getInstance() {

		return new RqToESB();

	}

	public String getRequest() {

		return this.response;
	}

	/**
	 * Method sets sbt flag
	 */
	private void setSBFlag() {

		double[] data = { 86.16586, 78.88448, 82.679344, 80.430145, 10.104777,
				83.449814, 26.916529, 15.24703, 82.7714, 11.47994, 98.1885,
				81.99514, 79.73708, 75.620285, 93.87316, 93.66079, 52.258743,
				48.0286, 94.02172, 85.82128, 26.0657, 79.41892, 84.11188,
				70.09833, 36.088913, 62.52272, 61.5199, 17.169056, 13.4713,
				37.641567, 69.00863, 70.40127, 90.07578, 73.64843, 79.703476,
				84.03388, 41.929382, 35.570946, 68.04708, 91.27183, 73.607124,
				46.76014, 49.08535, 30.706512, 90.46867, 81.612076, 37.1439,
				72.108986, 22.913603, 2.8673303, 71.50364, 7.872142, 54.417778,
				80.66467, 87.07771, 86.20168, 29.053865, 63.77049, 56.3295,
				21.572586, 92.483116, 37.74423, 19.752216, 1.9784291, 51.03352,
				87.232834, 29.15565, 96.10558, 44.451023, 59.113583, 66.77397,
				68.762405, 20.226103, 55.824615, 69.82101, 43.68555, 70.05701,
				39.936638, 86.57527, 45.231396, 23.013945, 31.826635,
				19.376947, 35.43992, 31.315247, 41.215904, 45.06259, 57.531654,
				76.31691, 99.32983, 11.350533, 25.065405, 16.351068, 7.5257044,
				75.52107, 62.745033, 25.426977, 46.41747, 36.410374, 61.21826 };

		logger.debug("COUNT SB: %d", count.get());

		double value = data[count.get()];

		if (value > 90) {

			this.sbflag = true;

		} else {

			this.sbflag = false;
		}

	}

	/**
	 * Method sets product type and sbt flag
	 */
	private void setProductTypeandSBFlag() {

		// set potred or doverie

		if (count.get() < 99)

			count.getAndIncrement();

		else {

			logger.debug("Count before reset: %s", count.get());

			count.set(0);

			logger.debug("Reset count! Count: %s", count.get());
		}

		setSBFlag();

		double[] dataFirst = { 98.41374, 20.58694, 66.5285, 39.441498,
				11.485894, 45.744995, 91.74246, 63.07487, 24.141188, 71.83153,
				73.87216, 41.954815, 16.155287, 75.90766, 55.300873, 81.4633,
				27.173428, 70.15134, 56.965397, 58.406017, 32.166252, 41.4379,
				77.88235, 35.523457, 14.778114, 91.41515, 10.532715, 79.04163,
				71.36034, 82.952065, 48.95898, 19.402878, 64.72308, 57.66737,
				91.77999, 3.6284063, 78.84945, 84.79023, 28.14785, 66.29501,
				69.9941, 42.869728, 57.959366, 7.856919, 60.73628, 83.833534,
				50.391346, 11.01176, 91.67316, 94.59795, 80.05464, 23.996197,
				48.524532, 66.85271, 5.756101, 13.101873, 54.473473, 40.78334,
				14.020872, 73.32649, 81.59017, 83.35257, 4.1590714, 74.91755,
				18.177233, 72.33899, 76.76164, 44.62775, 17.834747, 41.62753,
				76.079445, 81.781525, 5.972231, 46.260643, 57.7777, 12.750933,
				96.391815, 65.49267, 65.064865, 35.157066, 78.43344, 32.541847,
				63.17046, 38.04972, 33.007698, 36.90549, 97.590706, 75.47313,
				79.64565, 98.99341, 71.43411, 95.19979, 96.46754, 43.443024,
				60.580772, 15.007116, 90.71326, 25.80691, 60.219734, 12.683094 };

		double value = dataFirst[count.get()];

		if (value > 80) {

			this.codeProduct = "5";

			this.subProductCode = "1";

			this.firstname = generateName(7);

		} else {

			this.codeProduct = "10";

			double[] dataSecond = { 14.278897, 71.34094, 13.273826, 5.075161,
					9.37503, 27.342836, 27.060368, 79.212006, 93.264565,
					88.54575, 96.91934, 46.04552, 45.633633, 35.08396,
					89.28448, 40.562946, 11.964361, 97.31199, 84.90086,
					4.495965, 79.37408, 76.865875, 19.22438, 85.24698,
					25.105642, 14.99539, 43.707916, 14.396053, 68.682236,
					52.545284, 80.051834, 94.63323, 90.51613, 10.982599,
					45.943993, 4.5261283, 27.631319, 57.729015, 42.856888,
					39.44201, 3.1235723, 27.763483, 37.10509, 73.19653,
					40.03916, 21.010075, 46.59571, 88.597855, 12.504898,
					70.41968, 13.527823, 14.995357, 5.187164, 95.89128,
					30.713827, 82.22644, 80.49658, 55.259796, 60.20638,
					13.982635, 56.38752, 16.502134, 73.51911, 98.58811,
					19.127964, 13.081752, 19.92754, 87.499, 44.03554,
					6.9642625, 34.128613, 8.491132, 1.8891845, 81.29272,
					74.92296, 47.506763, 63.533676, 18.010973, 88.56361,
					93.47754, 75.09555, 40.867138, 81.98794, 88.13132, 73.2241,
					29.216356, 13.316907, 76.82466, 92.85995, 59.87329,
					80.76593, 53.820934, 38.21641, 21.93942, 44.466827,
					79.155174, 38.93509, 73.04887, 63.64793, 21.292894 };

			double val = (double) dataSecond[count.get()];

			if (val > 60) {

				this.subProductCode = "9911";

				this.firstname = generateName(2) + "оферта";

			} else {

				this.subProductCode = "9";

				this.firstname = generateName(7);
			}

		}

	}
}
