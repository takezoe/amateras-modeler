package net.java.amateras.uml.activitydiagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * 
 * @author Naoki Takezoe
 */
public class ActionModel extends AbstractUMLEntityModel implements EntityModel, ICloneableModel {
	
	public static final String P_ACTION_NAME = "_actionName";
	
	private String actionName = "Action";

	public String getActionName() {
		return actionName;
	}

	public void setActionName(String actionName) {
		this.actionName = actionName;
		firePropertyChange(P_ACTION_NAME, null, actionName);
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_ACTION_NAME, UMLPlugin
						.getDefault().getResourceString("property.name")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))};
	}
	
	public Object getPropertyValue(Object id) {
		if (P_ACTION_NAME.equals(id)) {
			return getActionName();
		}
		return super.getPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value) {
		if (P_ACTION_NAME.equals(id)) {
			setActionName((String) value);
		}
		super.setPropertyValue(id, value);
	}
	
	public boolean isPropertySet(Object id) {
		return P_ACTION_NAME.equals(id) || super.isPropertySet(id);
	}

	public String getName() {
		return getActionName();
	}

	public void setName(String name) {
		setActionName(name);
	}
	
	public Object clone(){
		ActionModel model = new ActionModel();
		model.setName(getName());
		model.setParent(getParent());
		model.setBackgroundColor(getBackgroundColor().getRGB());
		model.setForegroundColor(getForegroundColor().getRGB());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
