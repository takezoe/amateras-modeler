/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;

/**
 * @author shida
 *
 */
public class ActivationFigure extends RectangleFigure implements
		PresentationFigure {

	public ActivationFigure() {
		setOpaque(true);
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		setForegroundColor(model.getForegroundColor());
		setBackgroundColor(model.getBackgroundColor());
	}

	public void paint(Graphics graphics) {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
			graphics.setAntialias(SWT.ON);
			graphics.setTextAntialias(SWT.ON);
		}
		super.paint(graphics);
	}
}
