/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure;

import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;

/**
 * @author Takahiro Shida.
 *
 */
public class UsecaseIncludeConnectionFigure extends PolylineConnection implements
		PresentationFigure {

	public UsecaseIncludeConnectionFigure() {
		Label label = new Label();
		label.setText("<<include>>");
		setLineStyle(Graphics.LINE_DASH);
		setTargetDecoration(new PolylineDecoration());
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
	}
	
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		setForegroundColor(model.getForegroundColor());
	}

}
