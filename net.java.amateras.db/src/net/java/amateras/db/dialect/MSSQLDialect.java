package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

/**
 *
 * @since 1.0.8
 */
public class MSSQLDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("BIT", Messages.getResourceString("type.bit"), false, Types.BIT),
		new ColumnType("INT", Messages.getResourceString("type.integer"), false, Types.INTEGER),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), false, Types.BIGINT),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), false, Types.SMALLINT),
		new ColumnType("TINYINT", Messages.getResourceString("type.integer"), false, Types.TINYINT),
		new ColumnType("DECIMAL", Messages.getResourceString("type.numeric"), false, Types.DECIMAL),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), false, Types.NUMERIC),
		new ColumnType("MONEY", Messages.getResourceString("type.money"), false, Types.DECIMAL),
		new ColumnType("SMALLMONEY", Messages.getResourceString("type.money"), false, Types.DECIMAL),
		new ColumnType("FLOAT", Messages.getResourceString("type.real"), true, Types.FLOAT),
		new ColumnType("REAL", Messages.getResourceString("type.real"), false, Types.REAL),
		new ColumnType("DATETIME", Messages.getResourceString("type.date"), false, Types.DATE), // TIMESTAMPかも？
		new ColumnType("SMALLDATETIME", Messages.getResourceString("type.date"), false, Types.DATE), // TIMESTAMPかも？
		new ColumnType("CHAR", Messages.getResourceString("type.char"),true, Types.CHAR),
		new ColumnType("TEXT", Messages.getResourceString("type.text"),false, Types.VARCHAR),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("NCHAR", Messages.getResourceString("type.char") + Messages.getResourceString("type.unicode"),true, Types.NCHAR),
		new ColumnType("NTEXT", Messages.getResourceString("type.text") + Messages.getResourceString("type.unicode"),false, Types.VARCHAR),
		new ColumnType("NVARCHAR", Messages.getResourceString("type.string") + Messages.getResourceString("type.unicode"),true, Types.NVARCHAR),
		new ColumnType("BINARY", Messages.getResourceString("type.binary"),true, Types.BLOB),
		new ColumnType("VARBINARY", Messages.getResourceString("type.binary"), true, Types.BLOB),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.timestamp"), false, Types.BINARY),
		new ColumnType("SQL_VARIANT", Messages.getResourceString("type.variant"), false, Types.OTHER),
		new ColumnType("UNIQUEIDENTIFIER", Messages.getResourceString("type.guid"), false, Types.OTHER),
		new ColumnType("XML", Messages.getResourceString("type.xml"), false, Types.OTHER),
	};

	public MSSQLDialect() {
		super(COLUMN_TYPES, LS + "go");
	}

	@Override
	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment) {
		String ddl = super.createColumnDDL(root, tableModel, columnModel, schema, alterTable, additions, comment);
		if(columnModel.isAutoIncrement()){
			ddl += " IDENTITY";
		}
		return ddl;
	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		return "SELECT TOP 1 * FROM " + tableName  ;
	}


}
