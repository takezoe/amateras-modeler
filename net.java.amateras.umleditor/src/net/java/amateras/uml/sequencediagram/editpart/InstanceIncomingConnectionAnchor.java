/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.AbstractConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;

/**
 * @author Takahiro Shida.
 *
 */
public class InstanceIncomingConnectionAnchor extends AbstractConnectionAnchor {

	private AbstractUMLEntityModel relation;
	
	
	public InstanceIncomingConnectionAnchor(IFigure owner, AbstractUMLEntityModel relation) {
		super(owner);
		this.relation = relation;
	}


	/* (”ñ Javadoc)
	 * @see org.eclipse.draw2d.ConnectionAnchor#getLocation(org.eclipse.draw2d.geometry.Point)
	 */
	public Point getLocation(Point reference) {
		Point point = getOwner().getBounds().getCopy().getTop();
		if (relation.getConstraint().x > point.x) {
			point.x = getOwner().getBounds().getCopy().getRight().x;
		} else {
			point.x = getOwner().getBounds().getCopy().getLeft().x;
		}
		point.y = point.y + 10;
		InteractionEditPart.getLayer().translateToAbsolute(point);
		return point;
	}

}
