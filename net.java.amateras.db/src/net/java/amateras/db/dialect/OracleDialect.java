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
 * @author Naoki Takezoe
 * @since 1.0.3
 */
public class OracleDialect extends AbstractDialect {

	private static final IColumnType[] COLUMN_TYPES = {
		new ColumnType("NUMBER", Messages.getResourceString("type.numeric"), true, Types.NUMERIC),
		new ColumnType("INTEGER", Messages.getResourceString("type.integer"), true, Types.INTEGER),
		new ColumnType("BINARY_FLOAT", Messages.getResourceString("type.bit"), false, Types.FLOAT),
		new ColumnType("DOUBLE PRECISION", Messages.getResourceString("type.real"), false, Types.DOUBLE),
		new ColumnType("VARCHAR2", Messages.getResourceString("type.string"),true, Types.VARCHAR),
		//new ColumnType("NVARCHAR2", Messages.getResourceString("type.string"),true, Types.NVARCHAR),
		new ColumnType("CHAR", Messages.getResourceString("type.char"),true, Types.CHAR),
		//new ColumnType("NCHAR", Messages.getResourceString("type.char"),true, Types.NCHAR),
		new ColumnType("CLOB", Messages.getResourceString("type.string"),true, Types.CLOB),
		new ColumnType("LONG", Messages.getResourceString("type.string"),false, Types.CLOB),
		new ColumnType("DATE", Messages.getResourceString("type.date"), false, Types.DATE),
		new ColumnType("TIMESTAMP", Messages.getResourceString("type.date"), true, Types.TIMESTAMP),
		new ColumnType("RAW", Messages.getResourceString("type.binary"), false, Types.BINARY),
		new ColumnType("BLOB", Messages.getResourceString("type.binary"), false, Types.BLOB),
	};

	public OracleDialect() {
		super(COLUMN_TYPES);
	}

	@Override
	public ISchemaLoader getSchemaLoader() {
		return new OracleSchemaLoader();
	}
	@Override
	public String createTableDDL(RootModel root, TableModel model,
			boolean schema, boolean drop, boolean alterTable, boolean comment,
			StringBuilder additions) {

		String ddl = super.createTableDDL(
				root, model, schema, drop, alterTable, comment, additions);

		StringBuilder sb = new StringBuilder();

		StringBuilder sbComment = new StringBuilder();

		// Oracle Table Comments
		if(comment) {
			sbComment.append("COMMENT ON TABLE ").append(model.getTableName()).append(" is ");
			sbComment.append("'").append(model.getLogicalName()).append("';");
			sbComment.append(LS);
		}
		for(ColumnModel column: model.getColumns()){
			if(column.isAutoIncrement()){
				String seqName = model.getTableName() + "_" + column.getColumnName() + "_SEQ";
				String triggerName = model.getTableName() + "_" + column.getColumnName() + "_TRG";

				if(drop){
					sb.append("DROP SEQUENCE ").append(seqName).append(";").append(LS);
					//sb.append("DROP TRIGGER ").append(triggerName).append(";").append(LS);
					sb.append(LS);
				}

				sb.append("CREATE SEQUENCE ");
				sb.append(seqName);
				sb.append(" NOMAXVALUE NOCACHE NOORDER NOCYCLE;").append(LS);

				sb.append(LS);

				sb.append("CREATE TRIGGER ");
				sb.append(triggerName).append(LS);
				sb.append("BEFORE INSERT ON ").append(model.getTableName()).append(LS);
				sb.append("FOR EACH ROW").append(LS);
				sb.append("BEGIN").append(LS);
				sb.append("IF :NEW.").append(column.getColumnName()).append(" IS NOT NULL THEN").append(LS);
				sb.append("  SELECT ").append(seqName).append(".NEXTVAL ");
				sb.append("INTO :NEW.").append(column.getColumnName()).append(" FROM DUAL;").append(LS);
				sb.append("END IF;").append(LS);
				sb.append("END;").append(LS);
			}

			if(comment) {
				if(column.getLogicalName() != null && column.getLogicalName().length() > 0) {
					sbComment.append("COMMENT ON COLUMN ").append(model.getTableName()).append(".");
					sbComment.append(column.getColumnName()).append(" is ");
					sbComment.append("'").append(column.getLogicalName()).append("';");
					sbComment.append(LS);
				}
			}
		}

		if(sb.length() > 0){
			ddl = ddl + LS + sb.toString();
		}

		if(sbComment.length() > 0) {
			ddl = ddl + LS + sbComment.toString();
		}

		ddl = ddl + LS;


		return ddl;
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


	@Override
	protected void createDropTableStatement(StringBuilder sb, String tableName) {
		sb.append("DROP TABLE ").append(tableName).append(" CASCADE CONSTRAINTS;");
	}

	@Override
	public String getColumnMetadataSQL(String tableName) {
		//DBPlugin.logException(new Exception("SELECT * FROM " + tableName));
		return "SELECT * FROM " + tableName + " WHERE ROWNUM = 1" ;
		//return super.getColumnMetadataSQL(tableName) + "  ROWNUM = 1";
	}

}
