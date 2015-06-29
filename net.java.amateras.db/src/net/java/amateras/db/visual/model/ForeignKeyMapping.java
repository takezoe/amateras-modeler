package net.java.amateras.db.visual.model;

import net.java.amateras.db.DBPlugin;

public class ForeignKeyMapping {
	
	private ColumnModel target;
	private ColumnModel refer;
	
	public ColumnModel getRefer() {
		return refer;
	}
	
	public void setRefer(ColumnModel refer) {
		this.refer = refer;
	}
	
	public ColumnModel getTarget() {
		return target;
	}
	
	public void setTarget(ColumnModel target) {
		this.target = target;
	}
	
	public String getDisplayString(boolean logicalMode){
		StringBuffer sb = new StringBuffer();
		if(logicalMode){
			if(getRefer()==null){
				sb.append(DBPlugin.getResourceString("label.undef"));
			} else {
				sb.append(getRefer().getLogicalName());
			}
			sb.append("=");
			sb.append(getTarget().getLogicalName());
		} else {
			if(getRefer()==null){
				sb.append(DBPlugin.getResourceString("label.undef"));
			} else {
				sb.append(getRefer().getColumnName());
			}
			sb.append("=");
			sb.append(getTarget().getColumnName());
		}
		return sb.toString();
	}
}
