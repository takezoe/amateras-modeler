package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.Messages;
import net.java.amateras.db.validator.DiagramErrors;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

/**
 * 
 * @since 1.0.8
 */
public class DB2Dialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"), false, Types.INTEGER),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), false, Types.BIGINT),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), false, Types.SMALLINT),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
		new ColumnType("REAL", Messages.getResourceString("type.bit"), false, Types.FLOAT),
		new ColumnType("DOUBLE", Messages.getResourceString("type.real"), false, Types.DOUBLE),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("CHAR", Messages.getResourceString("type.char"),true, Types.CHAR),
		new ColumnType("CLOB", Messages.getResourceString("type.string"),true, Types.CLOB),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("TIME", Messages.getResourceString("type.date"), false, Types.TIME),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.date"), false, Types.TIMESTAMP),
		new ColumnType("BLOB", Messages.getResourceString("type.binary"), true, Types.BLOB),
	};	
	
	public DB2Dialect() {
		super(COLUMN_TYPES);
	}
	
	// TODO Should Oracle validation levels be customizable?
    @Override public void validate(DiagramErrors errors, RootModel model) {
        for(AbstractDBEntityModel entity: model.getChildren()){
            if(entity instanceof TableModel){
                TableModel table = (TableModel) entity;
                String tableName = table.getTableName();
                if(tableName.length() > 30){
                    errors.addError(DBPlugin.LEVEL_ERROR, table, 
                            DBPlugin.getResourceString("validation.error.oracle.tableNameLength"));
                }
                
                for(ColumnModel column: table.getColumns()){
                    String columnName = column.getColumnName();
                    if(columnName.length() > 30){
                        errors.addError(DBPlugin.LEVEL_ERROR, table, column, 
                                DBPlugin.getResourceString("validation.error.oracle.columnNameLength"));
                    }
                }
                
                for(IndexModel index: table.getIndices()){
                    String indexName = index.getIndexName();
                    if(indexName.length() > 30){
                        errors.addError(DBPlugin.LEVEL_ERROR, table, index, 
                                DBPlugin.getResourceString("validation.error.oracle.indexNameLength"));
                    }
                }
            }
        }
    }
	
	

}
