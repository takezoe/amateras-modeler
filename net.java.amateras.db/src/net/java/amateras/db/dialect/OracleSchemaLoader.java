/**
 * Oracle Schema Loader
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

import net.java.amateras.db.util.StringUtils;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.TableModel;

public class OracleSchemaLoader extends DefaultSchemaLoader {

	@Override
	protected TableModel getTableInfo(String tableName, IDialect dialect,
			Connection conn, String catalog, String schema, boolean autoConvert) throws SQLException {

		TableModel table = new TableModel();
		table.setTableName(tableName);

		if(autoConvert){
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
			if(autoConvert){
				//column.setLogicalName(NameConverter.physical2logical(column.getColumnName()));
				column.setLogicalName(column.getColumnName());
			} else {
				column.setLogicalName(getColumnComment(tableName, columns.getString("COLUMN_NAME"), conn, catalog, schema));
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

	@Override
	protected List<IndexModel> loadIndexModels(String tableName, IDialect dialect,
			Connection conn, String catalog, String schema, List<ColumnModel> columns) throws SQLException {

		List<IndexModel> result = new ArrayList<IndexModel>();
		//DatabaseMetaData meta = conn.getMetaData();
		//ResultSet rs = meta.getIndexInfo(catalog, schema, tableName, false, true);
		ResultSet rs = getIndexInfo(conn, schema, tableName);
		while(rs.next()){
			String indexName = rs.getString("INDEX_NAME");
			if(indexName != null){
				IndexModel indexModel = null;
				for(IndexModel index: result){
					if(index.getIndexName().equals(indexName)){
						indexModel = index;
						break;
					}
				}
				if(indexModel == null){
					indexModel = new IndexModel();
					indexModel.setIndexName(indexName);
					indexModel.setIndexName(rs.getString("INDEX_NAME"));
					if(rs.getBoolean("NON_UNIQUE")){
						indexModel.setIndexType(new IndexType("INDEX"));
					} else {
						indexModel.setIndexType(new IndexType("UNIQUE"));
					}
					result.add(indexModel);
				}
				indexModel.getColumns().add(rs.getString("COLUMN_NAME"));
			}
		}
		rs.close();

		List<IndexModel> removeIndexModels = new ArrayList<IndexModel>();
		for(IndexModel indexModel: result){
			List<String> pkColumns = new ArrayList<String>();
			for(ColumnModel columnModel: columns){
				if(columnModel.isPrimaryKey()){
					pkColumns.add(columnModel.getColumnName());
				}
			}
			if(indexModel.getColumns().size() == pkColumns.size()){
				boolean isNotPk = false;
				for(int i=0;i<indexModel.getColumns().size();i++){
					if(!indexModel.getColumns().get(i).equals(pkColumns.get(i))){
						isNotPk = true;
						break;
					}
				}
				if(!isNotPk){
					removeIndexModels.add(indexModel);
				}
			}
		}
		result.removeAll(removeIndexModels);

		return result;
	}
	/**
	 * get Index Information
	 * @param conn
	 * @param schema
	 * @param tableName
	 * @return
	 * @throws SQLException
	 */
	private ResultSet getIndexInfo(Connection conn, String schema, String tableName) throws SQLException {
		StringBuffer query = new StringBuffer();
		query.append("SELECT NULL                                     AS table_cat				");
		query.append("	    ,i.owner                                  AS table_schem            ");
		query.append("	    ,i.table_name                             AS table_name             ");
		query.append("	    ,decode(i.uniqueness ,'UNIQUE' ,0 ,1)     AS non_unique             ");
		query.append("	    ,NULL                                     AS index_qualifier        ");
		query.append("	    ,i.index_name                             AS index_name             ");
		query.append("	    ,1                                        AS type                   ");
		query.append("	    ,c.column_position                        AS ordinal_position       ");
		query.append("	    ,c.column_name                            AS column_name            ");
		query.append("	    ,NULL                                     AS asc_or_desc            ");
		query.append("	    ,i.distinct_keys                          AS cardinality            ");
		query.append("	    ,i.leaf_blocks                            AS pages                  ");
		query.append("	    ,NULL                                     AS filter_condition       ");
		query.append("  FROM all_indexes     i                                                  ");
		query.append("	    ,all_ind_columns c                                                  ");
		query.append(" WHERE i.table_name    = ?                                    			");
		if(StringUtils.isNotEmpty(schema))
			query.append("   AND i.owner         = ?                                          		");
		else {
			query.append("   AND i.owner         = USER                                        		");
		}
		query.append("   AND i.index_name    = c.index_name                                     ");
		query.append("   AND i.table_owner   = c.table_owner                                    ");
		query.append("   AND i.table_name    = c.table_name                                     ");
		query.append("   AND i.owner         = c.index_owner                                    ");
		query.append(" ORDER BY non_unique ,type ,index_name ,ordinal_position                  ");

		//DBPlugin.logException(new Exception(query.toString()));

		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		pstmt.setString(1, tableName.toUpperCase());
		if(StringUtils.isNotEmpty(schema))
			pstmt.setString(2, schema.toUpperCase());

		return pstmt.executeQuery();
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
	 * get Table's Comments
	 */
	protected String getTableComment(String tableName,
			Connection conn, String catalog, String schema )  throws SQLException {
		String comment = tableName;	// default

		StringBuffer query = new StringBuffer();
		query.append("SELECT COMMENTS FROM ALL_TAB_COMMENTS WHERE TABLE_NAME = ? ");
		if(StringUtils.isNotEmpty(schema))
			query.append("AND OWNER = ?");
		else {
			query.append("AND OWNER = USER");
		}

		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		pstmt.setString(1, tableName.toUpperCase());
		if(StringUtils.isNotEmpty(schema))
			pstmt.setString(2, schema.toUpperCase());

		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			comment = rs.getString(1);
		}
		rs.close();
		pstmt.close();
		return StringUtils.isEmpty(comment) ? tableName : comment;
	}

	/**
	 * get Column's Comments
	 */
	protected String getColumnComment(String tableName, String columnName,
			Connection conn, String catalog, String schema )  throws SQLException {
		String comment = columnName;	// default

		StringBuffer query = new StringBuffer();
		query.append("SELECT COMMENTS FROM ALL_COL_COMMENTS WHERE TABLE_NAME = ? AND COLUMN_NAME = ? ");
		if(StringUtils.isNotEmpty(schema))
			query.append("AND OWNER = ?");
		else {
			query.append("AND OWNER = USER");
		}

		PreparedStatement pstmt = conn.prepareStatement(query.toString());
		pstmt.setString(1, tableName.toUpperCase());
		pstmt.setString(2, columnName.toUpperCase());
		if(StringUtils.isNotEmpty(schema))
			pstmt.setString(3, schema.toUpperCase());

		ResultSet rs = pstmt.executeQuery();
		if(rs.next()) {
			comment = rs.getString(1);
		}
		//DBPlugin.logException(new Exception("comments = "+ rs.getString(1)));
		rs.close();
		pstmt.close();
		return StringUtils.isEmpty(comment) ? columnName : comment;
	}
}
