package requests;

import static ru.aplana.tools.Common.generateDOB;
import static ru.aplana.tools.Common.generateName;
import static ru.aplana.tools.Common.generateNumber;
import static ru.aplana.tools.Common.generateRqUID;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import db.DbOperation;

/**
 * Creates CRM app
 * 
 * @author Maksim Stepanov
 * 
 */
public class RqToESB {

	private String response = null;

	private ArrayList<String> dataArray = null;

	public static AtomicInteger count = new AtomicInteger(-1);

	public RqToESB(String[] settings) throws UnsupportedEncodingException {

		// generate RqUID
		String RqUID = generateRqUID();

		// generating FIO
		String firstname = new String(generateName(7));

		String lastname = new String(generateName(9));

		// generating ID
		String id = new String(generateNumber(6));

		// generating serial ID
		String serial = new String(generateNumber(4));

		String middlename = new String("фсбович");

		String birthday = new String(generateDOB(1975, 6));

		// date get id
		Date current = new Date();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String dateTemp = sdf.format(current);

		String dateId = new String(dateTemp);

		this.response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<ChargeLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"
				+ "<MessageData>" + "<RqUID>"
				+ RqUID
				+ "</RqUID>"
				+ "<RqTm>"
				+ new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.S")
						.format(new Date())
				+ "</RqTm>"
				+ "<FromAbonent>FSB</FromAbonent>"
				+ "</MessageData>"
				+ "<Application>"
				+ "<SrcObjID>1-12OZ3L</SrcObjID>"
				+ "<Unit>99010300000</Unit>"
				+ "<Channel>1</Channel>"
				+ "<Product>"
				+ "<Type>3</Type>"
				+ "<Code>15</Code>"
				+ "<SubProductCode>3</SubProductCode>"
				+ "<Amount>100000</Amount>"
				+ "<Currency>RUB</Currency>"
				+ "<GPC_code>11</GPC_code>"
				+ "<Card_code>111715</Card_code>"
				+ "<withoutAppearance>false</withoutAppearance>"
				+ "</Product>"
				+ "<Applicant>"
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
				+ "<Sex>1</Sex>"
				+ "<Birthday>"
				+ birthday
				+ "</Birthday>"
				+ "<BirthPlace>МОСКВА</BirthPlace>"
				+ "<Contact>"
				+ "<AddressList>"
				+ "<Address>"
				+ "<AddrType>1</AddrType>"
				+ "<Country>RU</Country>"
				+ "<PostalCode>109507</PostalCode>"
				+ "<RegionCode>0077</RegionCode>"
				+ "<AreaType>103</AreaType>"
				+ "<Area>Москва</Area>"
				+ "<CityType>301</CityType>"
				+ "<City>Москва</City>"
				+ "<SettlementType>405</SettlementType>"
				+ "<Settlement>Москва</Settlement>"
				+ "<StreetType>529</StreetType>"
				+ "<Street>Маяковская</Street>"
				+ "<HouseNum>10</HouseNum>"
				+ "<HouseExt>1</HouseExt>"
				+ "<Unit></Unit>"
				+ "<UnitNum>13</UnitNum>"
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
				+ "<IssuedBy>УФМС Москвы</IssuedBy>"
				+ "<IssuedCode>770-079</IssuedCode>"
				+ "<IssueDt>"
				+ dateId
				+ "</IssueDt>"
				+ "<PrevIDInfoFlag>false</PrevIDInfoFlag>"
				+ "</IdentityCard>"
				+ " <clientSegment>"
				+ " <Category>4</Category>"
				+ " <Segment1_Code>50</Segment1_Code>"
				+ " <Segment2_Code>51</Segment2_Code>"
				+ " <Segment3_Code>0</Segment3_Code>"
				+ " </clientSegment>"
				+ "</PersonInfo>"
				+ "<EmploymentHistory>"
				+ "<SBEmployeeFlag>false</SBEmployeeFlag>"
				+ "</EmploymentHistory>"
				+ "<AddData>"
				+ "<CBReqApprovalFlag>true</CBReqApprovalFlag>"
				+ "<CBSendApprovalFlag>true</CBSendApprovalFlag>"
				+ "</AddData>"
				+ "</Applicant>"
				+ "</Application>"
				+ "</ChargeLoanApplicationRq>";

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

}
