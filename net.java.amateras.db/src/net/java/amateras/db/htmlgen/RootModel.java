package net.java.amateras.db.htmlgen;

import java.util.ArrayList;
import java.util.List;

/**
 * The root model of the ER diagram.
 *
 * @author Naoki Takezoe
 */
public class RootModel extends AbstractDBModel {

	private List<AbstractDBEntityModel> children = new ArrayList<AbstractDBEntityModel>();
	private List<DommainModel> dommains = new ArrayList<DommainModel>();
	private String dialectName = "";
	private boolean logicalMode = false;

	private String jarFile = "";
	private String jdbcDriver = "";
	private String jdbcUrl = "";
	private String jdbcUser = "";
	private String jdbcPassword = "";
	private String jdbcSchema = "";
	private String jdbcCatalog = "";
	private boolean includeView = false;

	public static final String P_MODE = "p_mode";
	public static final String P_JDBC_INFO = "p_jdbc_info";
	public static final String P_CHILDREN = "p_children";
	public static final String P_DOMMAINS = "p_dommains";

	public void copyFrom(RootModel model){
		setDialectName(model.getDialectName());
		setLogicalMode(model.getLogicalMode());
		setJarFile(model.getJarFile());
		setJdbcDriver(model.getJdbcDriver());
		setJdbcUrl(model.getJdbcUrl());
		setJdbcUser(model.getJdbcUser());
		setJdbcPassword(model.getJdbcPassword());
		setJdbcSchema(model.getJdbcSchema());
		setJdbcCatalog(model.getJdbcCatalog());
		setIncludeView(model.isIncludeView());

		children.clear();
		children.addAll(model.getChildren());

		dommains.clear();
		dommains.addAll(model.getDommains());
	}

	public String getJarFile() {
		return jarFile;
	}

	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getJdbcUser() {
		return jdbcUser;
	}

	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
	}

	public String getJdbcSchema() {
		return jdbcSchema;
	}

	public void setJdbcSchema(String jdbcSchema) {
		this.jdbcSchema = jdbcSchema;
	}

	public String getJdbcCatalog() {
		return jdbcCatalog;
	}

	public void setJdbcCatalog(String jdbcCatalog) {
		this.jdbcCatalog = jdbcCatalog;
	}

	public boolean isIncludeView() {
		return includeView;
	}

	public void setIncludeView(boolean includeView) {
		this.includeView = includeView;
	}

	public void setLogicalMode(boolean logicalMode){
		this.logicalMode = logicalMode;
	}

	public boolean getLogicalMode(){
		return this.logicalMode;
	}

	public void addChild(AbstractDBEntityModel model){
		children.add(model);
	}

	public void removeChild(AbstractDBEntityModel model){
		children.remove(model);
	}

	public List<AbstractDBEntityModel> getChildren(){
		return this.children;
	}

	/**
	 * @since 1.0.6
	 */
	public List<TableModel> getTables(){
		List<TableModel> result = new ArrayList<TableModel>();
		for(AbstractDBEntityModel model: getChildren()){
			if(model instanceof TableModel){
				result.add((TableModel) model);
			}
		}
		return result;
	}

	public TableModel getTable(String tableName){
		for(int i=0;i<children.size();i++){
			TableModel table = (TableModel)children.get(i);
			if(table.getTableName().equals(tableName)){
				return table;
			}
		}
		return null;
	}

	public List<DommainModel> getDommains(){
		if(this.dommains == null){
			this.dommains = new ArrayList<DommainModel>();
		}
		return this.dommains;
	}

	public void setDommains(List<DommainModel> dommains){
		this.dommains = dommains;
		for(AbstractDBEntityModel entity: getChildren()){
			if(entity instanceof TableModel){
				TableModel table = (TableModel) entity;
				for(ColumnModel column: table.getColumns()){
					if(column.getDommain() != null){
						for(DommainModel dommain: dommains){
							if(dommain.getId().equals(column.getDommain().getId())){
								column.setDommain(dommain);
								break;
							}
						}
					}
				}
			}
		}
	}

	public String getDialectName() {
		return dialectName;
	}

	public void setDialectName(String dialectName) {
		this.dialectName = dialectName;
	}

}
