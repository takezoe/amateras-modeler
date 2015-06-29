/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure.ext;

import net.java.amateras.uml.sequencediagram.figure.InstanceFigure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * @author shidat
 *
 */
public class InstanceFigureEx extends InstanceFigure {

	private static final Color COLOR = new Color(null, 180,218,173);	
	private static final Color BORDER = new Color(null, 31,124,0);

	public InstanceFigureEx() {
		setBorder(new MarginBorder(1));
	}
	public void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(ColorConstants.lightGray);
		Rectangle copy = getBounds().getCopy();
		copy = copy.shrink(3, 3);
		copy = copy.translate(3, 3);
		graphics.fillRectangle(copy);
		copy = getBounds().getCopy();
		copy.shrink(3, 3);
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.setForegroundColor(COLOR);
		graphics.fillGradient(copy, true);
		graphics.setForegroundColor(BORDER);
		graphics.drawRectangle(copy);
	}
}
