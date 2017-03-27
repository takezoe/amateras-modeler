/**
 * 
 */
package net.java.amateras.uml.usecasediagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.ICloneableModel;
import net.java.amateras.uml.usecasediagram.property.ResourcePropertyDescriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author shida
 *
 */
public class UsecaseModel extends AbstractUMLEntityModel implements EntityModel, ICloneableModel {

	public static final String P_RESOURCE = "_resource";
	
	private static final Dimension MINIMUM_SIZE = new Dimension(100,40);
	
	private String name;
	
	private String resource;
	
	public UsecaseModel() {
		super();
		setName("usecase");
	}
	
	@Override
	public void setConstraint(Rectangle constraint) {
		Dimension size = constraint.getSize();
		if (MINIMUM_SIZE.contains(size)) {
			constraint.setSize(MINIMUM_SIZE);
		}
		super.setConstraint(constraint);
	}
	
	@Override
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange(P_ENTITY_NAME, old, name);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public String getResource() {
		return resource;
	}
	
	public void setResource(String file) {
		String old = this.resource;
		this.resource = file;
		firePropertyChange(P_RESOURCE, old, file);
	}
	
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_ENTITY_NAME, UMLPlugin
						.getDefault().getResourceString("property.name")),
				new ResourcePropertyDescriptor(P_RESOURCE, UMLPlugin
						.getDefault().getResourceString("property.resource")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))};
	}
	
	@Override
	public Object getPropertyValue(Object id) {
		if (P_ENTITY_NAME.equals(id)) {
			return name;
		} else if (P_RESOURCE.equals(id)) {
			return resource;
		}
		return super.getPropertyValue(id);
	}
	
	@Override
	public void setPropertyValue(Object id, Object value) {
		if (P_ENTITY_NAME.equals(id)) {
			setName((String) value);
		} else if (P_RESOURCE.equals(id)) {
			setResource((String) value);
		}
		super.setPropertyValue(id, value);
	}
	
	@Override
	public boolean isPropertySet(Object id) {
		return P_ENTITY_NAME.equals(id) || P_RESOURCE.equals(id) || super.isPropertySet(id);
	}
	
	public IFile getFileResource() {
		return ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromPortableString(resource));
	}
	
	@Override
	public Object clone(){
		UsecaseModel model = new UsecaseModel();
		model.setName(getName());
		model.setResource(getResource());
		model.setForegroundColor(getForegroundColor().getRGB());
		model.setBackgroundColor(getBackgroundColor().getRGB());
		model.setParent(getParent());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
