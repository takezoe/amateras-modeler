package net.java.amateras.db.dialect;

import java.io.Serializable;

public class ColumnType implements IColumnType, Serializable {
	
	private String name;
	private String logicalName;
	private boolean supportSize;
	private int type;
	
	public ColumnType(String name, String logicalName, boolean supportSize,int type){
		this.name = name;
		this.logicalName = logicalName;
		this.supportSize = supportSize;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getLogicalName(){
		return logicalName;
	}
	
	public boolean supportSize() {
		return supportSize;
	}
	
	public int getType(){
		return type;
	}
	
	public String toString(){
		//return getLogicalName() + " - " + getName();
		return getName() + " - " + getLogicalName();
	}
	
}
