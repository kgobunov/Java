package db;

import static tools.PropsChecker.db;

/**
 * Connection info to DB
 * Example connection string - 	jdbc:oracle:thin:/@server:port:sid
 *@author Maksim Stepanov
 *
 */
public class DbConn {

	public static final String ORA_HOST = db.getChildText("host");
	
	public static final String ORA_PORT = db.getChildText("port");
	
	public static final String ORA_SID = db.getChildText("sid");
	
	public static final String ORA_USER = db.getChildText("user");
	
	public static final String ORA_PASS = db.getChildText("password");
	
	public static final String ORA_DB_URL = "jdbc:oracle:thin://@" + ORA_HOST + ":" + ORA_PORT + ":" + ORA_SID;
	

}
