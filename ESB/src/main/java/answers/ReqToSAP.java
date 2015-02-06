package answers;


import java.util.ArrayList;

/**
 * Create request to SAP HR
 * 
 * @author Maksim Stepanov
 *
 */
public class ReqToSAP {

	private String response = null;
	
	
	public ReqToSAP(ArrayList<String> data) {

		this.response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
							"<ech:SrvSBEmployeeCheckRs xmlns:ech=\"http://CreditFactory/ru/sbrf/sbt/tsm/employeecheck\" xmlns:gen=\"http://CreditFactory/ru/sbrf/sbt/tsm/general\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://CreditFactory/ru/sbrf/sbt/tsm/employeecheck SrvSBEmployeeCkeckRq.xsd \">" +
							"<ech:SrcMessageData>" +  
									"<MessageId>"+data.get(0)+"</MessageId>"+
									"<MessageDT>"+data.get(1)+"</MessageDT>"+
									"<FromAbonent>SAP</FromAbonent>"+
							  "</ech:SrcMessageData>" + //TSMId
							  "<ech:TSMId>"+data.get(2)+"</ech:TSMId>"+
							  "<ech:result_info>" +
							    "<code>"+data.get(3)+"</code>" +
							    "<description>"+data.get(4)+"</description>" +
							    "<systemID>"+data.get(5)+"</systemID>" +
							  "</ech:result_info>" +
							  "<ech:business_info>" +
							    "<ech:SberbankEmployeeFlag>true</ech:SberbankEmployeeFlag>" +
							    "<ech:ChartNumber>"+data.get(7)+"</ech:ChartNumber>" +
							    "<ech:StartDate>2012-01-01</ech:StartDate>" +  
							    "<ech:EndDate>2015-01-01</ech:EndDate>" +
							  "</ech:business_info>" +
							"</ech:SrvSBEmployeeCheckRs>";
		
	}
	
	public String getRq() {
		
		return this.response;
	}
}
