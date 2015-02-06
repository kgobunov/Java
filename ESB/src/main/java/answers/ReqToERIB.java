package answers;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Create request to ERIB
 * 
 * @author Maksim Stepanov
 *
 */
public class ReqToERIB {

	private String response = null;
	
	private String responseFinal = null;
	
	/**
	 * 
	 * @param data for request
	 */
	public ReqToERIB(ArrayList<String> data) {
		
		String status = data.get(3);
		
		if (status.equalsIgnoreCase("-1") || status.equalsIgnoreCase("0") ) {
	
			this.response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
				"<StatusLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"+
				  "<RqUID>"+data.get(0)+"</RqUID>"+
				  "<OperUID>"+data.get(2)+"</OperUID>"+
				  "<SPName>BP_ES</SPName>"+
				  "<SrcRq>"+
				    "<RqUID>"+data.get(0)+"</RqUID>"+
				    "<RqTm>"+data.get(1)+"</RqTm>"+
				"</SrcRq>"+
				  "<ApplicationStatus>"+
				    "<Status>"+
				      "<StatusCode>"+status+"</StatusCode>"+
				      "<ApplicationNumber>"+data.get(4)+"</ApplicationNumber>"+
				      "<Error>"+
				        "<ErrorCode>"+data.get(5)+"</ErrorCode>"+
				        "<Message>"+data.get(6)+"</Message>"+
				      "</Error>"+
				    "</Status>"+
				  "</ApplicationStatus>"+
				"</StatusLoanApplicationRq>";

		} else if (status.equalsIgnoreCase("1") || status.equalsIgnoreCase("4")) {
			
			this.response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<StatusLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"+
					  "<RqUID>"+data.get(0)+"</RqUID>"+
					  "<RqTm>"+data.get(1)+"</RqTm>"+
					  "<OperUID>"+data.get(2)+"</OperUID>"+
					  "<SPName>BP_ES</SPName>"+
					  "<SrcRq>"+
					    "<RqUID>"+data.get(0)+"</RqUID>"+
					    "<RqTm>"+data.get(1)+"</RqTm>"+
					"</SrcRq>"+
					  "<ApplicationStatus>"+
					    "<Status>"+
					      "<StatusCode>"+status+"</StatusCode>"+
					      "<ApplicationNumber>"+data.get(4)+"</ApplicationNumber>"+
					    "</Status>"+
					  "</ApplicationStatus>"+
					"</StatusLoanApplicationRq>";
			
		} else {
			
			this.response = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
					"<StatusLoanApplicationRq xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"LoanApplication.xsd\">"+
					  "<RqUID>"+data.get(0)+"</RqUID>"+
					  "<RqTm>"+data.get(1)+"</RqTm>"+
					  "<OperUID>"+data.get(2)+"</OperUID>"+
					  "<SPName>BP_ES</SPName>"+
					  "<SrcRq>"+
					    "<RqUID>"+data.get(0)+"</RqUID>"+
					    "<RqTm>"+data.get(1)+"</RqTm>"+
					"</SrcRq>"+
					  "<ApplicationStatus>"+
					    "<Status>"+
					      "<StatusCode>"+status+"</StatusCode>"+
					      "<ApplicationNumber>"+data.get(4)+"</ApplicationNumber>"+
					    "</Status>"+
					    "<Approval>"+
					      "<PeriodM>"+data.get(5)+"</PeriodM>"+
					      "<Amount>"+data.get(6)+"</Amount>"+
					      "<InterestRate>"+data.get(7)+"</InterestRate>"+
					    "</Approval>"+
					  "</ApplicationStatus>"+
					"</StatusLoanApplicationRq>";
			
		}
		
		try {
			
			this.responseFinal = new String(this.response.getBytes(), "UTF-8");
			
		} catch (UnsupportedEncodingException e) {

			e.printStackTrace();
		}
	}
	
	public String getRq() {
		
		return this.responseFinal;
	}
}
