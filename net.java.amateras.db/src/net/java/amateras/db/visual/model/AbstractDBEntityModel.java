package net.java.amateras.db.visual.model;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class AbstractDBEntityModel extends AbstractDBModel implements IPropertySource {
	
	private Rectangle constraint;
	private List<AbstractDBConnectionModel> sourceConnections = new ArrayList<AbstractDBConnectionModel>();
	private List<AbstractDBConnectionModel> targetConnections = new ArrayList<AbstractDBConnectionModel>();
	
	public static final String P_CONSTRAINT = "p_constraint";
	public static final String P_SOURCE_CONNECTION = "p_source_connection";
	public static final String P_TARGET_CONNECTION = "p_target_connection";
	
	public Rectangle getConstraint() {
		return constraint;
	}
	
	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
		firePropertyChange(P_CONSTRAINT, null, constraint);
	}
	
	public boolean canSource(AbstractDBConnectionModel conn){
		return true;
	}
	
	public boolean canTarget(AbstractDBConnectionModel conn){
		return true;
	}
	
	public Object getEditableValue() {
		return this;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[0];
	}
	
	public Object getPropertyValue(Object id) {
		return null;
	}
	
	public boolean isPropertySet(Object id) {
		return false;
	}
	
	public void resetPropertyValue(Object id) {
	}
	
	public void setPropertyValue(Object id, Object value) {
	}
	
	public void addSourceConnection(AbstractDBConnectionModel connx) {
		sourceConnections.add(connx);
		firePropertyChange(P_SOURCE_CONNECTION, null, connx);
	}
	
	public void addTargetConnection(AbstractDBConnectionModel connx) {
		targetConnections.add(connx);
		firePropertyChange(P_TARGET_CONNECTION, null, connx);
	}
	
	public List<AbstractDBConnectionModel> getModelSourceConnections() {
		return sourceConnections;
	}
	
	public List<AbstractDBConnectionModel> getModelTargetConnections() {
		return targetConnections;
	}
	
	public void removeSourceConnection(Object connx) {
		sourceConnections.remove(connx);
		firePropertyChange(P_SOURCE_CONNECTION, connx, null);
	}
	
	public void removeTargetConnection(Object connx) {
		targetConnections.remove(connx);
		firePropertyChange(P_TARGET_CONNECTION, connx, null);
	}
	
//	public boolean equals(Object obj){
//		throw new RuntimeException("equals is not implemented!");
//	}
}
