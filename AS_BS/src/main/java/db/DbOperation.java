package db;

import java.sql.Connection;
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
				calcInsurance(this.dataArray);
				break;
			case 2:
				createInsurance(this.dataArray);
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
	 * Check exist application number
	 * 
	 * @param appNum
	 *            - application number
	 * @return
	 */
	private final boolean checkApplication(int appNum) {

		PreparedStatement ps = null;

		ResultSet rs = null;

		boolean result = false;

		String select = "select * from SBB_MONITOR.AS_BS_APP where app_number = ?";

		try {

			ps = this.connection.prepareStatement(select);

			ps.setInt(1, appNum);

			rs = ps.executeQuery();

			if (rs.next()) {

				result = true;

				logger.debug("Application %s was found!", appNum);

			}

		} catch (SQLException e) {

			logger.error("Can't get application! Error: %s", e.getMessage(), e);

		} finally {

			if (null != ps) {

				try {

					ps.close();

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

		return result;

	}

	/**
	 * 
	 * Insert or update info about calculated insurance
	 * 
	 * @param dataArray
	 */
	private final void calcInsurance(ArrayList<String> dataArray) {

		PreparedStatement preparedStatement = null;

		int appNum = Integer.parseInt(this.dataArray.get(1));

		if (!checkApplication(appNum)) {

			try {

				String insert = "insert into sbb_monitor.as_bs_app ( TIME, RQUD, app_number , CREDIT_AMOUNT, INSURANCE_PREMIUM, INSURANCE_AMOUNT, CALC_INSURANCE, CREATE_INSURANCE, CHANGE_INSURANCE, PRINT_INSURANCE, DISABLE_INSURANCE ) values (?,?,?,?,?,?,?,?,?,?,?)";

				preparedStatement = this.connection.prepareStatement(insert);

				preparedStatement.setTimestamp(1,
						new Timestamp(new Date().getTime()));

				preparedStatement.setString(2, dataArray.get(0));

				preparedStatement.setInt(3, appNum);

				preparedStatement.setString(4, this.dataArray.get(2));

				preparedStatement.setString(5, this.dataArray.get(3));

				preparedStatement.setString(6, this.dataArray.get(4));

				preparedStatement.setInt(7, 1);

				preparedStatement.setInt(8, 0);

				preparedStatement.setInt(9, 0);

				preparedStatement.setInt(10, 0);

				preparedStatement.setInt(11, 0);

				preparedStatement.executeUpdate();

				logger.info("Insert success! ");

			} catch (SQLException e) {

				logger.error("Failed insert data to databases! %s",
						e.getMessage(), e);

			} finally {

				if (null != preparedStatement) {

					try {

						preparedStatement.close();

					} catch (SQLException e) {

						logger.error(e.getMessage(), e);

					}

				}

			}

		} else {

			try {

				String update = "update sbb_monitor.as_bs_app SET Time =?, RQUD = ?, CREDIT_AMOUNT = ?, INSURANCE_PREMIUM = ?  WHERE APP_NUMBER = ?";

				preparedStatement = this.connection.prepareStatement(update);

				preparedStatement.setTimestamp(1,
						new Timestamp(new Date().getTime()));

				preparedStatement.setString(2, dataArray.get(0));

				preparedStatement.setString(3, this.dataArray.get(2));

				preparedStatement.setString(4, this.dataArray.get(3));

				preparedStatement.setInt(5, appNum);

				preparedStatement.executeUpdate();

				logger.info("Update exist application success!");

			} catch (SQLException e) {

				logger.error("Failed update exits application! %s",
						e.getMessage(), e);

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

	/**
	 * 
	 * Update info for insurance by app number
	 * 
	 * @param dataArray
	 */
	private final void createInsurance(ArrayList<String> dataArray) {

		PreparedStatement preparedStatement = null;

		String appNumber = dataArray.get(1);
		try {

			String update = "update sbb_monitor.as_bs_app SET RQUD = ?, CREDIT_AMOUNT = ?, INSURANCE_PREMIUM = ?, CREATE_INSURANCE=?  WHERE APP_NUMBER = ?";

			preparedStatement = this.connection.prepareStatement(update);

			preparedStatement.setString(1, dataArray.get(0));

			preparedStatement.setString(2, dataArray.get(2));

			preparedStatement.setString(3, dataArray.get(3));

			preparedStatement.setInt(4, 1);

			preparedStatement.setInt(5, Integer.parseInt(appNumber));

			int result = preparedStatement.executeUpdate();

			if (result == 0) {

				logger.error("App number %s not found", appNumber);

			} else {

				logger.info("Update success! App number %s", appNumber);

			}

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
