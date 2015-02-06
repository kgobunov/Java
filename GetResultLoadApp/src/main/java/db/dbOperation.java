package db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import ru.aplana.app.Main;
import ru.aplana.tools.OracleDB;

/**
 * Get results from DB
 * 
 * @author Maksim Stepanov
 * 
 */
public class dbOperation {

	private static Connection connection = null;

	public dbOperation() {

		// set stable connection
		if (null == connection) {

			initConnection();

		}

	}

	/**
	 * 
	 * init connection to DB
	 * 
	 */
	private final void initConnection() {

		Lock lock = new ReentrantLock();

		lock.lock();

		try {

			connection = OracleDB.getConn(dbConn.ORA_DB_URL, dbConn.ORA_USER,
					dbConn.ORA_PASS);

			System.out.println("Connected success!");

		} catch (SQLException e) {

			System.out.println("Error: Failed connect to databases! "
					+ e.getMessage());
		} finally {

			lock.unlock();
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

			flag = connection.isClosed();

		} catch (SQLException e) {

			e.printStackTrace();
		}

		return flag;
	}

	/**
	 * 
	 * Close connection
	 * 
	 * @return connection status
	 */
	public void closeConnection() {

		try {

			if (null != connection) {

				connection.close();

				System.out.println("Connection closed!");

			}

		} catch (SQLException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 
	 * get time of test
	 * 
	 */
	private void getTime() {

		if (checkConnection()) {

			if (null != connection) {

				try {

					connection.close();

				} catch (SQLException e) {

					System.err.println("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				connection = null;

				initConnection();
			}
		}

		String select = "select trunc(time, 'MI') as time from PUT_SYSTEMS_STATISTICS group by trunc(time, 'MI') order by 1 asc";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			while (rs.next()) {

				Main.time.add(rs.getTimestamp("time"));

			}

		} catch (SQLException e) {

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

	}

	/**
	 * 
	 * get systems names
	 * 
	 * @return
	 */
	private ArrayList<String> getPfrOperation() {

		ArrayList<String> systemsName = new ArrayList<String>();

		if (checkConnection()) {

			if (null != connection) {

				try {

					connection.close();

				} catch (SQLException e) {

					System.err.println("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				connection = null;

				initConnection();
			}
		}

		String select = "select distinct operation_name from aplana_pfr_statistics";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			while (rs.next()) {

				systemsName.add(rs.getString("operation_name"));

			}

		} catch (SQLException e) {

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

		return systemsName;

	}

	/**
	 * 
	 * Count operations PFR
	 * 
	 */
	private void getTotalCountPFRByOperation() {

		if (checkConnection()) {

			if (null != connection) {

				try {

					connection.close();

				} catch (SQLException e) {

					System.err.println("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				connection = null;

				initConnection();
			}
		}

		String select = "select count(*) as count, p.operation_name as name from aplana_pfr_statistics p group by p.operation_name";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			while (rs.next()) {

				Main.countPFR.put(rs.getString("name"), rs.getString("count"));

			}

		} catch (SQLException e) {

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

	}

	/**
	 * Response time PFR
	 * 
	 * 
	 * @param name
	 */
	private void getStatResponseTimePFR(String name) {

		String select = "select avg(p.response_time) as avg, max(p.response_time) as max, min(p.response_time) as min  from aplana_pfr_statistics p where p.operation_name = '"
				+ name + "' and error_msg is null";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			ArrayList<String> count = new ArrayList<String>();

			while (rs.next()) {

				count.add(String.valueOf(rs.getInt("avg")));

				count.add(String.valueOf(rs.getInt("max")));

				count.add(String.valueOf(rs.getInt("min")));

			}

			if (count.size() > 0) {

				Main.operations.put(name, count);

			}

		} catch (SQLException e) {

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

	}

	/**
	 * 
	 * Get PFR stat
	 * 
	 */
	public void pfrStat() {

		getTotalCountPFRByOperation();

		ArrayList<String> operationNames = getPfrOperation();

		for (String name : operationNames) {

			getStatResponseTimePFR(name);

			pfrErrors(name);

		}
		
		getLoadingPFR();

	}

	/**
	 * 
	 * Loading PFR
	 * 
	 */
	private void getLoadingPFR() {

		String select = "select trunc(time, 'MI'), count(*) as PFR from aplana_pfr_statistics where operation_name = 'FindPerson[Search]' group by operation_name,trunc(time, 'MI') order by 1";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			while (rs.next()) {

				Main.countPfr.add(Integer.valueOf(rs.getString("PFR")));

			}

		} catch (SQLException e) {

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
	}

	/**
	 * 
	 * PFR errors
	 * 
	 * @param name
	 */
	private void pfrErrors(String name) {

		String select = "select trunc(s.time, 'MI') as time,  s.error_msg from aplana_pfr_statistics s where s.error_msg is not null and s.operation_name = '"
				+ name + "'";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			ArrayList<String> count = new ArrayList<String>();

			while (rs.next()) {

				Main.timeError.add(rs.getTimestamp("time"));

				count.add(rs.getString("error_msg"));

			}

			if (count.size() > 0) {

				Main.errors.put(name, count);

			}

		} catch (SQLException e) {

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

	}

	/**
	 * 
	 * get systems names
	 * 
	 * @return
	 */
	private ArrayList<String> getSystemsMq() {

		ArrayList<String> systemsName = new ArrayList<String>();

		if (checkConnection()) {

			if (null != connection) {

				try {

					connection.close();

				} catch (SQLException e) {

					System.err.println("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				connection = null;

				initConnection();
			}
		}

		String select = "select distinct system_name from PUT_SYSTEMS_STATISTICS";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			while (rs.next()) {

				systemsName.add(rs.getString("system_name"));

			}

		} catch (SQLException e) {

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

		return systemsName;

	}

	/**
	 * Get responses for mq systems
	 * 
	 * 
	 */
	private final void getStatResponseMQ(String query) {

		String select = query;

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = st.executeQuery();

			ResultSetMetaData metaData = rs.getMetaData();

			int countColumns = metaData.getColumnCount();

			for (int i = 2; i <= countColumns; i++) {

				ArrayList<String> count = new ArrayList<String>();

				rs.first();

				count.add(rs.getString(metaData.getColumnName(i)));

				while (rs.next()) {

					count.add(rs.getString(metaData.getColumnName(i)));

				}

				if (count.size() > 0) {

					Main.systemsResponses.put(metaData.getColumnName(i), count);

				}

			}

		} catch (SQLException e) {

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

	}

	/**
	 * Get error systems by minutes
	 * 
	 * 
	 */
	private final void errorSystemsByTime(String query) {

		String select = query;

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select,
					ResultSet.TYPE_SCROLL_INSENSITIVE,
					ResultSet.CONCUR_READ_ONLY);

			rs = st.executeQuery();
			
			ResultSetMetaData metaData = rs.getMetaData();

			int countColumns = metaData.getColumnCount();

			for (int i = 2; i <= countColumns; i++) {

				ArrayList<String> count = new ArrayList<String>();

				if (rs.first()) {
					
					count.add(rs.getString(metaData.getColumnName(i)));
					
				}

				while (rs.next()) {

					count.add(rs.getString(metaData.getColumnName(i)));

				}

				if (count.size() > 0) {

					Main.systemsError.put(metaData.getColumnName(i), count);

				}

			}

		} catch (SQLException e) {

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

	}

	/**
	 * 
	 * Put to mq by minutes
	 * 
	 * @param name
	 */
	private final void mqPut(String name) {

		String select = "select trunc(time, 'MI'), system_name, count(*) as "
				+ name + " from PUT_SYSTEMS_STATISTICS where system_name = '"
				+ name
				+ "' group by trunc(time, 'MI'), system_name order by 1 asc";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			ArrayList<String> count = new ArrayList<String>();

			while (rs.next()) {

				count.add(rs.getString(name));

			}

			if (count.size() > 0) {

				Main.systems.put(name, count);

			}

		} catch (SQLException e) {

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

	}

	/**
	 * Stat pass/fail for mq systems
	 * 
	 */
	private final void passFailMq() {

		String select = "select sysname, fail, pass, trunc(fail/(fail+pass) * 100, 3) as percent_errors from (select sysname, sum(fail) as fail, sum(pass) as pass   from (select sysname, case when state = 'false' then 1 else 0 end as fail, case when state = 'true' then 1 else 0 end as pass from aplana_mq_resp ) group by sysname order by 1)";

		Main.sqls.add(select);

		PreparedStatement st = null;

		ResultSet rs = null;

		try {

			st = connection.prepareStatement(select);

			rs = st.executeQuery();

			ResultSetMetaData metaData = rs.getMetaData();

			int countColumns = metaData.getColumnCount();

			ArrayList<String> columnsName = new ArrayList<String>();

			for (int i = 1; i <= countColumns; i++) {

				columnsName.add(metaData.getColumnName(i));

			}

			while (rs.next()) {

				ArrayList<String> count = new ArrayList<String>();

				String sysname = rs.getString(columnsName.get(0));

				count.add(rs.getString(columnsName.get(1)));

				count.add(rs.getString(columnsName.get(2)));

				count.add(rs.getString(columnsName.get(3)));

				if (count.size() > 0) {

					Main.systemsErrorPercent.put(sysname, count);

				}

			}

		} catch (SQLException e) {

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

	}

	/**
	 * 
	 * Get stat for mq systems
	 * 
	 */
	public final void mqStat() {

		ArrayList<String> sysNames = getSystemsMq();

		getTime();

		if (checkConnection()) {

			if (null != connection) {

				try {

					connection.close();

				} catch (SQLException e) {

					System.err.println("Can't close connection! "
							+ e.getMessage());

					e.printStackTrace();
				}

				connection = null;

				initConnection();
			}
		}

		String query = "";

		String sum = "";

		for (String name : sysNames) {

			mqPut(name);

			query += "case when sysname = '" + name + "' then 1 else 0 end as "
					+ name + ",";

			sum += "sum(" + name + ") as " + name + ",";

		}

		passFailMq();

		query = query.substring(0, query.length() - 1);

		sum = sum.substring(0, sum.length() - 1);

		errorSystemsByTime("select time_,"
				+ sum
				+ " from (select trunc(time, 'MI') as time_,"
				+ query
				+ " from aplana_mq_resp where state = 'false') group by time_ order by 1");

		getStatResponseMQ("select time_,"
				+ sum
				+ " from (select trunc(time, 'MI') as time_,"
				+ query
				+ " from aplana_mq_resp where state = 'true') group by time_ order by 1");

	}

}
