package net.java.amateras.db.dialect;

public interface IColumnType {
	
	public String getName();
	
	public String getLogicalName();
	
	public boolean supportSize();
	
	public int getType();
}
