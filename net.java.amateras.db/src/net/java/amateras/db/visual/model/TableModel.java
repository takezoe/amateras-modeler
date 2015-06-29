package net.java.amateras.db.visual.model;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class TableModel extends AbstractDBEntityModel implements ICloneableModel {

	private String error = "";
	private String linkedPath = "";
	private String tableName = "";
	private String logicalName = "";
	private String description = "";
	private ColumnModel[] columns = new ColumnModel[0];
	private IndexModel[] indices = new IndexModel[0];
	private RGB backgroundColor;
	private String sql = "";
	private String schema;

	public static final String P_ERROR = "p_error";
	public static final String P_TABLE_NAME = "p_table_name";
	public static final String P_LOGICAL_NAME = "p_logical_name";
	public static final String P_COLUMNS = "p_columns";
	public static final String P_INDICES = "p_indices";
	public static final String P_CONSTRAINT = "p_constraint";
	public static final String P_LINKED_PATH = "p_linked_path";
	public static final String P_BACKGROUND_COLOR = "p_background_color";
	public static final String P_SCHEMA = "p_schema";


	@Override
	public boolean canSource(AbstractDBConnectionModel conn) {
		if(conn instanceof AnchorModel){
			if(conn.getTarget() != null && conn.getTarget() instanceof TableModel){
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canTarget(AbstractDBConnectionModel conn) {
		if(conn instanceof AnchorModel){
			if(conn.getSource() instanceof TableModel){
				return false;
			}
		}
		return true;

	}

	public ColumnModel[] getPrimaryKeyColumns(){
		List<ColumnModel> primaryKeyColumns = new ArrayList<ColumnModel>();
		for(ColumnModel columnModel: getColumns()){
			if(columnModel.isPrimaryKey()){
				primaryKeyColumns.add(columnModel);
			}
		}
		return primaryKeyColumns.toArray(new ColumnModel[primaryKeyColumns.size()]);
	}

	public void setSql(String sql){
		this.sql = sql;
	}

	public String getSql(){
		if(this.sql == null){
			this.sql = "";
		}
		return this.sql;
	}

	public boolean isLinkedTable(){
		return getLinkedPath().length()!=0;
	}

	public String getLinkedPath(){
		if(this.linkedPath == null){
			this.linkedPath = "";
		}
		return this.linkedPath;
	}

	public void setLinkedPath(String linkedPath){
		this.linkedPath = linkedPath;
		firePropertyChange(P_LINKED_PATH, null, linkedPath);
	}

	/**
	 * Returns error message about this table.
	 * @return error messages
	 */
	public String getError(){
		if(this.error == null){
			this.error = "";
		}
		return this.error;
	}

	/**
	 * Sets error messages about this table.
	 * @param error error messages
	 */
	public void setError(String error){
		this.error = error;
		firePropertyChange(P_ERROR, null, error);
	}

	public String getLogicalName() {
		return logicalName;
	}

	public void setLogicalName(String logicalName) {
		this.logicalName = logicalName;
		firePropertyChange(P_LOGICAL_NAME, null, logicalName);
	}

	public void setTableName(String tableName){
		this.tableName = tableName;
		firePropertyChange(P_TABLE_NAME, null, tableName);
	}

	public String getTableName(){
		return this.tableName;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public String getDescription(){
		if(this.description == null){
			this.description = "";
		}
		return this.description;
	}

	public void setColumns(ColumnModel[] columns){
		this.columns = columns;
		firePropertyChange(P_COLUMNS, null, columns);
	}

	public ColumnModel[] getColumns(){
		return this.columns;
	}

	public ColumnModel getColumn(String columnName){
		for(int i=0;i<columns.length;i++){
			if(columns[i].getColumnName().equals(columnName)){
				return columns[i];
			}
		}
		return null;
	}

	public IndexModel[] getIndices() {
		if(indices == null){
			indices = new IndexModel[0];
		}
		return indices;
	}

	public void setIndices(IndexModel[] indices) {
		this.indices = indices;
		firePropertyChange(P_INDICES, null, indices);
	}
	
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
		firePropertyChange(P_SCHEMA, null, schema);
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
	    if(isLinkedTable()){
            return new IPropertyDescriptor[]{
                    new PropertyDescriptor(P_TABLE_NAME, DBPlugin.getResourceString("property.physicalTableName")),
                    new PropertyDescriptor(P_LOGICAL_NAME, DBPlugin.getResourceString("property.logicalTableName")),
                    new PropertyDescriptor(P_LINKED_PATH, DBPlugin.getResourceString("property.linkedPath")),
                    new PropertyDescriptor(P_SCHEMA, DBPlugin.getResourceString("property.schema"))
            };
	    } else {
    		return new IPropertyDescriptor[]{
    				new TextPropertyDescriptor(P_TABLE_NAME, DBPlugin.getResourceString("property.physicalTableName")),
    				new TextPropertyDescriptor(P_LOGICAL_NAME, DBPlugin.getResourceString("property.logicalTableName")),
    				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, DBPlugin.getResourceString("property.backgroundColor"))
    		};
	    }
	}

	@Override
	public Object getPropertyValue(Object id) {
		if(id == P_TABLE_NAME){
			return getTableName();
		} else if(id == P_LOGICAL_NAME){
			return getLogicalName();
		} else if(id == P_LINKED_PATH){
		    return getLinkedPath();
		} else if(id == P_BACKGROUND_COLOR){
			return getBackgroundColor();
		} else if(id == P_SCHEMA){
			return getSchema();
		}
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		if(id == P_TABLE_NAME || id == P_LOGICAL_NAME || id == P_BACKGROUND_COLOR || id == P_SCHEMA){
			return true;
		}
		return false;
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if(id == P_TABLE_NAME){
			setTableName((String) value);
		} else if(id == P_LOGICAL_NAME){
			setLogicalName((String) value);
		} else if(id == P_BACKGROUND_COLOR){
			setBackgroundColor((RGB) value);
		} else if(id == P_SCHEMA){
			setSchema((String) value);
		}
	}

	public RGB getBackgroundColor() {
		if(backgroundColor == null){
			backgroundColor = new RGB(255, 255, 206);
		}
		return backgroundColor;
	}

	public void setBackgroundColor(RGB backgroundColor) {
		this.backgroundColor = backgroundColor;
		firePropertyChange(P_BACKGROUND_COLOR, null, backgroundColor);
	}

	@Override
	public ICloneableModel clone(){
	    TableModel table = new TableModel();
	    table.setTableName(getTableName());
	    table.setLogicalName(getLogicalName());
	    table.setDescription(getDescription());
	    table.setLinkedPath(getLinkedPath());
	    table.setConstraint(new Rectangle(getConstraint()));
	    table.setBackgroundColor(getBackgroundColor());
	    table.setSchema(getSchema());

	    ColumnModel[] oldColumns = getColumns();
	    ColumnModel[] newColumns = new ColumnModel[oldColumns.length];
	    for(int i=0; i<oldColumns.length; i++){
	        newColumns[i] = new ColumnModel();
	        newColumns[i].setColumnName(oldColumns[i].getColumnName());
            newColumns[i].setLogicalName(oldColumns[i].getLogicalName());
            newColumns[i].setDescription(oldColumns[i].getDescription());
            newColumns[i].setDefaultValue(oldColumns[i].getDefaultValue());
            newColumns[i].setColumnType(oldColumns[i].getColumnType());
            newColumns[i].setNotNull(oldColumns[i].isNotNull());
            newColumns[i].setPrimaryKey(oldColumns[i].isPrimaryKey());
            newColumns[i].setDommain(oldColumns[i].getDommain());
            newColumns[i].setSize(oldColumns[i].getSize());
	    }
	    table.setColumns(newColumns);

	    // TODO Copy Index...?

	    return table;
	}

}
