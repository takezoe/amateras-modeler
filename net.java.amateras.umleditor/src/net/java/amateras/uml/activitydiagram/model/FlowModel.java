package net.java.amateras.uml.activitydiagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.StereoTypeModel;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * 
 * @author Naoki Takezoe
 */
public class FlowModel extends AbstractUMLConnectionModel implements StereoTypeModel {
	
	private String condition = "";
	
	public static final String P_CONDITION = "_condition";
	
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
		firePropertyChange(P_CONDITION, null, condition);
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		IPropertyDescriptor[] descs = super.getPropertyDescriptors();
		IPropertyDescriptor[] newDescs = new IPropertyDescriptor[descs.length + 1];
		System.arraycopy(descs, 0, newDescs, 0, descs.length);
		
		newDescs[descs.length] = 
				new TextPropertyDescriptor(P_CONDITION, UMLPlugin
						.getDefault().getResourceString("property.condition"));
		
		return newDescs;
	}
	
	public Object getPropertyValue(Object id) {
		if (P_CONDITION.equals(id)) {
			return getCondition();
		}
		return super.getPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value) {
		if (P_CONDITION.equals(id)) {
			setCondition((String) value);
		}
		super.setPropertyValue(id, value);
	}
	
	public boolean isPropertySet(Object id) {
		return P_CONDITION.equals(id) || super.isPropertySet(id);
	}
	
	public String getStereoType() {
		return getCondition();
	}

	public void setStereoType(String stereoType) {
		setCondition(stereoType);
	}

}
