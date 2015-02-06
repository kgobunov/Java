package tools;

import static tools.PropsChecker.esb;


/**
 * 
 * Queues
 * 
 * @author Maksim Stepanov
 *
 */
public class Queues {
	
	
	// producer's queues

	public final static String ETSM_OUT = esb.getChildText("queueToEtsm");
	
	public final static String ERIB_OUT = esb.getChildText("queueToErib");

	public final static String CRM_OUT = esb.getChildText("queueToCrm");
	
	public final static String FSB_OUT = esb.getChildText("queueToFsb");
	
	//garbage queue
	public final static String GARBAGE_OUT = esb.getChildText("queueToGarbage");
	
	//queue for bad osgi message
	
	public final static String SERVICE_GARBAGE_OUT = esb.getChildText("queueToGarbageServices");
	
	// consumer's queues
	
	public final static String ERIB_IN = esb.getChildText("queueFromErib");
	
	public final static String ETSM_IN = esb.getChildText("queueFromEtsm");
	
	public final static String SAP_HR_IN = esb.getChildText("queueFromEtsmSAPHR");
	
	public final static String ASYNC_IN = esb.getChildText("queueFromEtsmAsync");
	
	public final static String ESB_CRM_IN = esb.getChildText("queueFromCrmEsb");
	
	public final static String FSB_IN = esb.getChildText("queueFromFsbEsb");
	
	public final static String SERVICES_IN = esb.getChildText("queueFromServices");
	

}
