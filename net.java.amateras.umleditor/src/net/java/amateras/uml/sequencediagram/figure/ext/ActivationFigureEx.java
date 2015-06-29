/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure.ext;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.swt.graphics.Color;

import net.java.amateras.uml.sequencediagram.figure.ActivationFigure;

/**
 * @author shidat
 *
 */
public class ActivationFigureEx extends ActivationFigure {

	private static final Color COLOR = new Color(null, 180,218,173);	
	private static final Color BORDER = new Color(null, 31,124,0);

	public ActivationFigureEx() {
		setBorder(new LineBorder(BORDER));
	}
	public void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.setForegroundColor(COLOR);
		graphics.fillGradient(getBounds(), true);
	}
}
