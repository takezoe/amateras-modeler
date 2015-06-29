package net.java.amateras.db.dialect;

import net.java.amateras.db.validator.DiagramErrors;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

/**
 * 
 * @author Naoki Takezoe
 */
public interface IDialect {
	
	/**
	 * Returns a column type from a given SQL type.
	 * <p>
	 * If corresponded column type is not found, this method returns null.
	 * 
	 * @param sqlType a sql type which is defined by {@link java.sql.Types}
	 * @return a column type
	 */
	public IColumnType getColumnType(int sqlType);
	
	/**
	 * Returns a column type from a given type name.
	 * <p>
	 * If corresponded column type is not found, this method returns null.
	 * 
	 * @param typeName a type name
	 * @return a column type
	 */
	public IColumnType getColumnType(String typeName);
	
	/**
	 * Returns a default column type.
	 * 
	 * @return a default column type
	 */
	public IColumnType getDefaultColumnType();
	
	/**
	 * Returns supported column types.
	 * 
	 * @return supported column types
	 */
	public IColumnType[] getColumnTypes();
	
	/**
	 * Returns supported index types.
	 * 
	 * @return supported index types
	 */
	public IIndexType[] getIndexTypes();
	
	/**
	 * Returns a default index type.
	 * 
	 * @return a default index type
	 */
	public IIndexType getDefaultIndexType();
	
	/**
	 * Returns a index type from a given type name.
	 * 
	 * @param typeName a type name
	 * @return a index type
	 */
	public IIndexType getIndexType(String typeName);

	/**
	 * Creates DDL from a given model.
	 * 
	 * @param model a root model of diagram
	 * @param schema if true, table names are generated with a schema name
	 * @param drop if true, this method generates drop statement
	 * @param alterTable if true, this method generates constrains as ALTER TABLE
	 * @param comment if true, this method add comment to DDL
	 * @return DDL that creates all tables
	 */
	public String createDDL(RootModel model, boolean schema, 
			boolean drop, boolean alterTable, boolean comment);
	
	/**
	 * Creates DDL that creates a given table.
	 * 
	 * @param root a root model of diagram
	 * @param model a table model
	 * @param schema if true, table names are generated with a schema name
	 * @param drop if true, this method generates drop statement
	 * @param alterTable if true, this method generates constrains as ALTER TABLE
	 * @param comment if true, this method add comment to DDL
	 * @param additions additional DDLs
	 * @return DDL that creates a given table
	 */
	public String createTableDDL(RootModel root, TableModel model, boolean schema, boolean drop, 
			boolean alterTable, boolean comment, StringBuilder additions);
	
	/**
	 * Returns an implementation of <code>ISchemaLoader</code>
	 * that used for reverse engineering.
	 * 
	 * @return an implementation of <code>ISchemaLoader</code>
	 */
	public ISchemaLoader getSchemaLoader();
	
	/**
	 * Validates diagram models.
	 * 
     * @param validation errors
	 * @param model the root model of the diagram
	 */
	public void validate(DiagramErrors errors, RootModel model);
	
	/**
	 * Returns SQL which selects all columns 
	 * of a given table to get table metadata for reverse engineering.
	 * 
	 * @param tableName a table name
	 * @return SQL which selects all columns of a given table
	 */
	public String getColumnMetadataSQL(String tableName);
}
