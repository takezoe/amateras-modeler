package net.java.amateras.db.dialect;

import java.sql.Connection;
import java.sql.SQLException;

import net.java.amateras.db.visual.model.RootModel;

/**
 * 
 * @author Naoki Takezoe
 */
public interface ISchemaLoader {
	
	public void loadSchema(RootModel root, IDialect dialect, Connection conn,
			String[] tables, String catalog, String schema, boolean autoConvert) throws SQLException;
	
}
