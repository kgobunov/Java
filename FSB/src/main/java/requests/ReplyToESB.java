package requests;

import java.util.ArrayList;

/**
 * 
 * Reply that card was cough up
 * 
 * @author Maksim Stepanov
 * 
 */
public class ReplyToESB {

	private ReplyToESB() {
	}

	public static String getResp(ArrayList<String> data) {

		String response = "<ConsumerCardStatus>" + "<MessUID>" + "<MessageId>"
				+ data.get(0) + "</MessageId>" + "<MessageDT>" + data.get(1)
				+ "</MessageDT>" + "<FromAbonent>FSB</FromAbonent>"
				+ "<ToAbonent>ETSM_Rtl</ToAbonent>" + "</MessUID>" + "<SrcRq>"
				+ "<RqUID>" + data.get(2) + "</RqUID>" + "<RqTm>" + data.get(3)
				+ "</RqTm>" + "</SrcRq>" + "<SrcObjID>1-12OZ3L</SrcObjID>"
				+ "<ResultCardStatus>" + "<StatusCode>1</StatusCode>"
				+ "</ResultCardStatus>" + "</ConsumerCardStatus>";

		return response;

	}

}
