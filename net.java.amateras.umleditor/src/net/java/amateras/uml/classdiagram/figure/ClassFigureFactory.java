/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.ext.ClassFigure;
import net.java.amateras.uml.classdiagram.figure.ext.EnumFigure;
import net.java.amateras.uml.classdiagram.figure.ext.InterfaceFigure;

import org.eclipse.draw2d.Figure;

/**
 * The factory to create figures in the class diagram.
 * 
 * @author shidat
 */
public class ClassFigureFactory {

	public static UMLClassFigure getClassFigure() { 
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new ClassFigure();
		}
		return new UMLClassFigure(UMLPlugin.getImageDescriptor("icons/class.gif").createImage(), new Figure());
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
