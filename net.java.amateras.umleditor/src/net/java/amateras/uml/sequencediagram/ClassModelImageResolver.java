/**
 * 
 */
package net.java.amateras.uml.sequencediagram;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

/**
 * @author Takahiro Shida.
 *
 */
public class ClassModelImageResolver {

	public static Image getAttributeImage(AttributeModel attr) {
		if (attr.getVisibility().equals(Visibility.PUBLIC)) {
			return UMLPlugin.getImageDescriptor(
					"icons/field_public.gif").createImage();
		} else if (attr.getVisibility().equals(Visibility.PRIVATE)) {
			return UMLPlugin.getImageDescriptor(
					"icons/field_private.gif").createImage();
		} else if (attr.getVisibility().equals(Visibility.PROTECTED)) {
			return UMLPlugin.getImageDescriptor(
					"icons/field_protected.gif").createImage();
		} else if (attr.getVisibility().equals(Visibility.PACKAGE)) {
			return UMLPlugin.getImageDescriptor(
					"icons/field_default.gif").createImage();
		}
		return ImageDescriptor.getMissingImageDescriptor().createImage();
	}
	
	public static Image getOperationImage(OperationModel ope) {
		if (ope.getVisibility().equals(Visibility.PUBLIC)) {
			return UMLPlugin.getImageDescriptor(
					"icons/method_public.gif").createImage();
		} else if (ope.getVisibility().equals(Visibility.PRIVATE)) {
			return UMLPlugin.getImageDescriptor(
					"icons/method_private.gif").createImage();
		} else if (ope.getVisibility().equals(Visibility.PROTECTED)) {
			return UMLPlugin.getImageDescriptor(
					"icons/method_protected.gif").createImage();
		} else if (ope.getVisibility().equals(Visibility.PACKAGE)) {
			return UMLPlugin.getImageDescriptor(
					"icons/method_default.gif").createImage();
		}
		return ImageDescriptor.getMissingImageDescriptor().createImage();
	}
}
