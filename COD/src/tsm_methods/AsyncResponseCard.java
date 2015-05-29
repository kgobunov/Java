package tsm_methods;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Building async response
 * 
 * @author Maksim Stepanov
 * 
 */
public class AsyncResponseCard {

	private String busResponse;

	private ArrayList<String> personalInfo = null;

	public AsyncResponseCard(ArrayList<String> dataArray) {

		// mapping data from sbol
		this.personalInfo = dbgetData.getInstance().select(dataArray);

		/*
		 * 
		 * <NS1:busResponse xmlns:NS1="http://experian.com/business/sbrf/dpc"
		 * xmlns:cfa="http://sberbank.ru/cf"
		 * xmlns:tns="http://sberbank.ru/cf/rqrs"
		 * xmlns:ca16="http://sberbank.ru/dem/commonAggregates/16"
		 * xmlns:loan="http://sberbank.ru/dem/cf-gbo/loan"
		 * xmlns:card="http://sberbank.ru/dem/ifxCards/16"
		 * xmlns:ind16="http://sberbank.ru/dem/individual/16"
		 * ><result_info><code>
		 * 0</code><description>OK</description><systemID>BUS
		 * </systemID></result_info
		 * ><general_info><transactRequestDateTime>2014-02
		 * -10T22:04:07.985702</transactRequestDateTime
		 * ><queryResultCode>0</queryResultCode
		 * ></general_info><business_info><mainDataBlock
		 * ><surname>������</surname
		 * ><firstName>��������</firstName><patronymicName
		 * >�������������</patronymicName
		 * ><birthDate>1975-01-27</birthDate><accountNo
		 * >40817810050044720746</accountNo
		 * ><accountCurrency>810</accountCurrency
		 * ><accountTotalSalary>90754.09</accountTotalSalary
		 * ><monthCountWhenSalaryIncome
		 * >111111</monthCountWhenSalaryIncome><accountTotalOtherIncome
		 * >100000.00
		 * </accountTotalOtherIncome><monthCountWhenOtherIncome>10000</
		 * monthCountWhenOtherIncome
		 * ></mainDataBlock><redundantDataBlock><appNo>00000000000426185650
		 * </appNo
		 * ><cardNo>4276850078436190</cardNo><bankBranchNo>8635</bankBranchNo
		 * ><bankSubBranchNo
		 * >281</bankSubBranchNo><bankBranchIsOnline>true</bankBranchIsOnline
		 * ><accountSubType
		 * >1</accountSubType><accountTypeName>53</accountTypeName
		 * ><birthPlace>�����������</birthPlace><addressReg>692441!����
		 * ����������
		 * !!!07!�����������!44!����������!12!!6</addressReg><addressActual
		 * >������ 692441 ���� ���������� � ����������� �� ���������� � 12 ��
		 * 6</addressActual><docType>99</docType><docSeries>05
		 * 10</docSeries><docNumber
		 * >835657</docNumber><docIssueDate>2011-09-30</docIssueDate
		 * ><docissuer>����� ������ �� ���� �� �
		 * ���</docissuer><isResidentFlag>true
		 * </isResidentFlag></redundantDataBlock
		 * ></business_info><MessUID><MessageId
		 * >0000000000042618565011021402040510104
		 * </MessageId><MessageDate>2014-02
		 * -11</MessageDate><FromAbonent>COD</FromAbonent
		 * ></MessUID></NS1:busResponse>
		 */

		// ��������� ������ �� �������
		String card = dataArray.get(0);
		String messageId = dataArray.get(1);
		String messageDate = dataArray.get(2);
		String appNo = dataArray.get(3);

		// �������� �������� xml-�����

		String transactRequestDateTime = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss").format(new Date());

		this.busResponse = "<NS1:busResponse xmlns:NS1=\"http://experian.com/business/sbrf/dpc\" xmlns:cfa=\"http://sberbank.ru/cf\" xmlns:tns=\"http://sberbank.ru/cf/rqrs\" xmlns:ca16=\"http://sberbank.ru/dem/commonAggregates/16\" xmlns:loan=\"http://sberbank.ru/dem/cf-gbo/loan\" xmlns:card=\"http://sberbank.ru/dem/ifxCards/16\" xmlns:ind16=\"http://sberbank.ru/dem/individual/16\">"
				+ "<result_info><code>0</code><description>OK</description><systemID>BUS</systemID></result_info>"
				+ "<general_info><transactRequestDateTime>"
				+ transactRequestDateTime
				+ "</transactRequestDateTime>"
				+ "<queryResultCode>0</queryResultCode>"
				+ "</general_info>"
				+ "<business_info>"
				+ "<mainDataBlock>"
				+ "<surname>"
				+ this.personalInfo.get(1)
				+ "</surname>"
				+ "<firstName>"
				+ this.personalInfo.get(0)
				+ "</firstName>"
				+ "<patronymicName>"
				+ this.personalInfo.get(2)
				+ "</patronymicName>"
				+ "<birthDate>"
				+ this.personalInfo.get(3)
				+ "</birthDate>"
				+ "<accountNo>40817810050044720746</accountNo>"
				+ "<accountCurrency>810</accountCurrency>"
				+ "<accountTotalSalary>150000</accountTotalSalary>"
				+ "<monthCountWhenSalaryIncome>11111</monthCountWhenSalaryIncome>"
				+ "<accountTotalOtherIncome>0.00</accountTotalOtherIncome>"
				+ "<monthCountWhenOtherIncome>0</monthCountWhenOtherIncome>"
				+ "</mainDataBlock>"
				+ "<redundantDataBlock>"
				+ "<appNo>"
				+ appNo
				+ "</appNo>"
				+ "<cardNo>"
				+ card
				+ "</cardNo>"
				+ "<bankBranchNo>0103</bankBranchNo>"
				+ "<bankSubBranchNo>281</bankSubBranchNo>"
				+ "<bankBranchIsOnline>true</bankBranchIsOnline>"
				+ "<accountSubType>1</accountSubType>"
				+ "<accountTypeName>53</accountTypeName>"
				+ "<birthPlace>������</birthPlace>"
				+ "<addressReg>143350!������!!!77!������!44!������!1!!23</addressReg>"
				+ "<addressActual>������ 143350 ������ � ������ �� ������ � 1 �� 23</addressActual>"
				+ "<docType>99</docType>"
				+ "<docSeries>4623</docSeries>"
				+ "<docNumber>837744</docNumber>"
				+ "<docIssueDate>2013-01-18</docIssueDate>"
				+ "<docissuer>���� ������</docissuer>"
				+ "<isResidentFlag>true</isResidentFlag>"
				+ "</redundantDataBlock>"
				+ "</business_info>"
				+ "<MessUID>"
				+ "<MessageId>"
				+ messageId
				+ "</MessageId>"
				+ "<MessageDate>"
				+ messageDate
				+ "</MessageDate>"
				+ "<FromAbonent>COD</FromAbonent>"
				+ "</MessUID>"
				+ "</NS1:busResponse>";

	}

	public String get() {

		return this.busResponse;

	}

}
