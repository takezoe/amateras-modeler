package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.util.StringUtils;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

/**
 *
 * @since 1.0.8
 */
public class SybaseDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("BIT", Messages.getResourceString("type.bit"), true, Types.BIT),
		new ColumnType("INT", Messages.getResourceString("type.integer"), true, Types.INTEGER),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), true, Types.BIGINT),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), true, Types.SMALLINT),
		new ColumnType("TINYINT", Messages.getResourceString("type.integer"), true, Types.TINYINT),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
		new ColumnType("REAL", Messages.getResourceString("type.real"), false, Types.DOUBLE),
		new ColumnType("FLOAT", Messages.getResourceString("type.real"), false, Types.FLOAT),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.CLOB),
		new ColumnType("CHAR", Messages.getResourceString("type.char"),true, Types.CHAR),
		new ColumnType("DATETIME", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.date"), true, Types.TIMESTAMP),
		//new ColumnType("varbinary", Messages.getResourceString("type.binary"), true, Types.BLOB)
		new ColumnType("IMAGE", Messages.getResourceString("type.binary"), true, Types.BLOB)
	};

	public SybaseDialect() {
		super(COLUMN_TYPES, "GO");
	}

	@Override
	public ISchemaLoader getSchemaLoader() {
		return new SybaseSchemaLoader();
	}


	@Override
	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment) {
		String ddl = super.createColumnDDL(root, tableModel, columnModel, schema, alterTable, additions, comment);
		if(columnModel.isAutoIncrement()){
			ddl += " IDENTITY ";
		}
		if(StringUtils.isNotEmpty(columnModel.getLogicalName())) {
			ddl +="  /*" + columnModel.getLogicalName() + " */";
		}
		return ddl;
	}

	@Override
	protected void createDropTableStatement(StringBuilder sb, String tableName) {
		sb.append(" IF EXISTS (").append(LS);
		sb.append("     SELECT 1").append(LS);
		sb.append("     FROM sysobjects").append(LS);
		sb.append("     WHERE name = '").append(tableName).append("' ").append(LS);
		sb.append("     AND type = 'U'").append(LS);
		sb.append(" )").append(LS);
		sb.append(" DROP TABLE ").append(tableName).append(LS);
		sb.append(" GO").append(LS);
	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		return "SELECT TOP 1 * FROM " + tableName  ;
	}
}
