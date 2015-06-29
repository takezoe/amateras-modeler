package net.java.amateras.db.validator;

import java.util.HashSet;
import java.util.Set;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.dialect.IColumnType;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.jface.preference.IPreferenceStore;

/**
 * The ER-Diagram validator.
 * 
 * @author Naoki Takezoe
 * @since 1.0.5
 */
public class DiagramValidator {
	
	private RootModel model;
	
	private Set<String> tableNames = new HashSet<String>();
	private Set<String> logicalNames = new HashSet<String>();
	
	/**
	 * The constructor.
	 * 
	 * @param model the model for validation
	 */
	public DiagramValidator(RootModel model){
		this.model = model;
	}
	
	/**
	 * Executes validation.
	 * 
	 * @return validation errors
	 */
	public DiagramErrors doValidate(){
		DiagramErrors errors = new DiagramErrors();
		
		for(AbstractDBEntityModel entity: model.getChildren()){
			if(entity instanceof TableModel){
				TableModel table = (TableModel) entity;
				table.setError("");
				validateTable(errors, model, table);
			}
		}
		
		String dialectName = model.getDialectName();
		IDialect dialect = DialectProvider.getDialect(dialectName);
		dialect.validate(errors, model);
		
		return errors;
	}
	
	private void validateTable(DiagramErrors errors, RootModel root, TableModel table){
	    
	    IPreferenceStore store = DBPlugin.getDefault().getPreferenceStore();
	    
		// Validates TableModel
		String tableName = table.getTableName();
		if(tableName==null || tableName.length()==0){
		    errors.addError(store.getString(DBPlugin.PREF_VALIDATE_PHYSICAL_TABLE_NAME_REQUIRED), 
		            table, DBPlugin.getResourceString("validation.error.tableName.required"));
		    
		} else if(tableNames.contains(tableName)){
			errors.addError(store.getString(DBPlugin.PREF_VALIDATE_PHYSICAL_TABLE_NAME_DUPLICATED),
			        table, DBPlugin.getResourceString("validation.error.tableName.duplicated"));
		} else {
			tableNames.add(tableName);
		}
		
		String logicalName = table.getLogicalName();
		if(logicalName==null || logicalName.length()==0){
			errors.addError(store.getString(DBPlugin.PREF_VALIDATE_LOGICAL_TABLE_NAME_REQUIRED),
			        table, DBPlugin.getResourceString("validation.error.logicalTableName.required"));
			
		} else if(logicalNames.contains(logicalName)){
			errors.addError(store.getString(DBPlugin.PREF_VALIDATE_LOGICAL_TABLE_NAME_DUPLICATED),
			        table, DBPlugin.getResourceString("validation.error.logicalTableName.duplicated"));
		} else {
			logicalNames.add(logicalName);
		}
		
		// Validates ColumnModels
		ColumnModel[] columns = table.getColumns();
		if(columns.length == 0){
			errors.addError(store.getString(DBPlugin.PREF_VALIDATE_NO_COLUMNS), 
			        table, DBPlugin.getResourceString("validation.error.noColumns"));
		} else {
			Set<String> columnNames = new HashSet<String>();
			Set<String> logicalColumnNames = new HashSet<String>();
			boolean findPk = false;
			for(ColumnModel column: columns){
				if(column.isPrimaryKey()){
					findPk = true;
				}
				String columnName = column.getColumnName();
				if(columnName == null || columnName.length()==0){
				    errors.addError(store.getString(DBPlugin.PREF_VALIDATE_PHYSICAL_COLUMN_NAME_REQUIRED),
					        table, column, DBPlugin.getResourceString("validation.error.columnName.required"));
				    
				} else if(columnNames.contains(columnName)){
				    errors.addError(store.getString(DBPlugin.PREF_VALIDATE_PHYSICAL_COLUMN_NAME_DUPLICATED),
					        table, column, DBPlugin.getResourceString("validation.error.columnName.duplicated"));
				} else {
					columnNames.add(columnName);
				}
				
				String logicalColumnName = column.getLogicalName();
				if(logicalColumnName == null || logicalColumnName.length()==0){
					errors.addError(store.getString(DBPlugin.PREF_VALIDATE_LOGICAL_COLUMN_NAME_REQUIRED),
					        table, column, DBPlugin.getResourceString("validation.error.logicalColumnName.required"));
					        
				} else if(logicalColumnNames.contains(logicalColumnName)){
					errors.addError(store.getString(DBPlugin.PREF_VALIDATE_LOGICAL_COLUMN_NAME_DUPLICATED), 
					        table, column, DBPlugin.getResourceString("validation.error.logicalColumnName.duplicated"));
				} else {
					logicalColumnNames.add(logicalColumnName);
				}
			}
			
			if(!findPk){
                errors.addError(store.getString(DBPlugin.PREF_VALIDATE_PRIMARY_KEY),
                        table, DBPlugin.getResourceString("validation.error.noPrimaryKey"));
			}
		}
		
		// Validates Relations
		for(AbstractDBConnectionModel conn: table.getModelSourceConnections()){
			if(conn instanceof ForeignKeyModel){
				ForeignKeyModel fk = (ForeignKeyModel) conn;
				for(ForeignKeyMapping mapping: fk.getMapping()){
					ColumnModel referer = mapping.getRefer();
					ColumnModel target = mapping.getTarget();
					
					IColumnType refererType = referer.getColumnType();
					IColumnType targetType = target.getColumnType();
					
					if(!refererType.getName().equals(targetType.getName())){
						errors.addError(store.getString(DBPlugin.PREF_VALIDATE_FOREIGN_KEY_COLUMN_TYPE), 
								table, referer, DBPlugin.getResourceString("validation.error.foreignKey.columnType"));
						continue;
					}
					if(refererType.supportSize() && !referer.getSize().equals(target.getSize())){
						errors.addError(store.getString(DBPlugin.PREF_VALIDATE_FOREIGN_KEY_COLUMN_SIZE), 
								table, referer, DBPlugin.getResourceString("validation.error.foreignKey.columnSize"));
						continue;
					}
				}
				
			}
		}
	}
	
}
