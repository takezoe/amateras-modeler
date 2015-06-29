package net.java.amateras.uml.activitydiagram.figure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;

/**
 * 
 * @author Naoki Takezoe
 */
public class EmptyBorder extends LineBorder {
	
	public EmptyBorder(int width){
		setWidth(width);
	}
	
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
	}

}
