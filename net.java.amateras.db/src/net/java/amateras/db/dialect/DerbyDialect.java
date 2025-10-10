package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

public class DerbyDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), false, Types.SMALLINT),
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"), false, Types.INTEGER),
		new ColumnType("INT", Messages.getResourceString("type.integer"), false, Types.INTEGER),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), false, Types.BIGINT),
		new ColumnType("DECIMAL", Messages.getResourceString("type.real"), true, Types.DECIMAL),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), false, Types.NUMERIC),
		new ColumnType("REAL", Messages.getResourceString("type.real"), false, Types.REAL),
		new ColumnType("DOUBLE PRECISION", Messages.getResourceString("type.real"), false, Types.DOUBLE),
		new ColumnType("DOUBLE", Messages.getResourceString("type.real"), false, Types.DOUBLE),
		new ColumnType("CHAR", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("LONG VARCHAR", Messages.getResourceString("type.string"),false, Types.VARCHAR),
		new ColumnType("CHAR FOR BIT DATA", Messages.getResourceString("type.binary"), true, Types.BINARY),
		new ColumnType("VARCHAR FOR BIT DATA", Messages.getResourceString("type.binary"), false, Types.BINARY),
		new ColumnType("CLOB", Messages.getResourceString("type.string"), false, Types.CLOB),
		new ColumnType("BLOB", Messages.getResourceString("type.binary"), false, Types.BLOB),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("TIME", Messages.getResourceString("type.time"), false, Types.TIME),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
		new ColumnType("BOOLEAN", Messages.getResourceString("type.boolean"), false, Types.BOOLEAN),
		// TODO: Support following TYPE
		//   XML
	};

	public DerbyDialect(){
		super(COLUMN_TYPES);
	}

	@Override
	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment) {
		String ddl = super.createColumnDDL(root, tableModel, columnModel, schema, alterTable, additions, comment);
		if(columnModel.isAutoIncrement()){
			ddl += " GENERATED ALWAYS AS IDENTITY";
		}
		return ddl;
	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		return super.getColumnMetadataSQL(tableName) + "  FETCH FIRST 1 ROW ONLY";
	}

	@Override
	protected void createDropTableStatement(StringBuilder sb, String tableName) {
		sb.append("DROP TABLE ").append(tableName).append(";");
	}

}
