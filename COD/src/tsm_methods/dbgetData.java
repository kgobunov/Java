package tsm_methods;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Getting data from db for response
 * 
 * @author Maksim Stepanov
 * 
 */
public class dbgetData {

	private static Connection connection = null;

	private static String ORA_HOST = WebAppContext.properties
			.getProperty("ORA_host");

	private static String ORA_PORT = WebAppContext.properties
			.getProperty("ORA_port");

	private static String ORA_SID = WebAppContext.properties
			.getProperty("ORA_sid");

	private static String ORA_USER = WebAppContext.properties
			.getProperty("ORA_user");

	private static String ORA_PASS = WebAppContext.properties
			.getProperty("ORA_password");

	private static String ORA_DB_URL = "jdbc:oracle:thin://@" + ORA_HOST + ":"
			+ ORA_PORT + ":" + ORA_SID;

	// jdbc:oracle:thin:/@server:port:sid

	public dbgetData(Properties properties) {

		// set stable connection

		Lock lock = new ReentrantLock();

		lock.lock();

		try {

			if (connection == null) {

				getConn();

			}

		} finally {

			lock.unlock();
		}

	}

	private final void getConn() {

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			connection = DriverManager.getConnection(ORA_DB_URL, ORA_USER,
					ORA_PASS);

			if (WebAppContext.debug) {
				System.out.println("Connected success!");
			}

		} catch (SQLException e) {

			System.err.println("Error: Failed connect to databases! "
					+ e.getMessage());

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * Check connection for available
	 * 
	 * @return connection status
	 */
	private boolean checkConnection() {

		boolean flag = true;

		try {

			flag = connection.isClosed();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return flag;
	}

	private final ArrayList<String> getData(ArrayList<String> dataArray) {

		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> saveData = dataArray;

		PreparedStatement statement = null;

		ResultSet resultset = null;

		if (checkConnection()) {

			if (null != connection) {

				try {

					connection.close();

				} catch (SQLException e) {

					System.err
							.println("Can't close connection! Error message: "
									+ e.getMessage());

					e.printStackTrace();

				}

				connection = null;

				getConn();

			}
		}

		try {

			String rq = "select firstname,lastname, middlename, birthday from  sbb_monitor.sbol_app  where RQUD = (select RQUID from UG_SBB.MESSAGE_QUEUE where appno = "
					+ Integer.valueOf(saveData.get(3)) + ")";

			if (WebAppContext.debug) {

				System.out.println(rq);

			}

			statement = connection.prepareStatement(rq);

			resultset = statement.executeQuery();

			while (resultset.next()) {

				result.add(resultset.getString("firstname"));

				result.add(resultset.getString("lastname"));

				result.add(resultset.getString("middlename"));

				result.add(resultset.getString("birthday"));
			}

			if (WebAppContext.debug) {

				System.out.println("firstname: " + result.get(0));

				System.out.println("lastname: " + result.get(1));

				System.out.println("middlename: " + result.get(2));

				System.out.println("birthday: " + result.get(3));

			}

			resultset.close();

			statement.close();

			if (WebAppContext.debug) {

				System.out.println("Select data success!");

			}

		} catch (SQLException e) {

			System.err
					.println("Error: Failed select data from databases! Error message: "
							+ e.getMessage());

			e.printStackTrace();

		} finally {

			if (null != resultset) {

				try {

					resultset.close();

				} catch (SQLException e) {

					e.printStackTrace();
				}

			}

			if (null != statement) {

				try {

					statement.close();

				} catch (SQLException e) {

					e.printStackTrace();
				}

			}

		}

		return result;

	}

	public final ArrayList<String> select(ArrayList<String> dataArray) {

		ArrayList<String> res = new ArrayList<String>();

		res = getData(dataArray);

		return res;

	}

}
