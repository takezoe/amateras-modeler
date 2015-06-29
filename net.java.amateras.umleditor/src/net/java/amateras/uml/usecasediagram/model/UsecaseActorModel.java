/**
 * 
 */
package net.java.amateras.uml.usecasediagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.ICloneableModel;
import net.java.amateras.uml.usecasediagram.property.ActorImagePropertyDescriptor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Takahiro Shida.
 *
 */
public class UsecaseActorModel extends AbstractUMLEntityModel implements EntityModel, ICloneableModel {

	public static final String P_IMAGE = "_image";
	
	private String name;
	
	private String imagePath;
	
	public UsecaseActorModel() {
		super();
		setName("actor");
	}
	
	public void setName(String name) {
		String old = this.name;
		this.name = name;
		firePropertyChange(P_ENTITY_NAME, old, name);
	}
	
	public String getName() {
		return name;
	}
	
	public void setImagePath(String image) {
		String old = this.imagePath;
		this.imagePath = image;
		firePropertyChange(P_IMAGE, old, image);
	}
	
	public IFile getImageFile() {
		if ("".equals(imagePath) || imagePath == null) {
			return null;
		}
		return ResourcesPlugin.getWorkspace().getRoot().getFile(Path.fromPortableString(imagePath));			
	}
	
	public String getImagePath() {
		return imagePath;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_ENTITY_NAME, UMLPlugin
						.getDefault().getResourceString("property.name")),
				new ActorImagePropertyDescriptor(P_IMAGE, UMLPlugin
						.getDefault().getResourceString("property.image")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))};
	}
	
	public Object getPropertyValue(Object id) {
		if (P_ENTITY_NAME.equals(id)) {
			return name;
		} else if (P_IMAGE.equals(id)) {
			return imagePath;
		}
		return super.getPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value) {
		if (P_ENTITY_NAME.equals(id)) {
			setName((String) value);
		} else if (P_IMAGE.equals(id)) {
			setImagePath((String) value);
		}
		super.setPropertyValue(id, value);
	}
	
	public boolean isPropertySet(Object id) {
		return P_ENTITY_NAME.equals(id) || P_IMAGE.equals(id) || super.isPropertySet(id);
	}
	
	public Object clone(){
		UsecaseActorModel model = new UsecaseActorModel();
		model.setName(getName());
		model.setImagePath(getImagePath());
		model.setForegroundColor(getForegroundColor().getRGB());
		model.setParent(getParent());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
