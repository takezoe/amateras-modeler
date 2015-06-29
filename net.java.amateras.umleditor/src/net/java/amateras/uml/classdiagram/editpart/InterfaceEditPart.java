package net.java.amateras.uml.classdiagram.editpart;

import net.java.amateras.uml.classdiagram.figure.ClassFigureFactory;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;

public class InterfaceEditPart extends CommonEntityEditPart {

	public UMLClassFigure getClassFigure() {
		return ClassFigureFactory.getInterfaceFigure();
	}
}
