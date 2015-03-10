package tools;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Implementation logger
 * 
 * @author Maksim Stepanov
 *
 */
public class CreateLogger {
	
	private Logger logger_info = null;
	
	private Handler handler_info = null;
	
	/**
	 * 
	 * @param logName - name for log file
	 * @param sizeFile - size log file
	 * @param maxcountFile - max count log files  
	 */
	public CreateLogger(String logName, int sizeFile, int maxcountFile) {
		
		this.logger_info = Logger.getLogger(logName + "_info");
		
 		
		try {
  		      this.handler_info = new FileHandler(System.getProperty("user.dir") + "\\logs\\" + logName + "_info%g.log", sizeFile, maxcountFile);
  		      
  		    } catch (IOException e) {
  		    
  		    	
  		      this.handler_info = new ConsoleHandler();
  		      
  		    }
  		
  		this.logger_info.addHandler(this.handler_info);
  		
  		this.logger_info.setLevel(Level.FINE);
  		
  		this.handler_info.setFormatter(new SimpleFormatter());
  		
 	
	}
	
	public Logger getInfoLogger() {
		
		return this.logger_info;
		
	}
	

}
