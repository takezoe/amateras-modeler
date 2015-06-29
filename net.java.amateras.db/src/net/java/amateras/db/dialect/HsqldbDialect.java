package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

public class HsqldbDialect extends AbstractDialect {

//	private static final String DB_NAME = "hsqldb";

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("INT", Messages.getResourceString("type.integer"), false, Types.INTEGER),
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"), false, Types.INTEGER),
		new ColumnType("DOUBLE", Messages.getResourceString("type.real"), false, Types.DOUBLE),
		new ColumnType("FLOAT", Messages.getResourceString("type.real"), false, Types.FLOAT),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("VARCHAR_IGNORECASE", Messages.getResourceString("type.string"), true, Types.VARCHAR),
		new ColumnType("CHAR", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("CHARACTER", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("DECIMAL", Messages.getResourceString("type.real"), true, Types.DECIMAL),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
		new ColumnType("BOOLEAN", Messages.getResourceString("type.boolean"), true, Types.BOOLEAN),
		new ColumnType("BIT", Messages.getResourceString("type.bit"), true, Types.BIT),
		new ColumnType("TINYINT", Messages.getResourceString("type.integer"), true, Types.TINYINT),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), true, Types.SMALLINT),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), true, Types.BIGINT),
		new ColumnType("REAL", Messages.getResourceString("type.real"), true, Types.REAL),
		new ColumnType("BINARY", Messages.getResourceString("type.binary"), true, Types.BINARY),
		new ColumnType("VARBINARY", Messages.getResourceString("type.binary"), true, Types.BINARY),
		new ColumnType("LONGVARBINARY", Messages.getResourceString("type.binary"), true, Types.BINARY),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("TIME", Messages.getResourceString("type.time"), false, Types.TIME),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
		new ColumnType("DATETIME", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
		new ColumnType("OTHER", Messages.getResourceString("type.other"), false, Types.OTHER),
		new ColumnType("OBJECT", Messages.getResourceString("type.object"), false, Types.OTHER)
	};

	public HsqldbDialect(){
		super(COLUMN_TYPES);
	}

	@Override
	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment){
		StringBuffer sb = new StringBuffer();
		sb.append(columnModel.getColumnName());
		sb.append(" ").append(columnModel.getColumnType().getName());
		if(columnModel.getColumnType().supportSize() && columnModel.getSize().length() > 0){
			sb.append("(").append(columnModel.getSize()).append(")");
		}
		if(columnModel.getDefaultValue().length()!=0){
			sb.append(" DEFAULT ").append(columnModel.getDefaultValue());
		}
		if(columnModel.isNotNull()){
			sb.append(" NOT NULL");
		}
		if(columnModel.isAutoIncrement()){
			sb.append(" IDENTITY");
		}
		if(columnModel.isPrimaryKey()){
			if(!alterTable && tableModel.getPrimaryKeyColumns().length == 1){
				sb.append(" PRIMARY KEY");
			}
		}
		return sb.toString();
	}

	@Override
	protected void createDropTableStatement(StringBuilder sb, String tableName) {
		sb.append("DROP TABLE ").append(tableName).append(" IF EXISTS;");
	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		return super.getColumnMetadataSQL(tableName) + "  LIMIT 1";
	}
}
