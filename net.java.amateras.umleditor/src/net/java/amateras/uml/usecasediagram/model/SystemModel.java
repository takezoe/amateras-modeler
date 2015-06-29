/**
 * 
 */
package net.java.amateras.uml.usecasediagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author shida
 *
 */
public class SystemModel extends AbstractUMLEntityModel implements EntityModel, ICloneableModel {

	private String name;

	private static final Dimension MINIMUM_SIZE = new Dimension(200,200);

	public SystemModel() {
		super();
		setName("system");
	}

	public void setConstraint(Rectangle constraint) {
		Dimension size = constraint.getSize();
		if (MINIMUM_SIZE.contains(size)) {
			constraint.setSize(MINIMUM_SIZE);
		}
		super.setConstraint(constraint);
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange(P_ENTITY_NAME, old, name);
	}
	
	public String getName() {
		return name;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_ENTITY_NAME, UMLPlugin
						.getDefault().getResourceString("property.name")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))};
	}
	
	public Object getPropertyValue(Object id) {
		if (P_ENTITY_NAME.equals(id)) {
			return name;
		}
		return super.getPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value) {
		if (P_ENTITY_NAME.equals(id)) {
			setName((String) value);
		} 
		super.setPropertyValue(id, value);
	}
	
	public boolean isPropertySet(Object id) {
		return P_ENTITY_NAME.equals(id) || super.isPropertySet(id);
	}
	
	public Object clone(){
		SystemModel model = new SystemModel();
		model.setName(getName());
		model.setBackgroundColor(getBackgroundColor().getRGB());
		model.setForegroundColor(getForegroundColor().getRGB());
		model.setParent(getParent());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
