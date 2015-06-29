package net.java.amateras.db.htmlgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ForeignKeyModel extends AbstractDBConnectionModel {

	private String foreignKeyName = "";
	private Map<ColumnModel, ColumnModel> references = new HashMap<ColumnModel, ColumnModel>();
	public static final String P_FOREIGN_KEY_NAME = "p_foreign_key_name";
	public static final String P_FOREIGN_KEY_MAPPING = "p_foreign_key_mapping";

	public void setForeignKeyName(String foreignKeyName){
		this.foreignKeyName = foreignKeyName;
	}

	public String getForeignKeyName(){
		return this.foreignKeyName;
	}

	public void setMapping(ForeignKeyMapping[] mapping){
		references.clear();
		for(int i=0;i<mapping.length;i++){
			references.put(mapping[i].getTarget(), mapping[i].getRefer());
		}
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

}
