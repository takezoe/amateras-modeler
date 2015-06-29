package net.java.amateras.uml.usecasediagram.edit;

import org.eclipse.draw2d.IFigure;

import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;
import net.java.amateras.uml.usecasediagram.figure.UsecaseExtendConnectionFigure;

/**
 * 
 * @author Takahiro Shida.
 *
 */
public class UsecaseExtendEditPart extends AbstractUMLConnectionEditPart {

	protected IFigure createFigure() {
		return new UsecaseExtendConnectionFigure();
	}
}
