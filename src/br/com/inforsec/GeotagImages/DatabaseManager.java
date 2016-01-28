package br.com.inforsec.GeotagImages;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javax.sql.DataSource;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DatabaseManager {

	private Connection conn = null;
	private DataSource datasource = null;
	private Statement stmt = null;

	public DatabaseManager(String propertiesFile) throws IOException, SQLException {
		this.datasource = this.getDataSource(propertiesFile);
	}
	
	public void connect() throws SQLException {
		System.out.println("DatabaseManager: Connecting to the Database...");
		this.conn = this.datasource.getConnection();
		System.out.println("DatabaseManager: Connected");
		this.stmt = this.conn.createStatement();
	}

	public void disconnect() {
		try {
			if (!this.conn.isClosed())
				this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(GeotagImages.LOG);
		}
	}

	public ResultSet executeQuery(String query) {
		try {
			return this.stmt.executeQuery(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(GeotagImages.LOG);
		}
		return null;
	}

	public MysqlDataSource getDataSource(String propertiesFile)
			throws IOException, SQLException {
		Properties props = new Properties();
		FileInputStream fis = null;

		fis = new FileInputStream(propertiesFile);
		props.load(fis);

		MysqlDataSource mysqlDS = new MysqlDataSource();

		mysqlDS = new MysqlDataSource();
		mysqlDS.setURL(props.getProperty("MYSQL_URL"));
		mysqlDS.setUser(props.getProperty("MYSQL_USERNAME"));
		mysqlDS.setPassword(props.getProperty("MYSQL_PASSWORD"));

		return mysqlDS;
	}

	public int insert(String fields, String values) {

		String query = "INSERT INTO images(";

		query += fields + ") VALUES (" + values + ");";

		try {
			return this.stmt.executeUpdate(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace(GeotagImages.LOG);
		}

		return -1;
	}

	public ResultSet select(String tableName) throws SQLException {
		return this.select(tableName, "*", null, null);
	}

	public ResultSet select(String tableName, String fields)
			throws SQLException {
		return this.select(tableName, fields, null, null);
	}

	public ResultSet select(String tableName, String fields, String where,
			String orderBy) throws SQLException {
		ResultSet rs = null;

		String query = "SELECT " + fields + " FROM " + tableName;

		if (where != null)
			query += " WHERE " + where;
		if (orderBy != null)
			query += " ORDER BY " + orderBy;

		rs = this.stmt.executeQuery(query);

		return rs;
	}
}
