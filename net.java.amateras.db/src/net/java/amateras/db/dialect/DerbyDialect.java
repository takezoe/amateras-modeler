package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

public class DerbyDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("BOOLEAN", Messages.getResourceString("type.boolean"), true, Types.BOOLEAN),
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), true, Types.BIGINT),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("LONG VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("CHAR", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("CHARACTER", Messages.getResourceString("type.char"), true, Types.CHAR),
		new ColumnType("DECIMAL", Messages.getResourceString("type.real"), true, Types.DECIMAL),
		new ColumnType("DEC", Messages.getResourceString("type.real"), true, Types.DECIMAL),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
		new ColumnType("NUM", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
		new ColumnType("INT", Messages.getResourceString("type.integer"), true, Types.INTEGER),
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"), true, Types.INTEGER),
		new ColumnType("DOUBLE", Messages.getResourceString("type.real"), true, Types.DOUBLE),
		new ColumnType("FLOAT", Messages.getResourceString("type.real"), true, Types.FLOAT),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("TIME", Messages.getResourceString("type.time"), false, Types.TIME),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
		new ColumnType("REAL", Messages.getResourceString("type.real"), true, Types.REAL),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), true, Types.SMALLINT),
		//new ColumnType("TINYINT", Messages.getResourceString("type.integer"), true, Types.TINYINT),
		new ColumnType("CHARACTER VARYING FOR BIT DATA", Messages.getResourceString("type.binary"), true, Types.BINARY),
		new ColumnType("CHAR VARYING FOR BIT DATA", Messages.getResourceString("type.binary"), true, Types.BINARY),
		new ColumnType("VARCHAR FOR BIT DATA", Messages.getResourceString("type.binary"), true, Types.BINARY),
		// TODO: Support following TYPE
		//   BLOB
		//   CLOB
		//   CHARACTER LARGE OBJECT
		//   CHAR LARGE OBJECT
        //   DBCLOB
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
