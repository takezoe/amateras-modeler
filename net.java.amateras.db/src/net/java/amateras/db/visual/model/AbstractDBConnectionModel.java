package net.java.amateras.db.visual.model;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

public class AbstractDBConnectionModel extends AbstractDBModel implements IPropertySource {
	
	private AbstractDBEntityModel source;
	private AbstractDBEntityModel target;

	public void attachSource() {
		if (!source.getModelSourceConnections().contains(this)){
			source.addSourceConnection(this);
		}
	}
	
	public void attachTarget() {
		if (!target.getModelTargetConnections().contains(this)){
			target.addTargetConnection(this);
		}
	}
	
	public void detachSource() {
		if(source!=null){
			source.removeSourceConnection(this);
		}
	}
	
	public void detachTarget() {
		if(target!=null){
			target.removeTargetConnection(this);
		}
	}
	
	public AbstractDBEntityModel getSource() {
		return source;
	}
	
	public AbstractDBEntityModel getTarget() {
		return target;
	}
	
	public void setSource(AbstractDBEntityModel model) {
		source = model;
	}
	
	public void setTarget(AbstractDBEntityModel model) {
		target = model;
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

}
