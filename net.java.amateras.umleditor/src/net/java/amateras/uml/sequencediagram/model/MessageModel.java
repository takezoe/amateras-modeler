/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import java.util.Collections;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.sequencediagram.property.MessagePropertyDescriptor;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Takahiro Shida.
 * 
 */
public class MessageModel extends AbstractUMLConnectionModel implements EntityModel {

	public static final String P_NAME = "_instance_name";

	public static final String P_DIRECTION = "_direction";

	private String name;

	private boolean direction;

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(P_NAME, oldName, name);
	}

	public String getName() {
		return this.name;
	}

	private EntityModel getType() {
		AbstractUMLEntityModel model = getTarget();
		if (model instanceof ActivationModel) {
			ActivationModel activationModel = (ActivationModel) model;
			InstanceModel owner = activationModel.getOwnerLine().getOwner();
			return owner.getType();
		}
		return null;
	}

	public List<AbstractUMLModel> getProporsal() {
		EntityModel model = getType();
		if (model instanceof AbstractUMLEntityModel) {
			AbstractUMLEntityModel entityModel = (AbstractUMLEntityModel) model;
			return entityModel.getChildren();
		}
		return Collections.emptyList();
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		MessagePropertyDescriptor descriptor = new MessagePropertyDescriptor(P_NAME,
				UMLPlugin.getDefault().getResourceString("property.name"));
		descriptor.setUMLModels(getProporsal());
		return new IPropertyDescriptor[] { descriptor, new ColorPropertyDescriptor(P_FOREGROUND_COLOR,
				UMLPlugin.getDefault().getResourceString("property.foreground")) };
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(P_NAME)) {
			return getName();
		}
		return super.getPropertyValue(id);
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(P_NAME)) {
			return true;
		}
		return super.isPropertySet(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(P_NAME)) {
			setName((String) value);
		}
		super.setPropertyValue(id, value);
	}

	public void attachTarget() {
		super.attachTarget();
	}

	public void calcDirection() {
		if (getSource() != null && getTarget() != null)
			setDirection(getSource().getConstraint().x < getTarget().getConstraint().x);
	}

	public boolean isDirection() {
		return direction;
	}

	public void setDirection(boolean direction) {
		this.direction = direction;
		firePropertyChange(P_DIRECTION, null, direction);
	}
}
