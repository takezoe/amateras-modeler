package net.java.amateras.db.visual.model;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.dialect.IIndexType;

/**
 * 
 * @author Naoki Takezoe
 * @since 1.0.3
 */
public class IndexModel {
	
	private String indexName;
	private IIndexType indexType;
	private List<String> columns = new ArrayList<String>();
	
	public String getIndexName() {
		return indexName;
	}
	
	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}
	
	/**
	 * Returns the index type.
	 * 
	 * @return "INDEX" or "UNIQUE"
	 */
	public IIndexType getIndexType() {
		return indexType;
	}
	
	/**
	 * Sets the index type.
	 * 
	 * @param indexType "INDEX" or "UNIQUE"
	 */
	public void setIndexType(IIndexType indexType) {
		this.indexType = indexType;
	}
	
	public List<String> getColumns() {
		return columns;
	}
	
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String column: columns){
			if(sb.length() != 0){
				sb.append(", ");
			}
			sb.append(column);
		}
		return indexName + " - " + indexType.getName() + " (" + sb.toString() + ")";
	}
	
}
