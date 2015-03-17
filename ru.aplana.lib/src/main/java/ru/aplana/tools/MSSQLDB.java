package ru.aplana.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;

/**
 * 
 * Set establish connection to MSSQL database and creating datasource
 * 
 * @author Maksim Stepanov
 * 
 */
public class MSSQLDB {

	/**
	 * Returns connection to DB MSSql
	 * 
	 * @param connectionString
	 *            - Example connection string to DB MSSQL -
	 *            jdbc:sqlserver://host
	 *            \\SQLEXPRESS;databaseName=?;user=?;password=?
	 * @return connection - establish a connection to the MSSQL database.
	 * @author Maksim Stepanov
	 * 
	 */
	public static Connection getConnection(String connectionString) {

		Connection connection = null;

		try {

			DriverManager
					.registerDriver(new com.microsoft.sqlserver.jdbc.SQLServerDriver());

			connection = DriverManager.getConnection(connectionString);

		} catch (SQLException e) {

			e.printStackTrace();

		}

		return connection;

	}

	/**
	 * 
	 * Getting MSSql datasource
	 * 
	 * @param url
	 * @param user
	 * @param password
	 * @return
	 */
	public static DataSource getMsSqlDataSource(String url) {

		SQLServerDataSource mssqlDS = new SQLServerDataSource();

		mssqlDS.setURL(url);

		return mssqlDS;
	}

}
