package net.java.amateras.db.visual.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.amateras.db.DBPlugin;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class ForeignKeyModel extends AbstractDBConnectionModel {
	
	private String foreignKeyName = "";
	private Map<ColumnModel, ColumnModel> references = new HashMap<ColumnModel, ColumnModel>();
	public static final String P_FOREIGN_KEY_NAME = "p_foreign_key_name";
	public static final String P_FOREIGN_KEY_MAPPING = "p_foreign_key_mapping";
	
	public void setForeignKeyName(String foreignKeyName){
		this.foreignKeyName = foreignKeyName;
		firePropertyChange(P_FOREIGN_KEY_NAME, null, foreignKeyName);
	}
	
	public String getForeignKeyName(){
		return this.foreignKeyName;
	}
	
	public void setMapping(ForeignKeyMapping[] mapping){
		references.clear();
		for(int i=0;i<mapping.length;i++){
			references.put(mapping[i].getTarget(), mapping[i].getRefer());
		}
		firePropertyChange(P_FOREIGN_KEY_MAPPING, null, mapping);
	}
	
	public ForeignKeyMapping[] getMapping(){
		List<ForeignKeyMapping> list = new ArrayList<ForeignKeyMapping>();
		
		TableModel target = (TableModel)getTarget();
		ColumnModel[] targetColumns = target.getColumns();
		
		for(int i=0;i<targetColumns.length;i++){
			if(targetColumns[i].isPrimaryKey()){
				ForeignKeyMapping mapping = new ForeignKeyMapping();
				mapping.setTarget(targetColumns[i]);
				
				ColumnModel referColumn = (ColumnModel) references.get(targetColumns[i]);
				TableModel source = (TableModel) getSource();
				ColumnModel[] sourceColumns = source.getColumns();
				
				if(referColumn != null){
					for(int j=0;j<sourceColumns.length;j++){
						if(sourceColumns[j] == referColumn){
							mapping.setRefer(referColumn);
							break;
						}
					}
				} else {
					for(int j=0;j<sourceColumns.length;j++){
						if(sourceColumns[j].getColumnName().equals(targetColumns[i].getColumnName())){
							referColumn = sourceColumns[j];
							break;
						}
					}
					if(referColumn != null){
						mapping.setRefer(referColumn);
					}
				}
				list.add(mapping);
			}
		}
		
		return list.toArray(new ForeignKeyMapping[list.size()]);
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		List<IPropertyDescriptor> descriptoes = new ArrayList<IPropertyDescriptor>();
		
		descriptoes.add(new TextPropertyDescriptor(P_FOREIGN_KEY_NAME, 
				DBPlugin.getResourceString("property.foreignKeyName")));
//		descriptoes.add(new ForeignKeyMappingPropertyDescriptor(
//				P_FOREIGN_KEY_MAPPING, "References", ((TableModel)getSource()).getColumns()));
		
		return descriptoes.toArray(new IPropertyDescriptor[descriptoes.size()]);
	}

	public Object getPropertyValue(Object id) {
		if(id == P_FOREIGN_KEY_NAME){
			return getForeignKeyName();
		}
//		else if(id == P_FOREIGN_KEY_MAPPING){
//			return getMapping();
//		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if(id == P_FOREIGN_KEY_NAME
//				|| id == P_FOREIGN_KEY_MAPPING
				){
			return true;
		}
		return false;
	}

	public void setPropertyValue(Object id, Object value) {
		if(id == P_FOREIGN_KEY_NAME){
			setForeignKeyName((String)value);
		}
//		else if(id == P_FOREIGN_KEY_MAPPING){
//			setMapping((ForeignKeyMapping[])value);
//		}
	}
	
}
