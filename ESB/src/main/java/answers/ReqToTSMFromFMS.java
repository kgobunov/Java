package answers;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static ru.aplana.tools.Common.generateTaskId;

/**
 * Create request to TSM from FMS
 * 
 * @author Maksim Stepanov
 *
 */
public class ReqToTSMFromFMS {
	
	private String response = null;
		
	/**
	 * Constructor
	 * 
	 * @param data - array with data request from ETSM
	 * @param type_request - type FMSBookRequest or FMSResultRequest
	 */
	public ReqToTSMFromFMS(ArrayList<String> data, String type_request) {
		
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSSSSS");
		Date currentDate = new Date();
		
		String messageId = null;
	
		String messageDate = null;
		
		String fromAbonent = null;

		messageId = new String(data.get(0));
		
		messageDate = new String(data.get(1));
			
		fromAbonent = new String(data.get(2));

		if (type_request.equalsIgnoreCase("FMSBookRequest")) {
			
			// generating taskID
			String taskId = generateTaskId();
			
			this.response = "<ns2:FMSBookResponse xmlns:ns2=\"http://CreditFactory/ru/sbrf/sbt/tsm/fms\">"+
				   "<result_info>"+
				      "<code>0</code>"+
				      "<description>OK</description>"+
				      "<systemID>FMS</systemID>"+
				   "</result_info>"+
				   "<general_info>"+
						"<transactRequestDateTime>"+sdf.format(currentDate)+"</transactRequestDateTime>"+
				   "</general_info>"+
				   "<business_info>"+
				      "<taskId>"+taskId+"</taskId>"+
				      "<fmsId>"+taskId+"</fmsId>"+
				   "</business_info>"+
				   "<MessUID>"+
				      "<MessageId>"+messageId+"</MessageId>"+
				      "<MessageDate>"+messageDate+"</MessageDate>"+
				      "<FromAbonent>"+fromAbonent+"</FromAbonent>"+
				   "</MessUID>"+
				"</ns2:FMSBookResponse>";	
		
		}
		
		 else {
			
			this.response = "<ns2:FMSResultResponse xmlns:ns2=\"http://CreditFactory/ru/sbrf/sbt/tsm/fms\">"+
			   "<result_info>"+
			      "<code>0</code>"+
			      "<description>OK</description>"+
			      "<systemID>FMS</systemID>"+
			   "</result_info>"+
			   "<general_info>"+
					"<transactRequestDateTime>"+sdf.format(currentDate)+"</transactRequestDateTime>"+
			   "</general_info>"+
			   "<business_info>"+
			      "<TaskResult>300</TaskResult>"+ //паспорт действителен
			   "</business_info>"+
			   "<MessUID>"+
			      "<MessageId>"+messageId+"</MessageId>"+
			      "<MessageDate>"+messageDate+"</MessageDate>"+
			      "<FromAbonent>"+fromAbonent+"</FromAbonent>"+
			   "</MessUID>"+
			"</ns2:FMSResultResponse>";
			
			
		}
		
	}
	
	/**
	 * 
	 * @return response for TSM
	 */
	public String getResp() {
		
		return this.response;
	}


}
