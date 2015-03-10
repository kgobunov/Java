package db;

import static ru.aplana.app.RunServer.db;

/**
 * connection settings to DB
 * Example connection string - 	jdbc:oracle:thin:/@oder1.cgs.sbrf.ru:1526:transcom
 *@author Maksim Stepanov
 *
 */
public class dbConn {

	public static final String ORA_HOST = db.getChildText("host");
	
	public static final String ORA_PORT = db.getChildText("port");
	
	public static final String ORA_SID = db.getChildText("sid");
	
	public static final String ORA_USER = db.getChildText("user");
	
	public static final String ORA_PASS = db.getChildText("password");
	
	public static final String ORA_DB_URL = "jdbc:oracle:thin://@" + ORA_HOST + ":" + ORA_PORT + ":" + ORA_SID;
	

}
