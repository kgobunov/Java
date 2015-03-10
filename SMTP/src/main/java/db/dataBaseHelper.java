package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.aplana.app.RunServer;
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

	public dataBaseHelper() {

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

				if (RunServer.debug) {

					RunServer.loggerInfo.info("Connected success!");

				}

			} catch (SQLException e) {

				RunServer.loggerSevere
						.severe("Error: Failed connect to databases! "
								+ e.getMessage());
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

			e.printStackTrace();
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

						RunServer.loggerSevere
								.severe("Can't close connection! "
										+ e.getMessage());

						e.printStackTrace();
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

				if (RunServer.debug) {

					RunServer.loggerInfo
							.info("Update successfully! APP_NUMBER: "
									+ appNumber);

				}

			} catch (SQLException e) {

				RunServer.loggerSevere.severe("Can't update email! "
						+ e.getMessage());

				e.printStackTrace();

			} finally {

				if (null != st) {

					try {

						st.close();

					} catch (SQLException e) {

						e.printStackTrace();
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

					RunServer.loggerSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
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

				if (RunServer.debug) {

					RunServer.loggerInfo.info("Calls for APP_NUMBER "
							+ appNumber + " : " + count);

				}

			}

		} catch (SQLException e) {

			RunServer.loggerSevere.severe("Can't check calls for " + appNumber
					+ "! " + e.getMessage());

			e.printStackTrace();

		} finally {

			if (null != st) {

				try {

					st.close();

				} catch (SQLException e) {

					e.printStackTrace();
				}
			}

			if (null != rs) {

				try {

					rs.close();

				} catch (SQLException e) {

					e.printStackTrace();
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

					RunServer.loggerSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
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

				if (RunServer.debug) {

					RunServer.loggerInfo.info("Email was found! APP_NUMBER: "
							+ appNumber);

				}

				flag = true;

			}

		} catch (SQLException e) {

			RunServer.loggerSevere.severe("Can't get email number " + appNumber
					+ "! " + e.getMessage());

			e.printStackTrace();

		} finally {

			if (null != st) {

				try {

					st.close();

				} catch (SQLException e) {

					e.printStackTrace();
				}
			}

			if (null != rs) {

				try {

					rs.close();

				} catch (SQLException e) {

					e.printStackTrace();
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

						RunServer.loggerSevere
								.severe("Can't close connection! "
										+ e.getMessage());

						e.printStackTrace();
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

					preparedStatement = this.connection.prepareStatement(insert);

					preparedStatement.setString(1, appNumber);

					preparedStatement.setString(2, body);

					preparedStatement.executeUpdate();

					if (RunServer.debug) {

						RunServer.loggerInfo.info("Insert success! ");

					}

				} catch (SQLException e) {

					RunServer.loggerSevere.severe("Insert failed. "
							+ e.getMessage());

					e.printStackTrace();

				} finally {

					if (null != preparedStatement) {

						try {

							preparedStatement.close();

						} catch (SQLException e) {

							e.printStackTrace();

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
