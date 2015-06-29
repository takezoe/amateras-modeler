package net.java.amateras.db.dialect;

import java.sql.Types;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.StringUtils;
import net.java.amateras.db.validator.DiagramErrors;
import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

public abstract class AbstractDialect implements IDialect {

	private IColumnType[] types;
	protected IIndexType[] indexTypes = {
			new IndexType("UNIQUE"),
			new IndexType("INDEX")
	};

	protected static final String LS 	= System.getProperty("line.separator");
	protected static final String TAB = "\t\t";

	protected String separator;

	public AbstractDialect(IColumnType[] types){
		this(types, ";");
	}

	public AbstractDialect(IColumnType[] types, String separator){
		this.types = types;
		this.separator = separator;
	}

	public IColumnType getColumnType(int sqlType){
		for(int i=0;i<types.length;i++){
			if(sqlType == types[i].getType()){
				return types[i];
			}
		}
		return null;
	}

	public IColumnType getColumnType(String typeName){
		for(IColumnType type: types){
			if(type.getName().toUpperCase().equals(typeName.toUpperCase())){
				return type;
			}
		}
		return null;
	}

	public IColumnType getDefaultColumnType() {
		IColumnType[] types = getColumnTypes();
		for(int i=0;i<types.length;i++){
			if(types[i].getType() == Types.INTEGER){
				return types[i];
			}
		}
		return types[0];
	}

	public IColumnType[] getColumnTypes() {
		return types;
	}

	public IIndexType[] getIndexTypes() {
		return indexTypes;
	}

	public IIndexType getDefaultIndexType() {
		return getIndexTypes()[0];
	}

	public IIndexType getIndexType(String typeName) {
		for(IIndexType indexType: getIndexTypes()){
			if(indexType.getName().equals(typeName)){
				return indexType;
			}
		}
		return null;
	}

	public String createDDL(RootModel model, boolean schema, boolean drop,
			boolean alterTable, boolean comment) {
		List<TableModel> children = TableDependencyCalculator.getSortedTable(model);
		StringBuilder sb = new StringBuilder();
		StringBuilder additions = new StringBuilder();
		for(int i=children.size() - 1; i>=0; i--){
			TableModel table = children.get(i);
			if(drop && !table.isLinkedTable()){
				String tableName = getTableName(model, table, schema);
				createDropTableStatement(sb, tableName);
				sb.append(LS);
			}
		}
		if(drop){
			sb.append(LS);
		}

		for(TableModel table: children){
		    if(!table.isLinkedTable()){
		        sb.append(createTableDDL(model, table, schema, drop, alterTable, comment, additions));
		        sb.append(LS);
		    }
		}

		if(additions.length() > 0){
			sb.append(LS);
			sb.append(additions.toString());
		}

		for(TableModel table: children){
			String sql = table.getSql();
			if(sql != null && sql.trim().length() != 0){
				sb.append(LS);
				sb.append(sql);
				sb.append(LS);
			}
		}

		return sb.toString();
	}

	protected void createDropTableStatement(StringBuilder sb, String tableName) {
		sb.append("DROP TABLE IF EXISTS ").append(tableName).append(";");
	}

	protected String getTableName(RootModel root, TableModel table, boolean schema){
		if(schema){
			if(table.isLinkedTable() && StringUtils.isNotEmpty(table.getSchema())){
				return table.getSchema() + "." + table.getTableName();
			}
			if(StringUtils.isNotEmpty(root.getJdbcSchema())){
				return root.getJdbcSchema() + "." + table.getTableName();
			}
		}

		return table.getTableName();
	}

	public String createTableDDL(RootModel root, TableModel model,
			boolean schema, boolean drop, boolean alterTable, boolean comment,
			StringBuilder additions) {

		int additionsLength = additions.length();
		StringBuffer sb = new StringBuffer();

		String logicalName = model.getLogicalName();

		if(comment) {
			sb.append("/**********************************/").append(LS);
			sb.append("/* ");
			if (logicalName != null && logicalName.length() > 0) {
				sb.append(DBPlugin.getResourceString("ddl.tableName")).append(": ");
				sb.append(logicalName);
			} else {
				sb.append(DBPlugin.getResourceString("ddl.tableName")).append(": ");
				sb.append(getTableName(root, model, schema));
			}
			sb.append(" */").append(LS);
			sb.append("/**********************************/").append(LS);
		}

		sb.append("CREATE TABLE ").append(getTableName(root, model, schema))
				.append("(" + LS);
		ColumnModel[] columns = model.getColumns();
		for (int i = 0; i < columns.length; i++) {
			if (i != 0) {
				sb.append("," + LS);
			}
			sb.append(TAB);
			sb.append(createColumnDDL(root, model, columns[i], schema, alterTable, additions, comment));
		}

		ColumnModel[] primaryKeys = model.getPrimaryKeyColumns();

		if (alterTable && primaryKeys.length != 0) {
			String pkName = "IDX_" + model.getTableName() +"_PK" ;
			StringBuilder pkBuf = new StringBuilder();
			pkBuf.append("ALTER TABLE ").append(
					getTableName(root, model, schema));
			pkBuf.append(" ADD CONSTRAINT ").append(pkName).append(" PRIMARY KEY (");
			for (int i = 0; i < primaryKeys.length; i++) {
				if (i != 0) {
					pkBuf.append(", ");
				}
				pkBuf.append(primaryKeys[i].getColumnName());
			}
			pkBuf.append(")").append(separator).append(LS);
			additions.insert(additionsLength, pkBuf.toString());
		}
		///////////////////////////////////////////////////////////////////
		// Primary Key
		///////////////////////////////////////////////////////////////////
		if (!alterTable && primaryKeys.length > 1) {
			sb.append(",").append(LS);
			sb.append("  PRIMARY KEY (");
			for (int i = 0; i < primaryKeys.length; i++) {
				if (i != 0) {
					sb.append(", ");
				}
				sb.append(primaryKeys[i].getColumnName());
			}
			sb.append(")");
		}

		///////////////////////////////////////////////////////////////////
		// Foreign Key
		///////////////////////////////////////////////////////////////////
		List<AbstractDBConnectionModel> connList = model
				.getModelSourceConnections();
		LOOP: for(int i = 0; i < connList.size(); i++) {

			AbstractDBConnectionModel conn = connList.get(i);

			if (conn instanceof ForeignKeyModel) {

				ForeignKeyModel fk = (ForeignKeyModel) connList.get(i);
				ForeignKeyMapping[] mappings = fk.getMapping();

				for(ForeignKeyMapping mapping : mappings) {
					if(mapping.getRefer() == null || mapping.getTarget() == null){
						continue LOOP;
					}
				}

				TableModel target = (TableModel) conn.getTarget();

				StringBuilder fkBuf = new StringBuilder();
				fkBuf.append("FOREIGN KEY (");
				for(int j = 0; j < mappings.length; j++) {
					if(j != 0) {
						fkBuf.append(",");
					}
					fkBuf.append(mappings[j].getRefer().getColumnName());
				}
				fkBuf.append(") REFERENCES ");
				fkBuf.append(getTableName(root, target, schema));
				fkBuf.append(" (");
				for(int j = 0; j < mappings.length; j++) {
					if(j != 0) {
						fkBuf.append(",");
					}
					fkBuf.append(mappings[j].getTarget().getColumnName());
				}
				fkBuf.append(")");

				if(alterTable){
					String fkName = "IDX_" + model.getTableName() + "_FK" + i;
					additions.append("ALTER TABLE ").append(getTableName(root, model, schema));
					additions.append(" ADD CONSTRAINT ").append(fkName).append(" ");
					additions.append(fkBuf.toString()).append(separator).append(LS);;
				} else {
					sb.append("," + LS + "  ");
					sb.append(fkBuf.toString());
				}
			}
		}

		///////////////////////////////////////////////////////////////////
		// Index
		///////////////////////////////////////////////////////////////////
		for(int index = 0; index < model.getIndices().length; index++){
			IndexModel indexModel = model.getIndices()[index];
			if(indexModel.getIndexType().getName().equals("UNIQUE")){
				StringBuilder indexBuf = new StringBuilder();

				indexBuf.append("CONSTRAINT ").append(indexModel.getIndexName()).append(" UNIQUE ");
				indexBuf.append("(");
				for(int i = 0; i < indexModel.getColumns().size(); i++){
					if(i != 0){
						indexBuf.append(", ");
					}
					indexBuf.append(indexModel.getColumns().get(i));
				}
				indexBuf.append(")");

				if(alterTable){
					additions.append("ALTER TABLE ").append(getTableName(root, model, schema)).append(" ");
					additions.append("ADD ").append(indexBuf.toString()).append(separator).append(LS);

				} else {
					sb.append("," + LS + "  ");
					sb.append(indexBuf.toString());
				}
			} else {
				additions.append("CREATE INDEX ").append(indexModel.getIndexName()).append(" ");
				additions.append("ON ").append(getTableName(root, model, schema)).append(" (");
				for(int i = 0; i < indexModel.getColumns().size(); i++){
					if(i != 0){
						additions.append(", ");
					}
					additions.append(indexModel.getColumns().get(i));
				}
				additions.append(")").append(separator).append(LS);
			}
		}

		sb.append(LS);
		sb.append(")");
		setupTableOption(root, model, schema, drop, alterTable, comment, additions, sb);
		sb.append(separator).append(LS);;

		if(additions.length() != additionsLength){
			additions.append(LS);
		}

		return sb.toString();
	}

	protected String createColumnDDL(RootModel root, TableModel tableModel,
			ColumnModel columnModel, boolean schema, boolean alterTable, StringBuilder additions, boolean comment){
		StringBuffer sb = new StringBuffer();
		sb.append(StringUtils.rpad(columnModel.getColumnName(), 30) );
		sb.append(TAB).append(columnModel.getColumnType().getName());
		if(columnModel.getColumnType().supportSize() && columnModel.getSize().length() > 0){
			sb.append("(").append(columnModel.getSize()).append(")");
		}
		if(columnModel.getDefaultValue().length()!=0){
			sb.append(TAB).append(" DEFAULT ").append(columnModel.getDefaultValue());
		}
		if(columnModel.isNotNull()){
			sb.append(TAB).append(" NOT NULL");
		} else {
			sb.append(TAB).append(" NULL ");
		}

		if(columnModel.isPrimaryKey()){
			if(!alterTable && tableModel.getPrimaryKeyColumns().length == 1){
				sb.append(TAB).append(" PRIMARY KEY");
			}
		}
		return sb.toString();
	}

	protected void setupTableOption(RootModel root, TableModel model,
			boolean schema, boolean drop, boolean alterTable, boolean comment,
			StringBuilder additions, StringBuffer sb) {
	    // do nothing as default
	}

	public ISchemaLoader getSchemaLoader() {
		return new DefaultSchemaLoader();
	}

	public void validate(DiagramErrors errors, RootModel model){
	}

	public String getColumnMetadataSQL(String tableName) {
		return "SELECT * FROM \"" + tableName + "\"";
	}

}
