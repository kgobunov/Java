package db;

import static tsm_methods.WebAppContext.properties;

/**
 * Connection info to DB Example connection string -
 * jdbc:oracle:thin:/@server:port:sid
 * 
 * @author Maksim Stepanov
 * 
 */
public class DbConn {

	public static String ORA_HOST = properties.getProperty("ORA_host");

	public static String ORA_PORT = properties.getProperty("ORA_port");

	public static String ORA_SID = properties.getProperty("ORA_sid");

	public static String ORA_USER = properties.getProperty("ORA_user");

	public static String ORA_PASS = properties.getProperty("ORA_password");

	public static final String ORA_DB_URL = "jdbc:oracle:thin://@" + ORA_HOST
			+ ":" + ORA_PORT + ":" + ORA_SID;

}
