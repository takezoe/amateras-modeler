package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

/**
 * 
 * @author Naoki Takezoe
 */
public class ForkNodeEditPart extends AbstractUMLEntityEditPart {

	protected IFigure createFigure() {
		RectangleFigure figure = new RectangleFigure();
		figure.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		figure.setOpaque(true);
		figure.setSize(100, 5);
		return figure;
	}

}
