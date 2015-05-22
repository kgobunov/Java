package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ru.aplana.tools.OracleDB;

/**
 * SMS logger
 * 
 * @author Maksim Stepanov
 * 
 */
public class dataBaseHelper {

	private Connection connection = null;

	private Lock lock = new ReentrantLock();

	private static final Logger logger = LogManager
			.getFormatterLogger(dataBaseHelper.class.getName());

	private dataBaseHelper() {

		setConnection();

	}

	public static dataBaseHelper getInstance() {

		return LasyDbHolder.dbInstance;

	}

	/**
	 * 
	 * Connect to DB
	 * 
	 */
	private void setConnection() {

		if (null == this.connection) {

			this.lock.lock();

			try {

				this.connection = OracleDB.getConn(dbConn.ORA_DB_URL,
						dbConn.ORA_USER, dbConn.ORA_PASS);

				logger.debug("Connected success!");

			} catch (SQLException e) {

				logger.error("Failed connect to databases! %s", e.getMessage(),
						e);

			} finally {

				this.lock.unlock();
			}
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
	 * Update exist sms status
	 * 
	 * @param data
	 */
	private final void update(String body, String appNumber) {

		if (appNumber.length() > 0) {

			if (checkConnection()) {

				if (null != this.connection) {

					try {

						this.connection.close();

					} catch (SQLException e) {

						logger.error("Can't close connection! %s",
								e.getMessage(), e);

					}

					this.connection = null;

					setConnection();
				}
			}

			String update = "update sbb_monitor.email_log set LAST_TIME = sysdate, MSG_TEXT = ?, COUNT_CALL = ? where APP_NUMBER = ?";

			PreparedStatement st = null;

			try {

				st = this.connection.prepareStatement(update);

				st.setString(1, body);

				int countCalls = checkCalls(appNumber);

				countCalls++;

				st.setInt(2, countCalls);

				st.setString(3, appNumber);

				st.executeUpdate();

				logger.debug("Update successfully! APP_NUMBER: %s", appNumber);

			} catch (SQLException e) {

				logger.error("Can't update email! %s", e.getMessage(), e);

			} finally {

				if (null != st) {

					try {

						st.close();

					} catch (SQLException e) {

						logger.error(e.getMessage(), e);
					}
				}

			}

		}

	}

	/**
	 * Check count calls by one email
	 * 
	 * @param id
	 *            - email number
	 * @return
	 */
	private final int checkCalls(String appNumber) {

		int count = 0;

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					logger.error("Can't close connection! %s", e.getMessage(),
							e);

				}

				this.connection = null;

				setConnection();
			}
		}

		String select = "select COUNT_CALL from sbb_monitor.email_log where APP_NUMBER = ?";

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = this.connection.prepareStatement(select);

			st.setString(1, appNumber);

			rs = st.executeQuery();

			if (rs.next()) {

				count = Integer.parseInt(rs.getString("COUNT_CALL"));

				logger.debug("Calls for APP_NUMBER %s: %s", appNumber, count);

			}

		} catch (SQLException e) {

			logger.error("Can't check calls for %s! %s", appNumber,
					e.getMessage(), e);

		} finally {

			if (null != st) {

				try {

					st.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}
			}

			if (null != rs) {

				try {

					rs.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			}
		}

		return count;

	}

	/**
	 * Check email id
	 * 
	 * @param id
	 *            - email number
	 * @return
	 */
	private final boolean select(String appNumber) {

		boolean flag = false;

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					logger.error("Can't close connection! %s", e.getMessage(),
							e);

				}

				this.connection = null;

				setConnection();
			}
		}

		String select = "select APP_NUMBER from sbb_monitor.email_log where APP_NUMBER = ?";

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = this.connection.prepareStatement(select);

			st.setString(1, appNumber);

			rs = st.executeQuery();

			if (rs.next()) {

				logger.debug("Email was found! APP_NUMBER: %s", appNumber);

				flag = true;

			}

		} catch (SQLException e) {

			logger.error("Can't get email number %s! %s", appNumber,
					e.getMessage(), e);

		} finally {

			if (null != st) {

				try {

					st.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}
			}

			if (null != rs) {

				try {

					rs.close();

				} catch (SQLException e) {

					logger.error(e.getMessage(), e);

				}

			}
		}

		return flag;

	}

	/**
	 * insert incoming email to db
	 * 
	 * @param dataArray
	 */
	public final void insert(String body, String appNumber) {

		if (appNumber.length() > 0) {

			if (checkConnection()) {

				if (null != this.connection) {

					try {

						this.connection.close();

					} catch (SQLException e) {

						logger.error("Can't close connection! %s",
								e.getMessage(), e);

					}

					this.connection = null;

					setConnection();
				}
			}

			if (select(appNumber)) {

				update(body, appNumber);

			} else {

				PreparedStatement preparedStatement = null;

				try {

					String insert = "insert into sbb_monitor.email_log (APP_NUMBER, MSG_TEXT, RECEIVED_TIME, COUNT_CALL, LAST_TIME) values (?,?,sysdate, 1, sysdate)";

					preparedStatement = this.connection
							.prepareStatement(insert);

					preparedStatement.setString(1, appNumber);

					preparedStatement.setString(2, body);

					preparedStatement.executeUpdate();

					logger.debug("Insert success! ");

				} catch (SQLException e) {

					logger.error("Insert failed. %s", e.getMessage(), e);

				} finally {

					if (null != preparedStatement) {

						try {

							preparedStatement.close();

						} catch (SQLException e) {

							logger.error(e.getMessage(), e);

						}

					}

				}

			}

		}

	}

	private static class LasyDbHolder {

		public static dataBaseHelper dbInstance = new dataBaseHelper();

	}

}
