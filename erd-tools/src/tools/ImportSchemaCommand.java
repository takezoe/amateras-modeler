package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

import net.java.amateras.db.dialect.DerbyDialect;
import net.java.amateras.db.dialect.HsqldbDialect;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.dialect.ISchemaLoader;
import net.java.amateras.db.dialect.MySQLDialect;
import net.java.amateras.db.dialect.OracleDialect;
import net.java.amateras.db.dialect.PostgreSQLDialect;
import net.java.amateras.db.util.DatabaseInfo;
import net.java.amateras.db.util.IOUtils;
import net.java.amateras.db.util.JarClassLoader;
import net.java.amateras.db.visual.editor.VisualDBSerializer;
import net.java.amateras.db.visual.model.RootModel;

/**
 * A command line tool to import database schema into a existing erd file.
 * <p>
 * <strong>Usage:</strong>
 * <pre>tools.ImportERDCommand erd-file</pre>
 * 
 * @author Naoki Takezoe
 */
public class ImportSchemaCommand {
	
	public static void main(String[] args) throws Exception {
		if(args.length != 1){
			System.err.println("Please specify the erd file!");
			System.exit(1);
		}
		
		File erdFile = new File(args[0]);
		if(!erdFile.exists() || !erdFile.isFile()){
			System.err.println("The specified erd file does not exist!");
			System.exit(1);
		}
		
		System.out.println("** Start to import from database **");
		
		FileInputStream in = new FileInputStream(erdFile);
		RootModel rootModel = VisualDBSerializer.deserialize(in);
		IOUtils.close(in);
		
		IDialect dialect = getDialect(rootModel.getDialectName());
		ISchemaLoader loader = dialect.getSchemaLoader();
		
		DatabaseInfo dbinfo = new DatabaseInfo(
				loadJdbcDriver(rootModel.getJarFile(), rootModel.getJdbcDriver()));
		dbinfo.setURI(rootModel.getJdbcUrl());
		dbinfo.setUser(rootModel.getJdbcUser());
		dbinfo.setPassword(rootModel.getJdbcPassword());
		dbinfo.setCatalog(rootModel.getJdbcCatalog());
		dbinfo.setSchema(rootModel.getJdbcSchema());
		List<String> tables = dbinfo.loadTables();
		
		Connection conn = null;
		try {
			conn = dbinfo.connect();
			loader.loadSchema(rootModel, dialect, conn, tables.toArray(new String[tables.size()]),
					dbinfo.getCatalog(), dbinfo.getSchema());
		} finally {
			if(conn != null){
				conn.close();
			}
		}
		
		FileOutputStream out = new FileOutputStream(erdFile);
		InputStream result = VisualDBSerializer.serialize(rootModel);
		IOUtils.copyStream(result, out);
		IOUtils.close(result);
		IOUtils.close(out);
		
		System.out.println("** Completed! **");		
	}
	
	private static Class<?> loadJdbcDriver(String jarPath, String driver) throws Exception {
		URL jarURL = new URL("file:///" + jarPath);
		JarClassLoader classLoader = new JarClassLoader(new URL[]{ jarURL });
		java.util.List<Class<?>> list = classLoader.getJDBCDriverClass(jarPath);
		for(Class<?> item: list){
			if(item.getName().equals(driver)){
				return item;
			}
		}
		throw new Exception("JDBC driver is not found!");
	}
	
	private static IDialect getDialect(String dialectName) throws Exception {
		if(dialectName.equals("hsqldb")){
			return new HsqldbDialect();
		} else if(dialectName.equals("Derby")){
			return new DerbyDialect();
		} else if(dialectName.equals("MySQL")){
			return new MySQLDialect();
		} else if(dialectName.equals("PostgreSQL")){
			return new PostgreSQLDialect();
		} else if(dialectName.equals("Oracle")){
			return new OracleDialect();
		}
		throw new Exception("Dialect is not found!");
	}
	
}
