package tools;

import static tools.PropCheck.ckpit;
/**
 * 
 * Queues
 * 
 * @author Maksim Stepanov
 *
 */
public class Queues {
	
	public final static String CKPIT_OUT = ckpit.getChildText("queueTo");
	
	public final static String CKPIT_IN = ckpit.getChildText("queueFrom");

}
