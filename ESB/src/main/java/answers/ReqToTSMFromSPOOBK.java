package answers;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Create request to TSM from SPOOBK
 * 
 * @author Maksim Stepanov
 *
 */
public class ReqToTSMFromSPOOBK {
	
	private String response = null;
	
	/**
	 * 
	 * @param data - array with data request from ETSM
	 * 
	 *  */
	public ReqToTSMFromSPOOBK(ArrayList<String> data) {
		
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'hh:mm:ss.SSSSSS");
		
		Date currentDate = new Date();
		
		String messageId = null;
		
		String messageDate = null;
		
		String fromAbonent = null;
			 
		messageId = new String(data.get(0));
		
		messageDate = new String(data.get(1));
			
		fromAbonent = new String(data.get(2));
		
	
		this.response = "<cap:SrvCardApproveInfoRs xmlns:cap=\"http://CreditFactory/ru/sbrf/sbt/tsm/cardapprove\" xmlns:gen=\"http://CreditFactory/ru/sbrf/sbt/tsm/general\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"+
				   "<MessUID>"+
				      "<MessageId>"+messageId+"</MessageId>"+
				      "<MessageDT>"+messageDate+"</MessageDT>"+
				      "<FromAbonent>"+fromAbonent+"</FromAbonent>"+
				   "</MessUID>"+
					"<result_info>"+
						"<code>0</code>"+
						"<description>OK</description>"+
						"<systemID>SPOOBK</systemID>"+
					"</result_info>"+
					"<business_info>"+
						"<srcMessageId>"+messageId.substring(6, 15)+"</srcMessageId>"+
						"<date>"+sdf.format(currentDate)+"</date>"+
						"<status>0</status>"+
						"<statusDesc>Ok</statusDesc>"+
					"</business_info>"+
				"</cap:SrvCardApproveInfoRs>";	
			
	}
	
	public String getResp() {
		
		return this.response;
	}
}
