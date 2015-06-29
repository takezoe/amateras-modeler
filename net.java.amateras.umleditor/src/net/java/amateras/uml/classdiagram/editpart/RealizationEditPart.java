package net.java.amateras.uml.classdiagram.editpart;

import net.java.amateras.uml.classdiagram.figure.RealizationConnectionFigure;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;

public class RealizationEditPart extends AbstractUMLConnectionEditPart {

	protected IFigure createFigure() {
		return new RealizationConnectionFigure();
	}
	
}
