package connections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import ru.aplana.tools.OracleDB;
import tools.LoggerImplimentation;

/**
 * 
 * Classname: DatabaseHelper
 * 
 * Version: 1.0
 * 
 * 
 * Database operations
 * 
 * @author Maksim Stepanov
 * 
 */
public class DatabaseHelper {

	private Connection connection = null;

	private Logger dbInfo;

	private Logger dbSevere;

	private final int batchSize = 1000; // commit every 1000 call for send
										// message

	public static PreparedStatement st = null;

	private static AtomicInteger countSetBatch = new AtomicInteger(0);

	private static final Lock lockStatement = new ReentrantLock();

	private static final Lock lock = new ReentrantLock();

	public DatabaseHelper() {

		LoggerImplimentation loggers = new LoggerImplimentation(
				"logs\\DBLogger", 512000000, 2);

		this.dbInfo = loggers.getInfoLogger();

		this.dbSevere = loggers.getSevereLogger();

		this.dbInfo.info("DB info log started!");

		this.dbSevere.severe("DB severe log started!");

		setConnection();

		initStatment();

	}

	/**
	 * 
	 * Connect to DB
	 * 
	 */
	private void setConnection() {

		lock.lock();

		try {

			if (null == this.connection) {

				try {

					this.connection = OracleDB.getConn(Connections.ORA_URL,
							Connections.ORA_USER, Connections.ORA_PASSWORD);

					if (Connections.ORA_DEBUG) {

						this.dbInfo.info("Connected success!");

					}

				} catch (SQLException e) {

					this.dbSevere.severe("Error: Failed connect to databases! "
							+ e.getMessage());

				}
			}

		} finally {

			lock.unlock();
		}

	}

	public void disconnect() {

		try {

			this.connection.close();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		this.connection = null;

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
	 * 
	 * init statement
	 * 
	 */
	public final void initStatment() {

		String insert = "insert into aplana_mq_resp ( XML_DATA, SYSNAME, STATE, TIME ) values (?,?,?,?)";

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					this.dbSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				this.connection = null;

				setConnection();
			}
		}

		try {

			st = this.connection.prepareStatement(insert);

		} catch (SQLException e) {

			this.dbSevere.severe("Can't init statement! Error: "
					+ e.getMessage());

			e.printStackTrace();
		}

	}

	/**
	 * Save responses
	 * 
	 * @param request
	 * @param valid
	 * @param sysname
	 */
	public final void saveResponseStatus(String request, boolean valid,
			String sysname) {

		lockStatement.lock();

		try {

			Date currentDate = new Date();

			st.setBytes(1, request.getBytes());

			st.setString(2, sysname);

			st.setString(3, String.valueOf(valid));

			st.setTimestamp(4, new Timestamp(currentDate.getTime()));

			st.addBatch();

			countSetBatch.getAndIncrement();

			if (countSetBatch.get() % this.batchSize == 0) {

				st.executeBatch();

				countSetBatch.set(0);

				if (null != st) {

					st.close();

				}

				initStatment();

			}

		} catch (SQLException e) {

			this.dbSevere.severe("Can't add batch/close statement. Error: "
					+ e.getMessage());

			e.printStackTrace();

		} finally {

			lockStatement.unlock();

		}

	}

	/**
	 * insert result from as_request
	 * 
	 * @param dataArray
	 */
	public final void insert(String request, boolean valid, String sysname) {

		Date currentDate = new Date();

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					this.dbSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				this.connection = null;

				setConnection();
			}
		}

		PreparedStatement preparedStatement = null;

		try {

			if (null != this.connection) {

				String insert = "insert into aplana_mq_resp ( XML_DATA, SYSNAME, STATE, TIME ) values (?,?,?,?)";

				preparedStatement = this.connection.prepareStatement(insert);

				preparedStatement.setBytes(1, request.getBytes());

				preparedStatement.setString(2, sysname);

				preparedStatement.setString(3, String.valueOf(valid));

				preparedStatement.setTimestamp(4,
						new Timestamp(currentDate.getTime()));

				preparedStatement.executeUpdate();

				preparedStatement.close();

				if (Connections.ORA_DEBUG) {

					this.dbInfo.info("Insert success! ");

				}

			} else {

				this.dbInfo.info("Test end!");
			}

		} catch (SQLException e) {

			this.dbSevere.severe("Insert failed. " + e.getMessage());

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

	/**
	 * insert correlation ids
	 * 
	 * @param dataArray
	 */
	public final void insertCorrId(String corrID, String sysname) {

		Date currentDate = new Date();

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					this.dbSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				this.connection = null;

				setConnection();
			}
		}

		PreparedStatement preparedStatement = null;

		try {

			if (null != this.connection) {

				String insert = "insert into aplana_mq_corrid ( SYSNAME, CORR_ID, TIME_SEND ) values (?,?,?)";

				preparedStatement = this.connection.prepareStatement(insert);

				preparedStatement.setString(1, sysname);

				preparedStatement.setString(2, corrID);

				preparedStatement.setTimestamp(3,
						new Timestamp(currentDate.getTime()));

				preparedStatement.executeUpdate();

				preparedStatement.close();

				if (Connections.ORA_DEBUG) {

					this.dbInfo.info("Insert success! ");

				}

			} else {

				this.dbInfo.info("Test end!");
			}

		} catch (SQLException e) {

			this.dbSevere.severe("Insert failed. " + e.getMessage());

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

	/**
	 * insert correlation ids
	 * 
	 * @param dataArray
	 */
	public final void updateCorrIdResp(String corrID) {

		Date currentDate = new Date();

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					this.dbSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				this.connection = null;

				setConnection();
			}
		}

		PreparedStatement preparedStatement = null;

		try {

			if (null != this.connection) {

				String insert = "update aplana_mq_corrid set TIME_RESP = ? where CORR_ID = ?";

				preparedStatement = this.connection.prepareStatement(insert);

				preparedStatement.setTimestamp(1,
						new Timestamp(currentDate.getTime()));

				preparedStatement.setString(2, corrID);

				preparedStatement.executeUpdate();

				preparedStatement.close();

				if (Connections.ORA_DEBUG) {

					this.dbInfo.info("Update correlation ID success! ");

				}

			} else {

				this.dbInfo.info("Test end!");
			}

		} catch (SQLException e) {

			this.dbSevere.severe("Update correlation ID failed. "
					+ e.getMessage());

			this.dbSevere.severe("correlation ID  " + corrID);

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

	/**
	 * insert pfr statistics
	 * 
	 * @param dataArray
	 */
	public final void insertPut(ArrayList<Timestamp> times, String system) {

		int count = 0;

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					this.dbSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				this.connection = null;

				setConnection();
			}
		}

		PreparedStatement preparedStatement = null;

		try {

			if (null != this.connection) {

				String insert = "insert into PUT_SYSTEMS_STATISTICS ( TIME, SYSTEM_NAME ) values (?,?)";

				preparedStatement = this.connection.prepareStatement(insert);

				for (Timestamp time : times) {

					preparedStatement.setTimestamp(1, time);

					preparedStatement.setString(2, system);

					preparedStatement.addBatch();

					if (++count % this.batchSize == 0) {

						preparedStatement.executeBatch();

						this.dbInfo
								.info("Insert systems data success by batch! Count: "
										+ count);

					}

				}

				preparedStatement.executeBatch();

				preparedStatement.close();

				if (Connections.ORA_DEBUG) {

					this.dbInfo.info("Insert systems put data success! ");

				}

			} else {

				this.dbInfo.info("Test end!");
			}

		} catch (SQLException e) {

			this.dbSevere.severe("Insert system put data failed. "
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

	/**
	 * insert pfr statistics
	 * 
	 * @param dataArray
	 */
	public final void insertPfr(String operation, long response, String error) {

		Date currentDate = new Date();

		if (checkConnection()) {

			if (null != this.connection) {

				try {

					this.connection.close();

				} catch (SQLException e) {

					this.dbSevere.severe("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				this.connection = null;

				setConnection();
			}
		}

		PreparedStatement preparedStatement = null;

		try {

			if (null != this.connection) {

				String insert = "insert into aplana_pfr_statistics ( TIME, OPERATION_NAME, RESPONSE_TIME, ERROR_MSG ) values (?,?,?,?)";

				preparedStatement = this.connection.prepareStatement(insert);

				preparedStatement.setTimestamp(1,
						new Timestamp(currentDate.getTime()));

				preparedStatement.setString(2, operation);

				preparedStatement.setLong(3, response);

				preparedStatement.setString(4, error);

				preparedStatement.executeUpdate();

				preparedStatement.close();

				if (Connections.ORA_DEBUG) {

					this.dbInfo.info("Insert pfr data success! ");

				}

			} else {

				this.dbInfo.info("Test end!");
			}

		} catch (SQLException e) {

			this.dbSevere.severe("Insert pfr data failed. " + e.getMessage());

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
