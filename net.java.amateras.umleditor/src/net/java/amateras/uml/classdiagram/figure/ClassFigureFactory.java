/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.ext.ClassFigure;
import net.java.amateras.uml.classdiagram.figure.ext.EnumFigure;
import net.java.amateras.uml.classdiagram.figure.ext.InterfaceFigure;

import org.eclipse.draw2d.Figure;
import org.eclipse.swt.graphics.Image;

import static net.java.amateras.uml.editpart.AbstractUMLEditPart.addDecoratorTopRight;

/**
 * The factory to create figures in the class diagram.
 * 
 * @author shidat
 */
public class ClassFigureFactory {

	public static UMLClassFigure getClassFigure(boolean isAbstract) { 
		Image iconImage = UMLPlugin.getImageDescriptor("icons/class.gif").createImage();
		if (isAbstract) {
			Image iconDecorator = UMLPlugin.getImageDescriptor("icons/abstract_co.gif").createImage();
			iconImage = addDecoratorTopRight(iconImage, iconDecorator);
		}
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new ClassFigure(iconImage);
		}
		return new UMLClassFigure(iconImage, new Figure());
	}
	
	public static UMLClassFigure getInterfaceFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new InterfaceFigure();
		}
		return new UMLClassFigure(UMLPlugin.getImageDescriptor("icons/interface.gif").createImage(), new Figure());
	}
	
	public static UMLClassFigure getEnumFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new EnumFigure();
		}
		return new UMLClassFigure(UMLPlugin.getImageDescriptor("icons/enum.gif").createImage(), new Figure());
	}
}
