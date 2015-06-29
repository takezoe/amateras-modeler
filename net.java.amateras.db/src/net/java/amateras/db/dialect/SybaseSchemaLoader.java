/**
 * Sybase Schema Loader(61UAT Server DB_AG all_comments)
 */
package net.java.amateras.db.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.StringUtils;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.TableModel;

public class SybaseSchemaLoader extends DefaultSchemaLoader {

	@Override
	protected TableModel getTableInfo(String tableName, IDialect dialect, 
			Connection conn, String catalog, String schema, boolean autoConvert) throws SQLException {
		
		boolean hasCommentTable = existsCommentTable(conn);
		
		TableModel table = new TableModel();
		table.setTableName(tableName);
		
		if(autoConvert || !hasCommentTable){
			//table.setLogicalName(NameConverter.physical2logical(table.getTableName()));
			table.setLogicalName(table.getTableName());
		} else {
			table.setLogicalName(getTableComment(tableName, conn, catalog, schema));
		}
		
		DatabaseMetaData meta = conn.getMetaData();
		
		List<ColumnModel> list = new ArrayList<ColumnModel>();
		
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(dialect.getColumnMetadataSQL(getTableName(tableName, schema)));
		ResultSetMetaData rm = rs.getMetaData();
		
		ResultSet columns = meta.getColumns(catalog, schema, tableName, "%");
		while(columns.next()){
			IColumnType type = dialect.getColumnType(columns.getString("TYPE_NAME"));
			if(type == null){
				type = dialect.getColumnType(columns.getInt("DATA_TYPE"));
				if(type == null){
					type = dialect.getDefaultColumnType();
				}
			}
			
			ColumnModel column = new ColumnModel();
			column.setColumnName(columns.getString("COLUMN_NAME"));
			if(autoConvert|| !hasCommentTable){
				//column.setLogicalName(NameConverter.physical2logical(column.getColumnName()));
				column.setLogicalName(column.getColumnName());
			} else {
				column.setLogicalName(getColumnComment(tableName, columns.getString("COLUMN_NAME"), conn, catalog, schema));
			}
			
			if(hasCommentTable){
				column.setDescription(getColumnDescription(tableName, columns.getString("COLUMN_NAME"), conn, catalog, schema));
			}
			
			column.setColumnType(type);
			column.setSize(columns.getString("COLUMN_SIZE"));
			column.setNotNull(columns.getString("IS_NULLABLE").equals("NO"));
			
			int rmIndex = getResultSetMetaDataIndex(rm, column.getColumnName());
			if(rmIndex > 0){
				column.setAutoIncrement(rm.isAutoIncrement(rmIndex));
			}
			
			list.add(column);
		}
		columns.close();
		
		ResultSet keys = meta.getPrimaryKeys(catalog, schema, tableName);
		while(keys.next()){
			String columnName = keys.getString("COLUMN_NAME");
			for(int i=0;i<list.size();i++){
				ColumnModel column = (ColumnModel)list.get(i);
				if(column.getColumnName().equals(columnName)){
					column.setPrimaryKey(true);
				}
			}
		}
		keys.close();
		
		rs.close();
		stmt.close();
		
		table.setColumns(list.toArray(new ColumnModel[list.size()]));
		
		List<IndexModel> indices = loadIndexModels(tableName, dialect, conn, catalog, schema, list);
		table.setIndices(indices.toArray(new IndexModel[indices.size()]));
		
		return table;
	}



	/**
	 * get Table's Name
	 * @param tabName
	 * @param schema
	 * @return
	 */
	private String getTableName(String tabName, String schema) {
		if(StringUtils.isNotEmpty(schema)) {
			return schema + "." + tabName;
		} else {
			return tabName;
		} 
	}
	
	/**
	 * has Comments Tables
	 * @param conn
	 * @return
	 * @throws SQLException
	 */
	private boolean existsCommentTable(Connection conn) throws SQLException {
		boolean exists = false;
		StringBuffer query = new StringBuffer();
		query.append("select name from sysobjects where name = 'all_comments' ");
		
		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			exists = true;
		} 	
		rs.close();
		pstmt.close();		
		return exists;
	}
	
	/**
	 * get Table's Comments
	 */
	protected String getTableComment(String tableName,  
			Connection conn, String catalog, String schema )  throws SQLException {
		String comment = tableName;	// default
		
		StringBuffer query = new StringBuffer();
		query.append("select table_name from all_comments where table_id = ? ");
		
		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		pstmt.setString(1, tableName);
		
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			comment = rs.getString(1);
		} 
		rs.close();
		pstmt.close();
		return StringUtils.isEmpty(comment) ? tableName : comment.trim();
	}
	
	/**
	 * get Column's Comments
	 */
	protected String getColumnComment(String tableName, String columnName,  
			Connection conn, String catalog, String schema )  throws SQLException {
		String comment = columnName;	// default
		
		StringBuffer query = new StringBuffer();
		query.append("select column_name from all_comments where table_id = ? and column_id = ? ");

		
		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		pstmt.setString(1, tableName);
		pstmt.setString(2, columnName);
		
		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			comment = rs.getString(1);
		}
		//DBPlugin.logException(new Exception("comments = "+ rs.getString(1)));
		rs.close();
		pstmt.close();
		return StringUtils.isEmpty(comment) ? columnName : comment.trim();
	}

	/**
	 * get Column's Description
	 */
	protected String getColumnDescription(String tableName, String columnName,  
			Connection conn, String catalog, String schema ) {
		String comment = "";	// default
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
		StringBuffer query = new StringBuffer();
		query.append("select description from all_comments where table_id = ? and column_id = ? ");

		
		pstmt = conn.prepareStatement(query.toString());
		pstmt.setString(1, tableName);
		pstmt.setString(2, columnName);
		
		rs = pstmt.executeQuery();
		if(rs.next()) {
			comment = rs.getString(1);
		}
		
		} catch(SQLException e) {
			DBPlugin.logException(new Exception("getColumnDescription error"));
		} finally {
			try {
				if(rs !=null) rs.close();
				if(pstmt != null) pstmt.close();
			} catch(Exception e) {}
		}
		return  StringUtils.isEmpty(comment) ? "": comment.trim();
	}
	
}
