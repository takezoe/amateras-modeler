/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure.ext;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;

/**
 * @author shidat
 *
 */
public class InterfaceFigure extends UMLClassFigure {

	private static final Image ICON = UMLPlugin.getImageDescriptor("icons/interface.gif").createImage();

	private static final Color COLOR = new Color(null, 192,193,239);
	
	private static final Color BORDER = new Color(null, 31,0,124);
	
	public InterfaceFigure() {
		super(ICON, new GradientFigure(COLOR));
		CompoundBorder border = new CompoundBorder(new MarginBorder(3), new LineBorder(BORDER, 1));
		setForegroundColor(BORDER);
		setBorder(border);
	}

	protected void paintFigure(Graphics graphics) {
		graphics.setBackgroundColor(ColorConstants.lightGray);
		Rectangle copy = getBounds().getCopy();
		copy = copy.shrink(3, 3);
		copy = copy.translate(3, 3);
		graphics.fillRectangle(copy);
		graphics.setBackgroundColor(ColorConstants.white);
		copy = getBounds().getCopy();
		copy.shrink(3, 3);
		graphics.fillRectangle(copy);
		graphics.setForegroundColor(BORDER);
	}
}
