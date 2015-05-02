package pfr;

import ru.aplana.app.Initialization;

/**
 * 
 * Classname: Statistics
 * 
 * Version: 1.0
 * 
 * 
 * Save statistics about pfr to db
 * 
 * @author Maksim Stepanov
 *
 */
public class Statistics implements Runnable {

	private String operanionName;
	
	private long responseTime;
	
	private String errorMsg;
	
	public Statistics(String operation, long responseTime, String error) {
		
		this.operanionName = operation;
		
		this.responseTime = responseTime;
		
		this.errorMsg = error;
		
	}
	
	@Override
	public void run() {

		Initialization.helper.insertPfr(this.operanionName, this.responseTime, this.errorMsg );
		
	}

}
