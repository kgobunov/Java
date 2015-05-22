package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

	private static final Logger logger = LogManager
			.getFormatterLogger(DatabaseOperation.class.getName());

	private DatabaseOperation() {

		if (null == this.connection) {

			try {

				initConnection();

			} catch (SQLException e) {

				logger.error("Failed connect to databases first time! %s",
						e.getMessage(), e);

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

					logger.info("Connection closed!");

					this.connection = null;

					this.lock.lock();

					try {

						initConnection();

					} catch (SQLException e) {

						logger.error("Failed connect to databases! %s",
								e.getMessage(), e);

					} finally {

						this.lock.unlock();

					}

				}

			} else {

				this.lock.lock();

				try {

					initConnection();

				} catch (SQLException e) {

					logger.error("Failed connect to databases! %s",
							e.getMessage(), e);

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

			logger.error("Can't connecting to DB! %s", e.getMessage(), e);
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

		this.connection = OracleDB.getConnection(DatabaseConn.ORA_DB_URL,
				DatabaseConn.ORA_USER, DatabaseConn.ORA_PASS);

		logger.info("Connected success!");

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

			logger.info("Insert success! ");

		} catch (SQLException e) {

			logger.error("Failed insert data to databases! %s", e.getMessage(),
					e);

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

			logger.info("Update success!");

		} catch (SQLException e) {

			logger.error("Failed update data to databases! %s", e.getMessage(),
					e);

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

	private static class LasyDbHolder {

		public static DatabaseOperation dbInstance = new DatabaseOperation();

	}

}
