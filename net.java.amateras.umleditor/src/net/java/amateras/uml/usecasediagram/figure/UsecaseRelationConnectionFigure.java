/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure;

import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.PolylineConnection;

/**
 * @author Takahiro Shida.
 *
 */
public class UsecaseRelationConnectionFigure extends PolylineConnection
		implements PresentationFigure {

	public UsecaseRelationConnectionFigure() {
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		setForegroundColor(model.getForegroundColor());
	}

}
