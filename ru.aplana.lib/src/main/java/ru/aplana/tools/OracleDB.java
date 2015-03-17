package ru.aplana.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

import oracle.jdbc.pool.OracleDataSource;

/**
 * 
 * Set establish connection to Oracle database and creating datasource
 * 
 * @author Maksim Stepanov
 * 
 */
public class OracleDB {

	/**
	 * Get connection to DB Oracle
	 * 
	 * @param connectionString
	 *            - Example connection string to DB Oracle -
	 *            jdbc:oracle:thin:/@HOST:PORT:SID
	 * @param user
	 *            - username to DB
	 * @param password
	 *            - password for user
	 * @return connection
	 * @author Maksim Stepanov
	 */
	public static Connection getConnection(String connectionString,
			String user, String password) {

		Connection connection = null;

		try {

			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());

			connection = DriverManager.getConnection(connectionString, user,
					password);

		} catch (SQLException e) {

			e.printStackTrace();

		}

		return connection;

	}

	/**
	 * 
	 * Get Oracle datasource
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 * @throws SQLException
	 */
	public static DataSource getOracleDataSource(String url, String user,
			String password) {

		OracleDataSource oracleDS = null;

		try {

			oracleDS = new OracleDataSource();

		} catch (SQLException e) {

			e.printStackTrace();

		}

		oracleDS.setURL(url);

		oracleDS.setUser(user);

		oracleDS.setPassword(password);

		return oracleDS;
	}

}
