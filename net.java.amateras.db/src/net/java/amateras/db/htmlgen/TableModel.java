package net.java.amateras.db.htmlgen;

import java.util.ArrayList;
import java.util.List;

public class TableModel extends AbstractDBEntityModel {

	private String error = "";
	private String linkedPath = "";
	private String tableName = "";
	private String logicalName = "";
	private String description = "";
	private ColumnModel[] columns = new ColumnModel[0];
	private IndexModel[] indices = new IndexModel[0];
	private String sql = "";
	private String schema;

	public static final String P_ERROR = "p_error";
	public static final String P_TABLE_NAME = "p_table_name";
	public static final String P_LOGICAL_NAME = "p_logical_name";
	public static final String P_COLUMNS = "p_columns";
	public static final String P_INDICES = "p_indices";
	public static final String P_CONSTRAINT = "p_constraint";
	public static final String P_LINKED_PATH = "p_linked_path";
	public static final String P_BACKGROUND_COLOR = "p_background_color";
	public static final String P_SCHEMA = "p_schema";


	@Override
	public boolean canSource(AbstractDBConnectionModel conn) {
		if(conn instanceof AnchorModel){
			if(conn.getTarget() != null && conn.getTarget() instanceof TableModel){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canTarget(AbstractDBConnectionModel conn) {
		if(conn instanceof AnchorModel){
			if(conn.getSource() instanceof TableModel){
				return false;
			}
		}
		return true;

	}

	public ColumnModel[] getPrimaryKeyColumns(){
		List<ColumnModel> primaryKeyColumns = new ArrayList<ColumnModel>();
		for(ColumnModel columnModel: getColumns()){
			if(columnModel.isPrimaryKey()){
				primaryKeyColumns.add(columnModel);
			}
		}
		return primaryKeyColumns.toArray(new ColumnModel[primaryKeyColumns.size()]);
	}

	public void setSql(String sql){
		this.sql = sql;
	}

	public String getSql(){
		if(this.sql == null){
			this.sql = "";
		}
		return this.sql;
	}

	public boolean isLinkedTable(){
		return getLinkedPath().length()!=0;
	}

	public String getLinkedPath(){
		if(this.linkedPath == null){
			this.linkedPath = "";
		}
		return this.linkedPath;
	}

	public void setLinkedPath(String linkedPath){
		this.linkedPath = linkedPath;
	}

	/**
	 * Returns error message about this table.
	 * @return error messages
	 */
	public String getError(){
		if(this.error == null){
			this.error = "";
		}
		return this.error;
	}

	/**
	 * Sets error messages about this table.
	 * @param error error messages
	 */
	public void setError(String error){
		this.error = error;
	}

	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
	}

	public String getTableName(){
		return this.tableName;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		if(this.description == null){
			this.description = "";
		}
		return this.description;
	}

	public void setColumns(ColumnModel[] columns){
		this.columns = columns;
	}

	public ColumnModel[] getColumns(){
		return this.columns;
	}

	public ColumnModel getColumn(String columnName){
		for(int i=0;i<columns.length;i++){
			if(columns[i].getColumnName().equals(columnName)){
				return columns[i];
			}
		}
		return null;
	}

	public IndexModel[] getIndices() {
		if(indices == null){
			indices = new IndexModel[0];
		}
		return indices;
	}

	public void setIndices(IndexModel[] indices) {
		this.indices = indices;
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}
