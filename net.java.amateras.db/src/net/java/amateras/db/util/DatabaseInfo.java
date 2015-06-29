package net.java.amateras.db.util;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseInfo {
	
	private String uri = "";
	private String user = "";
	private String password = "";
	private String catalog = "";
	private String schema = "";
	private Driver driver = null;
	private boolean enableView = false;
	private String productName = null;
	private boolean autoConvert = false;
	
	final public String POSTGRESQL = "PostgreSQL";
	final public String MYSQL = "MySQL";
	final public String HSQLDB = "HSQL Database Engine";
	final public String DERBY = "Apache Derby";
	final public String SYBASE = "Adaptive Server Enterprise";
	
	public DatabaseInfo(Class<?> driverClass) throws InstantiationException, IllegalAccessException {
		driver = (Driver) driverClass.newInstance();
	}

	public void setURI(String uri) {
		this.uri = uri;
	}
	
	public String getURI(){
		return this.uri;
	}

	public void setCatalog(String catalog) {
		this.catalog = catalog;
	}
	
	public String getCatalog(){
		return this.catalog;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword(){
		return this.password;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public String getSchema(){
		return this.schema;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	public String getUser(){
		return this.user;
	}

	public void setEnableView(boolean flag) {
		enableView = flag;
	}
	
	public boolean isEnableView(){
		return this.enableView;
	}
	
	public boolean isAutoConvert() {
		return autoConvert;
	}

	public void setAutoConvert(boolean autoConvert) {
		this.autoConvert = autoConvert;
	}

	/**
	 * Connect to the database and return the connection.
	 * 
	 * @return the JDBC connection
	 * @throws SQLException Connect error
	 */
	public Connection connect() throws SQLException {
		Properties p = new Properties();
		
		if(isSybase()) {
			p.setProperty( "JCONNECT_VERSION", "3" );
			p.setProperty( "charSet", "eucksc" );	// Database encoding
		}
		
		p.setProperty("user", user);
		p.setProperty("password", password);
		return driver.connect(uri, p);
	}
	
	public List<String> loadTables() throws SQLException {
		List<String> list = new ArrayList<String>();
		Connection con = null;
		
		try {
			con = connect();
			DatabaseMetaData meta = con.getMetaData();
			productName = meta.getDatabaseProductName();
			//DBPlugin.logException(new Exception(productName));
			
			if (isMSSQL()) {
				if (catalog.length() == 0) {
					catalog = "%";
				}
			}

			catalog = (catalog.length() == 0) ? null : catalog;
			schema  = (schema.length()  == 0) ? null : schema;
			ResultSet tables = meta.getTables(catalog, schema, "%",
					isOracle() ? new String[] { "TABLE", "VIEW", "SYNONYM" } : null);
			
			while (tables.next()) {
				String t = tables.getString("TABLE_TYPE");
				if ("TABLE".equals(t) || ("VIEW".equals(t) && enableView) || (isOracle() && "SYNONYM".equals(t))) {
					list.add(tables.getString("table_name"));
				}
			}

			tables.close();

			if (driver.getClass().getName().equals("org.hsqldb.jdbcDriver") && uri.indexOf("jdbc:hsqldb:hsql://")!=0) {
				Statement stmt = null;
				try {
					stmt = con.createStatement();
//					System.out.println("SHUTDOWN");
//					System.out.println(uri);
					stmt.executeUpdate("SHUTDOWN;");
				} finally {
					if(stmt!=null){
						stmt.close();
					}
				}
			}
		} finally {
			if (con != null){
				con.close();
			}
		}
		return list;

	}
	
	public String getProductName() {
		return productName;
	}

	public boolean isPostgreSQL() {
		return POSTGRESQL.equals(productName);
	}

	public boolean isMySQL() {
		return MYSQL.equals(productName);
	}

	public boolean isHSQLDB() {
		return HSQLDB.equals(productName);
	}

	public boolean isDerby() {
		return DERBY.equals(productName);
	}

	public boolean isMSSQL() {
		if (productName.toLowerCase().indexOf("microsoft") != -1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isSybase() {
		return SYBASE.equals(productName);
	}
	
	public boolean isOracle() {
		if (productName.toLowerCase().indexOf("oracl") != -1) {
			return true;
		} else {
			return false;
		}
	}

//	public void writeResultColumns(ResultSet cols) throws SQLException {
//		ResultSetMetaData m = cols.getMetaData();
//		int c = m.getColumnCount();
//
//		while (cols.next()) {
//			for (int k = 1; k < (c + 1); k++) {
//				System.out.println(m.getColumnName(k) + ":" + cols.getString(k));
//			}
//		}
//	}
	
}
