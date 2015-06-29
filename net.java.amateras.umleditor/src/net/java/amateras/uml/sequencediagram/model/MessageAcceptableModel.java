/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * @author Takahiro Shida.
 * 
 */
public abstract class MessageAcceptableModel extends AbstractUMLEntityModel {

	public abstract void updateLocation(Rectangle delta);

	public abstract void computeCaller(int size);

	public abstract int getCalleeSize();

	public abstract ActivationModel getOwner();

	// public abstract int computeChild();

	public abstract void computeCaller();

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] { new ColorPropertyDescriptor(
				P_BACKGROUND_COLOR, UMLPlugin.getDefault().getResourceString(
						"property.background")) };
	}
}
