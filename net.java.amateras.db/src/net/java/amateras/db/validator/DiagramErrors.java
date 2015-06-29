package net.java.amateras.db.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * This object contains ER-Diagram validation errors.
 * 
 * @author Naoki Takezoe
 * @since 1.0.5
 */
public class DiagramErrors {
	
	private List<DiagramError> errors = new ArrayList<DiagramError>();
	
	public static final String ERROR_PREFIX = "[ERROR]";
    public static final String WARNING_PREFIX = "[WARN]";
	
	private static String createColumnMessage(TableModel table, ColumnModel column, String message){
		return "[" + table.getTableName() + "." + column.getColumnName()+ "]" + message;
	}
	
	private static String createColumnMessage(ColumnModel column, String message){
		return "[" + column.getColumnName()+ "]" + message;
	}
	
    private static String createIndexMessage(TableModel table, IndexModel index, String message){
        return "[" + table.getTableName() + "." + index.getIndexName()+ "]" + message;
    }
    
    private static String createIndexMessage(IndexModel index, String message){
        return "[" + index.getIndexName()+ "]" + message;
    }
    
	private static String createTableMessage(TableModel table, String message){
		return "[" + table.getTableName() + "]" + message;
	}
	
	/**
	 * Add an error messagefor TableModel.
	 * 
	 * @param level the error level
	 * @param table the table model
	 * @param message the error message
	 */
	public void addError(String level, TableModel table, String message){
	    if(level.equals(DBPlugin.LEVEL_ERROR)){
    		this.errors.add(new DiagramError(table, createTableMessage(table, message), level));
    		addErrorMessageToModel(table, ERROR_PREFIX + message);
    		
	    } else if(level.equals(DBPlugin.LEVEL_WARNING)){
            this.errors.add(new DiagramError(table, createTableMessage(table, message), level));
            addErrorMessageToModel(table, WARNING_PREFIX + message);
	    }
	}
	
	/**
	 * Add an error message for ColumnModel.
	 * 
	 * @param level the error level
	 * @param table the table model
	 * @param column the column model
	 * @param message the error message
	 */
	public void addError(String level, TableModel table, ColumnModel column, String message){
        if(level.equals(DBPlugin.LEVEL_ERROR)){
    		this.errors.add(new DiagramError(table, createColumnMessage(table, column, message), level));
    		addErrorMessageToModel(table, ERROR_PREFIX + createColumnMessage(column, message));
    		
        } else if(level.equals(DBPlugin.LEVEL_WARNING)){
            this.errors.add(new DiagramError(table, createColumnMessage(table, column, message), level));
            addErrorMessageToModel(table, WARNING_PREFIX + createColumnMessage(column, message));
        } 
	}
	
    /**
     * Add an error message for IndexModel.
     * 
     * @param level the error level
     * @param table the table model
     * @param index the index model
     * @param message the error message
     */
    public void addError(String level, TableModel table, IndexModel index, String message){
        if(level.equals(DBPlugin.LEVEL_ERROR)){
            this.errors.add(new DiagramError(table, createIndexMessage(table, index, message), level));
            addErrorMessageToModel(table, ERROR_PREFIX + createIndexMessage(index, message));
            
        } else if(level.equals(DBPlugin.LEVEL_WARNING)){
            this.errors.add(new DiagramError(table, createIndexMessage(table, index, message), level));
            addErrorMessageToModel(table, WARNING_PREFIX + createIndexMessage(index, message));
        } 
    }
	
	private void addErrorMessageToModel(TableModel table, String message){
		String error = table.getError();
		if(error.length() > 0){
			error = error + "\n";
		}
		error = error + message;
		table.setError(error);
	}
	
	/**
	 * Returns all errors.
	 * 
	 * @return all errors
	 */
	public List<DiagramError> getErrors(){
		return this.errors;
	}
	
	public static class DiagramError {
		
		private Object target;
		private String message;
		private String level;
		
		private DiagramError(Object target, String message, String level){
			this.target = target;
			this.message = message;
			this.level = level;
		}

		/**
		 * Returns the error model
		 * @return the error model
		 */
		public Object getTarget() {
			return target;
		}

		/**
		 * Returns the error message
		 * @return the error message
		 */
		public String getMessage() {
			return message;
		}
		
		/**
		 * Returns the error level
		 * @return the error level
		 */
		public String getLevel(){
			return this.level;
		}
		
		/**
		 * Add marker to the given file.
		 * @param file the ER-Diagram file
		 */
		public void addMarker(IFile file){
			if(level==DBPlugin.LEVEL_ERROR){
				addMarker(file, IMarker.SEVERITY_ERROR, message);
			} else {
				addMarker(file, IMarker.SEVERITY_WARNING, message);
			}
		}
		
		/**
		 * Adds marker to the specified line.
		 * 
		 * @param resource the target resource
		 * @param type the error type that defined by IMaker
		 * @param message the error message
		 */
		private static void addMarker(IResource resource, int type, String message){
			try {
				IMarker marker = resource.createMarker(IMarker.PROBLEM);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(IMarker.SEVERITY, new Integer(type));
				map.put(IMarker.MESSAGE, message);
//				map.put(IMarker.LINE_NUMBER,new Integer(line));
				marker.setAttributes(map);
			} catch(CoreException ex){
				DBPlugin.logException(ex);
			}
		}
	}
	
//	public static enum ErrorLevel {
//		WARN, 
//		ERROR
//	}
	
}
