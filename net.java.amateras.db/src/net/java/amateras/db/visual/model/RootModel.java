package net.java.amateras.db.visual.model;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.FontPropertyDescriptor;
import net.java.amateras.db.util.FontPropertyDescriptor.FontDataWrapper;

import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * The root model of the ER diagram.
 *
 * @author Naoki Takezoe
 */
public class RootModel extends AbstractDBModel implements IPropertySource {

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
	private String fontData;

	public static final String P_MODE = "p_mode";
	public static final String P_JDBC_INFO = "p_jdbc_info";
	public static final String P_CHILDREN = "p_children";
	public static final String P_DOMMAINS = "p_dommains";
	public static final String P_FONT = "p_font";

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

		firePropertyChange(P_CHILDREN, null, null);
	}

	public FontData[] getFontData(){
		if(fontData == null){
			fontData = "";
		}
		return PreferenceConverter.basicGetFontData(fontData);
	}

	public void setFontData(FontData[] fontData){
		this.fontData = PreferenceConverter.getStoredRepresentation(fontData);
		firePropertyChange(P_FONT, null, fontData);
	}

	public String getJarFile() {
		return jarFile;
	}

	public void setJarFile(String jarFile) {
		this.jarFile = jarFile;
		firePropertyChange(P_JDBC_INFO, null, jarFile);
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}

	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
		firePropertyChange(P_JDBC_INFO, null, jdbcDriver);
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
		firePropertyChange(P_JDBC_INFO, null, jdbcUrl);
	}

	public String getJdbcUser() {
		return jdbcUser;
	}

	public void setJdbcUser(String jdbcUser) {
		this.jdbcUser = jdbcUser;
		firePropertyChange(P_JDBC_INFO, null, jdbcUser);
	}

	public String getJdbcPassword() {
		return jdbcPassword;
	}

	public void setJdbcPassword(String jdbcPassword) {
		this.jdbcPassword = jdbcPassword;
		firePropertyChange(P_JDBC_INFO, null, jdbcPassword);
	}

	public String getJdbcSchema() {
		return jdbcSchema;
	}

	public void setJdbcSchema(String jdbcSchema) {
		this.jdbcSchema = jdbcSchema;
		firePropertyChange(P_JDBC_INFO, null, jdbcSchema);
	}

	public String getJdbcCatalog() {
		return jdbcCatalog;
	}

	public void setJdbcCatalog(String jdbcCatalog) {
		this.jdbcCatalog = jdbcCatalog;
		firePropertyChange(P_JDBC_INFO, null, jdbcCatalog);
	}

	public boolean isIncludeView() {
		return includeView;
	}

	public void setIncludeView(boolean includeView) {
		this.includeView = includeView;
		firePropertyChange(P_JDBC_INFO, null, new Boolean(jdbcCatalog));
	}

	public void setLogicalMode(boolean logicalMode){
		this.logicalMode = logicalMode;
		firePropertyChange(P_MODE, null, new Boolean(logicalMode));
	}

	public boolean getLogicalMode(){
		return this.logicalMode;
	}

	public void addChild(AbstractDBEntityModel model){
		children.add(model);
		firePropertyChange(P_CHILDREN, null, model);
	}

	public void removeChild(AbstractDBEntityModel model){
		children.remove(model);
		firePropertyChange(P_CHILDREN, model, null);
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
		for(int i = 0; i < children.size(); i++){
			AbstractDBEntityModel child = children.get(i);
			if(child instanceof TableModel){
				TableModel table = (TableModel) child;
				if(table.getTableName().equals(tableName)){
					return table;
				}
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
				table.firePropertyChange(TableModel.P_COLUMNS, null, null);
			}
		}

		firePropertyChange(P_DOMMAINS, null, dommains);
	}

	public String getDialectName() {
		return dialectName;
	}

	public void setDialectName(String dialectName) {
		this.dialectName = dialectName;
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[]{
				new TextPropertyDescriptor(P_JDBC_INFO,
						DBPlugin.getResourceString("property.schema")),
				new FontPropertyDescriptor(P_FONT,
						DBPlugin.getResourceString("property.font"))
		};
	}

	public Object getPropertyValue(Object id) {
		if(id == P_JDBC_INFO){
			return getJdbcSchema();
		} else if(id == P_FONT){
			return new FontDataWrapper(getFontData());
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if(id == P_JDBC_INFO || id == P_FONT){
			return true;
		}
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
		if(id == P_JDBC_INFO){
			setJdbcSchema((String)value);
		} else if(id == P_FONT){
			setFontData(((FontDataWrapper) value).getFontData());
		}
	}

}
