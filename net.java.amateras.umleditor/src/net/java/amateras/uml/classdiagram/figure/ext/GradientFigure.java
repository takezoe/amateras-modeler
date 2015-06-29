/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure.ext;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.swt.graphics.Color;

/**
 * @author shidat
 *
 */
public class GradientFigure extends Figure {

	private final Color color;
	public GradientFigure(Color color) {
		this.color = color;
	}
	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.setForegroundColor(color);
		graphics.fillGradient(getBounds(), true);
	}
}
