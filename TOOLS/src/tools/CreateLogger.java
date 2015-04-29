package tools;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logger implementation
 * 
 * @author Maksim Stepanov
 *
 */
public class CreateLogger {
	
	private Logger loggerInfo = null;
	
	private Handler handlerInfo = null;
	
	private Logger loggerSevere = null;
	
	private Handler handlerSevere = null;
	
	/**
	 * 
	 * @param logName - name for log file
	 * @param sizeFile - size log file
	 * @param maxcountFile - max count log files
	 */
	public CreateLogger(String logName, int sizeFile, int maxcountFile) {
		
		this.loggerInfo = Logger.getLogger(logName + "_info");
		
		this.loggerSevere = Logger.getLogger(logName + "_severe");
		
 		
		try {
  		      this.handlerInfo = new FileHandler(System.getProperty("user.dir") + "\\logs\\" + logName + "_info%g.log", sizeFile, maxcountFile);
  		      
  		      this.handlerSevere = new FileHandler(System.getProperty("user.dir") + "\\logs\\" + logName + "_severe%g.log", sizeFile, maxcountFile);
  		      
  		    } catch (IOException e) {
  		    
  		    	
  		      this.handlerInfo = new ConsoleHandler();
  		      
  		      this.handlerSevere = new ConsoleHandler();
  		      
  		    }
  		
  		this.loggerInfo.addHandler(this.handlerInfo);
  		
  		this.loggerSevere.addHandler(this.handlerSevere);
  		
  		this.loggerInfo.setLevel(Level.FINE);
  		
  		this.handlerInfo.setFormatter(new SimpleFormatter());
  		
  		this.handlerSevere.setFormatter(new SimpleFormatter());
  		
 	
	}
	
	public Logger getInfoLogger() {
		
		return this.loggerInfo;
		
	}

	public Logger getSevereLogger() {
		
		return this.loggerSevere;
		
	}

}
