package net.java.amateras.db.dialect;

import java.sql.Types;

import net.java.amateras.db.Messages;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

public class PostgreSQLDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("BIGINT", Messages.getResourceString("type.integer"), false, Types.BIGINT),
		new ColumnType("BIT", Messages.getResourceString("type.bit"), true, Types.BIT),
		new ColumnType("VARBIT", Messages.getResourceString("type.bit"), true, Types.BIT),
		new ColumnType("BOOLEAN", Messages.getResourceString("type.boolean"),false, Types.BOOLEAN),
//		new ColumnType("BOX", Messages.getResourceString("type.integer"),false, Types.OTHER),
		new ColumnType("BYTEA", Messages.getResourceString("type.binary"),false, Types.BINARY),
		new ColumnType("VARCHAR", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		new ColumnType("CHARACTER", Messages.getResourceString("type.char"),true, Types.CHAR),
//		new ColumnType("CIDR", Messages.getResourceString("type.real"), false, Types.OTHER),
//		new ColumnType("CIRCLE", Messages.getResourceString("type.real"), false, Types.OTHER),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
//		new ColumnType("DOUBLE PRECISION", Messages.getResourceString("type.real"), false, Types.DOUBLE),
//		new ColumnType("INET", Messages.getResourceString("type.date"), false, Types.DOUBLE),
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"), false, Types.INTEGER),
//		new ColumnType("INTERVAL", Messages.getResourceString("type.time"), false, Types.OTHER),
//		new ColumnType("LINE", Messages.getResourceString("type.datetime"), false, Types.OTHER),
//		new ColumnType("LSEG", Messages.getResourceString("type.year"), false, Types.OTHER),
//		new ColumnType("MACADDR", Messages.getResourceString("type.char"), false, Types.OTHER),
//		new ColumnType("MONEY", Messages.getResourceString("type.char"), false, Types.OTHER),
		new ColumnType("NUMERIC", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
//		new ColumnType("PATH", Messages.getResourceString("type.binary"),false, Types.OTHER),
//		new ColumnType("POINT", Messages.getResourceString("type.binary"),false, Types.OTHER),
//		new ColumnType("POLYGON", Messages.getResourceString("type.string"), false, Types.OTHER),
		new ColumnType("REAL", Messages.getResourceString("type.real"), false, Types.REAL),
		new ColumnType("SMALLINT", Messages.getResourceString("type.integer"), false, Types.SMALLINT),
		new ColumnType("TEXT", Messages.getResourceString("type.string"), false, Types.VARCHAR),
		new ColumnType("TIME", Messages.getResourceString("type.time"), false, Types.TIME),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.datetime"), false, Types.TIMESTAMP),
//		new ColumnType("TSQUERY", Messages.getResourceString("type.string"), false, Types.OTHER),
//		new ColumnType("TSVECTOR", Messages.getResourceString("type.string"), false, Types.OTHER),
//		new ColumnType("TXID_SNAPSHOT", Messages.getResourceString("type.string"), false, Types.OTHER),
//		new ColumnType("UUID", Messages.getResourceString("type.string"), false, Types.OTHER),
		new ColumnType("SERIAL", Messages.getResourceString("type.serial"), false, Types.INTEGER),
		new ColumnType("BIGSERIAL", Messages.getResourceString("type.serial"), false, Types.BIGINT),
		new ColumnType("XML", Messages.getResourceString("type.xml"), false, Types.SQLXML),
		new ColumnType("INTERVAL", Messages.getResourceString("type.interval"), false, Types.OTHER),
		new ColumnType("INET", Messages.getResourceString("type.networkaddress"), false, Types.OTHER),
		new ColumnType("CIDR", Messages.getResourceString("type.networkaddress"), false, Types.OTHER),
		new ColumnType("MACADDR", Messages.getResourceString("type.macaddress"), false, Types.OTHER),
	};

	public PostgreSQLDialect(){
		super(COLUMN_TYPES);
	}

	@Override
	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment) {

		StringBuffer sb = new StringBuffer();
		sb.append(columnModel.getColumnName());
		if(columnModel.isAutoIncrement()){
			if(columnModel.getColumnType().getName().equals("BIGINT")){
				sb.append(" BIGSERIAL");
			} else {
				sb.append(" SERIAL");
			}
		} else {
			sb.append(" ").append(columnModel.getColumnType().getName());
			if(columnModel.getColumnType().supportSize() && columnModel.getSize().length() > 0){
				sb.append("(").append(columnModel.getSize()).append(")");
			}
			if(columnModel.isNotNull()){
				sb.append(" NOT NULL");
			}
		}
		if(columnModel.getDefaultValue().length()!=0){
			sb.append(" DEFAULT ").append(columnModel.getDefaultValue());
		}
		if(columnModel.isPrimaryKey()){
			if(!alterTable && tableModel.getPrimaryKeyColumns().length == 1){
				sb.append(" PRIMARY KEY");
			}
		}
		return sb.toString();

	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		return super.getColumnMetadataSQL(tableName) + "  LIMIT 1";
	}

}
