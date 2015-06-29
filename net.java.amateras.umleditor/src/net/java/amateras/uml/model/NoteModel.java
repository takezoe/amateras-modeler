/**
 * 
 */
package net.java.amateras.uml.model;

import net.java.amateras.uml.UMLPlugin;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author Takahiro Shida.
 *
 */
public class NoteModel extends AbstractUMLEntityModel {

	private String content = "";
	
	private String old;
	
	private static final int DEFAULT_WIDTH = 100;
	
	private static final int DEFAULT_HEIGHT = 30;
	
	public static final String P_CONTENT = "_instance_name";
	
	public void setConstraint(Rectangle constraint) {
		if (constraint.width < DEFAULT_WIDTH) {
			constraint.width = DEFAULT_WIDTH;
		}
		if (constraint.height < DEFAULT_HEIGHT) {
			constraint.height = DEFAULT_HEIGHT;
		}
		super.setConstraint(constraint);
	}
	
	public void setContent(String content) {
		old = this.content;
		this.content = content;
		firePropertyChange(P_CONTENT, old, content);
	}
	
	public String getContent() {
		return content;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[]{
				new TextPropertyDescriptor(P_CONTENT, 
						UMLPlugin.getDefault().getResourceString("property.name")),
		};
	}

	public Object getPropertyValue(Object id) {
		if(id.equals(P_CONTENT)){
			return getContent();
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if(id.equals(P_CONTENT)){
			return true;
		}
		return false;
	}

	public void setPropertyValue(Object id, Object value) {
		if(id.equals(P_CONTENT)){
			setContent((String)value);
		}
	}
}
