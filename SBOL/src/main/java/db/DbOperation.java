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
 * DML operation for applications statuses from CRM and TSM
 * 
 * @author Maksim Stepanov
 * 
 */
public class DbOperation {

	private Connection connection = null;

	private Lock lock = new ReentrantLock();

	private int operation;

	private ArrayList<String> dataArray = null;

	private DbOperation() {

		if (null == this.connection) {

			initConnection();

		}

	}

	public static DbOperation getInstance() {

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

					initConnection();

				}

			} else {

				initConnection();

			}

			/*
			 * insert equal 1 update equal 2. DML operation for ERIB
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
	 */
	private void initConnection() {

		this.lock.lock();

		try {

			this.connection = OracleDB.getConnection(DbConn.ORA_DB_URL,
					DbConn.ORA_USER, DbConn.ORA_PASS);

			loggerInfo.info("Connected success!");

		} finally {

			this.lock.unlock();
		}

	}

	private void insert(ArrayList<String> dataArray) {

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

			preparedStatement.setString(8, "SBOL");

			preparedStatement.setTimestamp(9,
					new Timestamp(new Date().getTime()));

			preparedStatement.executeUpdate();

			preparedStatement.close();

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

	private void update(ArrayList<String> dataArray) {

		PreparedStatement preparedStatement = null;

		try {

			String update = "update sbb_monitor.sbol_app SET app_number = ?, app_status = ?, description = ?, err_code = ?  WHERE RQUD = ?";

			preparedStatement = this.connection.prepareStatement(update);

			preparedStatement.setInt(1, Integer.valueOf(dataArray.get(2)));

			preparedStatement.setString(2, dataArray.get(1));

			preparedStatement.setString(3, dataArray.get(4));

			preparedStatement.setString(4, dataArray.get(3));

			preparedStatement.setString(5, dataArray.get(0));

			preparedStatement.executeUpdate();

			preparedStatement.close();

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

		public static DbOperation dbInstance = new DbOperation();

	}

}
