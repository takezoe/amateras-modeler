package net.java.amateras.uml.classdiagram.editpart;

import net.java.amateras.uml.classdiagram.figure.GeneralizationConnectionFigure;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;

public class GeneralizationEditPart extends AbstractUMLConnectionEditPart {

	@Override
	protected IFigure createFigure() {
		return new GeneralizationConnectionFigure();
	}

}
