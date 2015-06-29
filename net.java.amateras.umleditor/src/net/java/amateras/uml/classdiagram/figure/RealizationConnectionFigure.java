/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * @author Takahiro Shida.
 *
 */
public class RealizationConnectionFigure extends PolylineConnection implements
		PresentationFigure {

	public RealizationConnectionFigure() {
		PolygonDecoration decoration = new PolygonDecoration();
		decoration.setScale(10, 7);
		decoration.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		setTargetDecoration(decoration);
		setLineStyle(Graphics.LINE_DASH);
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		setForegroundColor(model.getForegroundColor());
	}

}
