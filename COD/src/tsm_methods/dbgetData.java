package tsm_methods;

import static tsm_methods.WebAppContext.properties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Getting data from db for response
 * 
 * @author Maksim Stepanov
 * 
 */
public class dbgetData {

	private static final Logger logger = LogManager.getLogger(dbgetData.class
			.getName());

	private static Connection connection = null;

	private static String ORA_HOST = properties.getProperty("ORA_host");

	private static String ORA_PORT = properties.getProperty("ORA_port");

	private static String ORA_SID = properties.getProperty("ORA_sid");

	private static String ORA_USER = properties.getProperty("ORA_user");

	private static String ORA_PASS = properties.getProperty("ORA_password");

	private static String ORA_DB_URL = "jdbc:oracle:thin://@" + ORA_HOST + ":"
			+ ORA_PORT + ":" + ORA_SID;

	private static Lock lock = new ReentrantLock();

	// jdbc:oracle:thin:/@server:port:sid

	private dbgetData() {

		if (connection == null) {

			getConn();

		}

	}

	public static dbgetData getInstance() {

		return LazyDbgetData.instance;

	}

	private void getConn() {

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			connection = DriverManager.getConnection(ORA_DB_URL, ORA_USER,
					ORA_PASS);

			logger.debug("Connected success!");

		} catch (SQLException e) {

			logger.error("Failed connect to databases! " + e.getMessage(), e);

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

			logger.error(e.getMessage(), e);
		}

		return flag;
	}

	private ArrayList<String> getData(ArrayList<String> dataArray) {

		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> saveData = dataArray;

		PreparedStatement statement = null;

		ResultSet resultset = null;

		if (checkConnection()) {

			lock.lock();

			try {

				try {
					if (connection.isClosed()) {

						connection = null;

						getConn();

					}
				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			} finally {

				lock.unlock();

			}
		}

		try {

			String rq = "select firstname,lastname, middlename, birthday from  sbb_monitor.sbol_app  where card_number = '"
					+ saveData.get(0) + "'";

			logger.debug("Statement: " + rq);

			statement = connection.prepareStatement(rq);

			resultset = statement.executeQuery();

			while (resultset.next()) {

				result.add(resultset.getString("firstname"));

				result.add(resultset.getString("lastname"));

				result.add(resultset.getString("middlename"));

				result.add(resultset.getString("birthday"));
			}

			logger.debug("firstname: " + result.get(0));

			logger.debug("lastname: " + result.get(1));

			logger.debug("middlename: " + result.get(2));

			logger.debug("birthday: " + result.get(3));

			logger.debug("Select data success!");

		} catch (SQLException e) {

			logger.error("Failed select data from databases! Error message: "
					+ e.getMessage(), e);

		} finally {

			if (null != resultset) {

				try {

					resultset.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);
				}

			}

			if (null != statement) {

				try {

					statement.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);
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

	private static class LazyDbgetData {

		public static dbgetData instance = new dbgetData();

	}

}
