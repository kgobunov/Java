package db;

import static tsm_methods.WebAppContext.countCalls;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
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
public class DataBaseHelper {

	private static final Logger logger = LogManager
			.getLogger(DataBaseHelper.class.getName());

	private Connection connection = null;

	private Lock lock = new ReentrantLock();

	private DataBaseHelper() {

		if (this.connection == null) {

			setConnection();

		}

	}

	public static DataBaseHelper getInstance() {

		return LazyDbgetData.instance;

	}

	private void setConnection() {

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			this.connection = DriverManager.getConnection(DbConn.ORA_DB_URL,
					DbConn.ORA_USER, DbConn.ORA_PASS);

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

			flag = this.connection.isClosed();

		} catch (SQLException e) {

			logger.error(e.getMessage(), e);
		}

		return flag;
	}

	/**
	 * Update count system calls
	 * 
	 * @param systemName
	 *            - extarnal system name
	 */
	public void updateSystem(String systemName, boolean resetCalls) {

		PreparedStatement statement = null;

		if (checkConnection()) {

			this.lock.lock();

			try {

				try {
					if (this.connection.isClosed()) {

						this.connection = null;

						setConnection();

					}
				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			} finally {

				this.lock.unlock();

			}
		}

		try {

			String rq = "update sbb_monitor.check_calls_ext_systems SET LAST_UPDATE = ?, COUNT_CALLS = ? where SYSTEM_NAME = ?";

			logger.debug("Statement: " + rq);

			statement = this.connection.prepareStatement(rq);

			statement.setTimestamp(1, new Timestamp(new Date().getTime()));

			if (resetCalls) {

				statement.setLong(2, 0);

			} else {

				statement.setLong(2, countCalls.get());

			}

			statement.setString(3, systemName);

			int countRows = statement.executeUpdate();

			if (countRows == 0) {

				saveCountCall(systemName);

			}

			logger.info("Update system " + systemName + " calls successfully!");

		} catch (SQLException e) {

			logger.error("Failed update system " + systemName
					+ " calls! Error message: " + e.getMessage(), e);

		} finally {

			if (null != statement) {

				try {

					statement.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);
				}

			}

		}

	}

	/**
	 * 
	 * Check exist system.
	 * 
	 * @param systemName
	 */
	@SuppressWarnings("unused")
	private boolean checkExistSystem(String systemName) {

		PreparedStatement statement = null;

		ResultSet resultset = null;

		boolean flagExist = false;

		if (checkConnection()) {

			lock.lock();

			try {

				try {
					if (this.connection.isClosed()) {

						this.connection = null;

						setConnection();

					}
				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			} finally {

				lock.unlock();

			}
		}

		try {

			String rq = "select SYSTEM_NAME from sbb_monitor.check_calls_ext_systems  where SYSTEM_NAME = ?";

			logger.debug("Statement: " + rq);

			statement = this.connection.prepareStatement(rq);

			statement.setString(1, systemName);

			resultset = statement.executeQuery();

			if (resultset.next()) {

				flagExist = true;

				logger.debug("System " + systemName
						+ " exist. It will be update to zero.");

				updateSystem(systemName, true);

			}

		} catch (SQLException e) {

			logger.error("Failed check existing system " + systemName
					+ "! Error message: " + e.getMessage(), e);

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

		return flagExist;

	}

	/**
	 * 
	 * close connection to database
	 * 
	 */
	public void disconnect() {

		try {

			if (null != this.connection) {

				this.connection.close();

				logger.info("Connection closed!");

			}

		} catch (SQLException e) {

			logger.error(e.getMessage(), e);

		}

	}

	private ArrayList<String> getData(ArrayList<String> dataArray) {

		ArrayList<String> result = new ArrayList<String>();

		ArrayList<String> saveData = dataArray;

		PreparedStatement statement = null;

		ResultSet resultset = null;

		if (checkConnection()) {

			this.lock.lock();

			try {

				if (this.connection.isClosed()) {
					this.connection = null;

					setConnection();
				}

			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				this.lock.unlock();
			}

		}

		try {
			String rq = "select firstname,lastname, middlename, birthday from  sbb_monitor.sbol_app  where card_number = '"
					+ (String) saveData.get(0) + "'";

			logger.debug("Statement: " + rq);

			statement = this.connection.prepareStatement(rq);

			resultset = statement.executeQuery();

			while (resultset.next()) {
				result.add(resultset.getString("firstname"));

				result.add(resultset.getString("lastname"));

				result.add(resultset.getString("middlename"));

				result.add(resultset.getString("birthday"));
			}

			logger.debug("firstname: " + (String) result.get(0));

			logger.debug("lastname: " + (String) result.get(1));

			logger.debug("middlename: " + (String) result.get(2));

			logger.debug("birthday: " + (String) result.get(3));

			logger.debug("Select data success!");

		} catch (SQLException e) {
			logger.error("Failed select data from databases! Error message: "
					+ e.getMessage(), e);

		} finally {

			if (resultset != null) {
				try {
					resultset.close();
				} catch (SQLException e) {
					logger.error(e.getMessage(), e);
				}

			}

			if (statement != null) {
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

	/**
	 * Insert init info about calls for system
	 * 
	 * @param systemName
	 *            - external system name
	 */
	public void saveCountCall(String systemName) {

		PreparedStatement statement = null;

		if (checkConnection()) {

			lock.lock();

			try {

				try {
					if (this.connection.isClosed()) {

						this.connection = null;

						setConnection();

					}
				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			} finally {

				lock.unlock();

			}
		}

		try {

			String rq = "insert into sbb_monitor.check_calls_ext_systems ( LAST_UPDATE, SYSTEM_NAME , COUNT_CALLS) values (?, ?, ?)";

			logger.debug("Statement: " + rq);

			statement = this.connection.prepareStatement(rq);

			statement.setTimestamp(1, new Timestamp(new Date().getTime()));

			statement.setString(2, systemName);

			statement.setLong(3, countCalls.getAndSet(0));

			statement.executeUpdate();

			logger.info("Init system " + systemName + " successfully!");

		} catch (SQLException e) {

			logger.error("Failed init system " + systemName
					+ "! Error message: " + e.getMessage(), e);

		} finally {

			if (null != statement) {

				try {

					statement.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);
				}

			}

		}

	}

	private static class LazyDbgetData {

		public static DataBaseHelper instance = new DataBaseHelper();

	}

}
