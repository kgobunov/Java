package tools;


/**
 * Class clear black list
 * 
 * @author Maksim Stepanov
 *
 */
public class ClearBlackList implements Runnable {

	
	public ClearBlackList() {
		
		WebAppContext.loggerInfo.info(">>>Activating clearing black list!");
	}
	
	@Override
	public void run() {
		
		
		Staff.clearBadUrl();
		
	}

}
