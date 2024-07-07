/**
 * 
 */
package net.java.amateras.uml.activitydiagram.figure.ext;

import net.java.amateras.uml.activitydiagram.figure.ActionFigure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

/**
 * @author shidat
 *
 */
public class ActionFigureEx extends ActionFigure {

	private static final Color COLOR = new Color(null, 210,248,203);	
	private static final Color BORDER = new Color(null, 31,124,0);
	
	public ActionFigureEx() {
		super();
	}

	public void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(ColorConstants.lightGray);
		Rectangle copy = getBounds().getCopy();
		Dimension corner = getCornerDimensions();
		copy = copy.shrink(3, 3);
		copy = copy.translate(3, 3);
		graphics.fillRoundRectangle(copy, corner.width, corner.height);
		copy = getBounds().getCopy();
		copy.shrink(3, 3);
		graphics.setBackgroundColor(COLOR);
		graphics.fillRoundRectangle(copy, corner.width, corner.height);
		graphics.setForegroundColor(BORDER);
		graphics.drawRoundRectangle(copy, corner.width, corner.height);
	}
}
