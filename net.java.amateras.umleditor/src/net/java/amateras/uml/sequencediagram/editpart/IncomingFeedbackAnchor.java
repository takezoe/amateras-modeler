/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author Takahiro Shida.
 *
 */
public class IncomingFeedbackAnchor extends AbstractConnectionAnchor {

	private Point point;
	
	public IncomingFeedbackAnchor(IFigure figure, Point point) {
		super(figure);
		this.point = point;
	}

	/* (”ñ Javadoc)
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Point p = getOwner().getBounds().getLocation().getCopy();
		Point newP = this.point.getCopy();
		InteractionEditPart.getLayer().translateToAbsolute(p);
		InteractionEditPart.getLayer().translateToAbsolute(newP);
		newP.x = p.x;
		return newP;
	}

}
