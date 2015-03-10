package db;

import static tools.PropCheck.loggerInfo;
import static tools.PropCheck.loggerSevere;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.aplana.tools.OracleDB;

/**
 * DML operation for applications statuses from CKPIT
 * 
 * @author Maksim Stepanov
 * 
 */
public class DatabaseOperation {

	private Connection connection = null;

	private Lock lock = new ReentrantLock();

	private int operation;

	private ArrayList<String> dataArray = null;

	public DatabaseOperation() {

		if (null == this.connection) {

			try {

				initConnection();

			} catch (SQLException e) {

				loggerSevere
						.severe("Error: Failed connect to databases first time! "
								+ e.getMessage());

				e.printStackTrace();
			}

		}

	}

	public static DatabaseOperation getInstance() {

		return LasyDbHolder.dbInstance;

	}

	/**
	 * 
	 * Evaluation task
	 * 
	 * @param operation
	 * @param dataArray
	 */
	public void evalOperation(int operation, ArrayList<String> dataArray) {

		this.operation = operation;

		this.dataArray = dataArray;

		try {

			if (null != this.connection) {

				if (this.connection.isClosed()) {

					loggerInfo.info("Connection closed!");

					this.connection = null;

					this.lock.lock();

					try {

						initConnection();

					} catch (SQLException e) {

						loggerSevere
								.severe("Error: Failed connect to databases! "
										+ e.getMessage());

					} finally {

						this.lock.unlock();

					}

				}

			} else {

				this.lock.lock();

				try {

					initConnection();

				} catch (SQLException e) {

					loggerSevere.severe("Error: Failed connect to databases! "
							+ e.getMessage());

				} finally {

					this.lock.unlock();

				}

			}

			/*
			 * insert equal 1 update equal 2. DML operation for CKPIT
			 * applications statuses
			 */

			switch (this.operation) {
			case 1:
				insert(this.dataArray);
				break;
			case 2:
				update(this.dataArray);
				break;
			default:
				break;
			}

		} catch (SQLException e) {

			loggerSevere.severe("Can't connecting to DB! " + e.getMessage());

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * init connection to DB
	 * 
	 * @throws SQLException
	 * 
	 */
	private final void initConnection() throws SQLException {

		this.connection = OracleDB.getConn(DatabaseConn.ORA_DB_URL,
				DatabaseConn.ORA_USER, DatabaseConn.ORA_PASS);

		loggerInfo.info("Connected success!");

	}

	private final void insert(ArrayList<String> dataArray) {

		Date currentDate = new Date();

		PreparedStatement preparedStatement = null;

		try {

			String insert = "insert into sbb_monitor.ckpit_app ( RQUID, REQ_TIME , RESP_TIME, STATUS_CODE, STATUS_DESC, TYPE ) values (?,?,?,?,?,?)";

			preparedStatement = this.connection.prepareStatement(insert);

			preparedStatement.setString(1, dataArray.get(0));

			preparedStatement.setTimestamp(2,
					new Timestamp(currentDate.getTime()));

			preparedStatement.setTimestamp(3, null);

			preparedStatement.setString(4, null);

			preparedStatement.setString(5, null);

			preparedStatement.setString(6, dataArray.get(1));

			preparedStatement.executeUpdate();

			loggerInfo.info("Insert success! ");

		} catch (SQLException e) {

			loggerSevere.severe("Error: Failed insert data to databases! "
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

	private final void update(ArrayList<String> dataArray) {

		Date currentDate = new Date();

		PreparedStatement preparedStatement = null;

		try {

			String update = "update sbb_monitor.ckpit_app SET RESP_TIME = ?, STATUS_CODE = ?, STATUS_DESC = ?  WHERE RQUID = ?";

			preparedStatement = this.connection.prepareStatement(update);

			preparedStatement.setTimestamp(1,
					new Timestamp(currentDate.getTime()));

			preparedStatement.setString(2, dataArray.get(1));

			preparedStatement.setString(3, dataArray.get(2));

			preparedStatement.setString(4, dataArray.get(0));

			preparedStatement.executeUpdate();

			loggerInfo.info("Update success!");

		} catch (SQLException e) {

			loggerSevere.severe("Error: Failed update data to databases!");

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

	private static class LasyDbHolder {

		public static DatabaseOperation dbInstance = new DatabaseOperation();

	}

}
