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
public class OutgoingFeedbackAnchor extends AbstractConnectionAnchor {

	Point point = null;
	
	public OutgoingFeedbackAnchor(IFigure figure, Point point) {
		super(figure);
		this.point = point;
	}

	/* (”ñ Javadoc)
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Point rv = point.getCopy();
		getOwner().translateToAbsolute(rv);
		return rv;
	}

	
}
