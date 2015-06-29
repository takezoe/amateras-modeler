/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure.ext;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import net.java.amateras.uml.usecasediagram.figure.UsecaseFigure;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

/**
 * @author shidat
 *
 */
public class UsecaseFigureEx extends UsecaseFigure {

	private static final Color COLOR = new Color(null, 210,248,203);	
	private static final Color BORDER = new Color(null, 31,124,0);
	
	public UsecaseFigureEx(UsecaseModel model) {
		super(model);
	}

	public void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(ColorConstants.lightGray);
		Rectangle copy = getBounds().getCopy();
		copy = copy.shrink(3, 3);
		copy = copy.translate(3, 3);
		graphics.fillOval(copy);
		copy = getBounds().getCopy();
		copy.shrink(3, 3);
		graphics.setBackgroundColor(COLOR);
		graphics.fillOval(copy);
		graphics.setForegroundColor(BORDER);
		graphics.drawOval(copy);
	}
}
