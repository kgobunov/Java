package db;

import static tools.PropsChecker.callsCountEsopss;
import static tools.PropsChecker.callsCountFms;
import static tools.PropsChecker.callsCountMdm;
import static tools.PropsChecker.callsCountSpoobk;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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

				switch (systemName) {
				case "ESOPSS":
					statement.setLong(2, callsCountEsopss.get());
					break;
				case "MDM":
					statement.setLong(2, callsCountMdm.get());
					break;
				case "FMS":
					statement.setLong(2, callsCountFms.get());
					break;
				case "SPOOBK":
					statement.setLong(2, callsCountSpoobk.get());
					break;

				default:
					break;
				}

			}

			statement.setString(3, systemName);

			int countRows = statement.executeUpdate();

			if (countRows == 0) {

				saveCountCalls(systemName);

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

	/**
	 * Insert init info about calls for system
	 * 
	 * @param systemName
	 *            - extarnal system name
	 */
	public void saveCountCalls(String systemName) {

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

			switch (systemName) {
			case "ESOPSS":
				statement.setLong(3, callsCountEsopss.getAndSet(0));
				break;
			case "MDM":
				statement.setLong(3, callsCountMdm.getAndSet(0));
				break;
			case "FMS":
				statement.setLong(3, callsCountFms.getAndSet(0));
				break;
			case "SPOOBK":
				statement.setLong(3, callsCountSpoobk.getAndSet(0));
				break;

			default:
				break;
			}

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
