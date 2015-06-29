package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

public class MySQLDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("BOOL", Messages.getResourceString("type.boolean"), false, Types.BOOLEAN),
		new ColumnType("BOOLEAN", Messages.getResourceString("type.boolean"), false, Types.BOOLEAN),
		new ColumnType("BIT", Messages.getResourceString("type.bit"), false, Types.BIT),
		new ColumnType("TINYINT", Messages.getResourceString("type.integer"), true, Types.TINYINT),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"),true, Types.SMALLINT),
		new ColumnType("MEDIUMINT", Messages.getResourceString("type.integer"),true, Types.INTEGER),
		new ColumnType("INT", Messages.getResourceString("type.integer"),true, Types.INTEGER),
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"),true, Types.INTEGER),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"),true, Types.BIGINT),
		new ColumnType("FLOAT", Messages.getResourceString("type.real"), true, Types.FLOAT),
		new ColumnType("DOUBLE", Messages.getResourceString("type.real"), true, Types.DOUBLE),
		new ColumnType("DECIMAL", Messages.getResourceString("type.real"), true, Types.DECIMAL),
		new ColumnType("DEC", Messages.getResourceString("type.real"), true, Types.DECIMAL),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("DATETIME", Messages.getResourceString("type.datetime"), false, Types.DATE),
		new ColumnType("TIME", Messages.getResourceString("type.time"), false, Types.TIME),
		new ColumnType("DATETIME", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
		new ColumnType("YEAR", Messages.getResourceString("type.year"), false, Types.INTEGER),
		new ColumnType("CHAR", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("CHARACTER", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"), true, Types.VARCHAR),
		new ColumnType("BINARY", Messages.getResourceString("type.binary"),true, Types.BINARY),
		new ColumnType("VARBINARY", Messages.getResourceString("type.binary"),true, Types.VARBINARY),
		new ColumnType("BLOB", Messages.getResourceString("type.binary"), false, Types.BLOB),
		new ColumnType("TINYTEXT", Messages.getResourceString("type.string"), false, Types.VARCHAR),
		new ColumnType("TEXT", Messages.getResourceString("type.string"), false, Types.VARCHAR),
		new ColumnType("MEDIUMTEXT", Messages.getResourceString("type.string"), false, Types.VARCHAR),
		new ColumnType("LONGTEXT", Messages.getResourceString("type.string"), false, Types.VARCHAR),

		// TODO: Support following TYPE
		//   TINYBLOB
		//   MEDIUMBLOB
		//   ENUM
		//   SET
	};
	public MySQLDialect() {
		super(COLUMN_TYPES);
	}

	@Override
	protected void setupTableOption(RootModel root, TableModel model,
			boolean schema, boolean drop, boolean alterTable, boolean comment,
			StringBuilder additions, StringBuffer sb) {
		super.setupTableOption(root, model, schema, drop, alterTable, comment, additions, sb);
		if(comment) {
			if(model.getLogicalName() != null && model.getLogicalName().length() > 0) {
				sb.append(" COMMENT='").append(model.getLogicalName()).append("'");
			}
		}
	}

	@Override
	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment) {
		String ddl = super.createColumnDDL(root, tableModel, columnModel, schema, alterTable, additions, comment);
		if(columnModel.isAutoIncrement()){
			ddl += " AUTO_INCREMENT";
		}
		if(comment) {
			if(columnModel.getLogicalName() != null && columnModel.getLogicalName().length() > 0) {
				ddl += " COMMENT '" + columnModel.getLogicalName() + "'";
			}
		}
		return ddl;
	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		return "SELECT * FROM " + tableName + " LIMIT 0, 1";
	}

	@Override
	protected void createDropTableStatement(StringBuilder sb, String tableName) {
		sb.append("DROP TABLE IF EXISTS ").append(tableName).append(";");
	}

}
