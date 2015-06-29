/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * @author Takahiro Shida.
 * 
 */
public class ReturnMessageConnectionFigure extends PolylineConnection implements EntityFigure, PresentationFigure {

	Label label = null;
	private ConnectionLocator locator;

	public ReturnMessageConnectionFigure() {
		label = new Label() {
			public void paint(Graphics graphics) {
				if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
					graphics.setAntialias(SWT.ON);
					graphics.setTextAntialias(SWT.ON);
				}
				super.paint(graphics);
			}
		};
		setLineStyle(SWT.LINE_DASH);
		setTargetDecoration(new PolygonDecoration());
		locator = new ConnectionLocator(this, ConnectionLocator.TARGET);
		locator.setGap(3);
		locator.setRelativePosition(PositionConstants.NORTH_WEST);
		add(label, locator);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.amateras.uml.figure.EntityFigure#getLabel()
	 */
	public Label getLabel() {
		return label;
	}

	public Rectangle getCellEditorRectangle() {
		Rectangle rect = label.getBounds().getCopy();
		if (rect.width > 100) {
			return rect;
		} else {
			return new Rectangle(rect.x, rect.y, 100, rect.height);
		}
	}

	public void updatePresentation(AbstractUMLModel model) {
		setForegroundColor(model.getForegroundColor());
		label.setForegroundColor(model.getForegroundColor());
	}

	public void locateLabel(boolean b) {
		if (b) {
			locator.setRelativePosition(PositionConstants.NORTH_WEST);
		} else {
			locator.setRelativePosition(PositionConstants.NORTH_EAST);
		}
	}
	
	public void paint(Graphics graphics) {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
			graphics.setAntialias(SWT.ON);
			graphics.setTextAntialias(SWT.ON);
		}
		super.paint(graphics);
	}
}
