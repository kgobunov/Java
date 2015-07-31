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
 * DML operation for applications statuses from CRM and TSM
 * 
 * @author Maksim Stepanov
 * 
 */
public class DbOperation {

	private Connection connection = null;

	private int operation;

	private ArrayList<String> dataArray = null;

	private Lock lock = new ReentrantLock();

	private static final Logger logger = LogManager
			.getFormatterLogger(DbOperation.class.getName());

	/**
	 * 
	 * default constructor
	 */
	private DbOperation() {

		// Set stable connection
		if (null == this.connection) {

			initConnection();

		}

	}

	public static DbOperation getInstance() {

		return LasyDbHolder.dbInstance;

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

			this.lock.lock();

			try {

				if (null != this.connection) {

					if (this.connection.isClosed()) {

						logger.info("Connection closed!");

						this.connection = null;

						initConnection();

					}

				} else {

					initConnection();

				}

			} finally {

				this.lock.unlock();

			}

			/*
			 * insert equal 1 update equal 2. DML operation for applications
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
	 */
	private final void initConnection() {

		this.connection = OracleDB.getConnection(DbConn.ORA_DB_URL,
				DbConn.ORA_USER, DbConn.ORA_PASS);

		logger.info("Connected success!");

	}

	/**
	 * 
	 * 
	 * 
	 * @param dataArray
	 */
	private final void insert(ArrayList<String> dataArray) {

		PreparedStatement preparedStatement = null;

		try {

			String insert = "insert into sbb_monitor.sbol_app ( RQUD, app_number , app_status, firstname, lastname, middlename, birthday, system, time ) values (?,?,?,?,?,?,?,?,?)";

			preparedStatement = this.connection.prepareStatement(insert);

			preparedStatement.setString(1, dataArray.get(0));

			preparedStatement.setInt(2, 0);

			preparedStatement.setInt(3, 99);

			preparedStatement.setString(4, dataArray.get(1));

			preparedStatement.setString(5, dataArray.get(2));

			preparedStatement.setString(6, dataArray.get(3));

			preparedStatement.setString(7, dataArray.get(4));

			preparedStatement.setString(8, "FSB");

			preparedStatement.setTimestamp(9,
					new Timestamp(new Date().getTime()));

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

		PreparedStatement preparedStatement = null;

		String appNumber = dataArray.get(2);

		int app = 0;

		if (appNumber.length() > 0) {

			app = Integer.parseInt(appNumber);

		}

		try {

			String update = "update sbb_monitor.sbol_app SET app_number = ?, app_status = ?, description = ?, err_code = ?  WHERE RQUD = ?";

			preparedStatement = this.connection.prepareStatement(update);

			preparedStatement.setInt(1, app);

			preparedStatement.setString(2, dataArray.get(1));

			preparedStatement.setString(3, dataArray.get(4));

			preparedStatement.setString(4, dataArray.get(3));

			preparedStatement.setString(5, dataArray.get(0));

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

		public static DbOperation dbInstance = new DbOperation();

	}

}
