package db;


/**
 * Connection info to DB
 * Example connection string - 	jdbc:oracle:thin:/@server:port:sid
 *@author Maksim Stepanov
 *
 */
public class dbConn {

	public static String ORA_HOST = "10.68.25.153";
	
	public static String ORA_PORT = "1521";
	
	public static String ORA_SID = "inquiry";
	
	public static String ORA_USER = "sb";
	
	public static String ORA_PASS = "sb";
	
	public static final String ORA_DB_URL = "jdbc:oracle:thin://@" + ORA_HOST + ":" + ORA_PORT + ":" + ORA_SID;
	

}
