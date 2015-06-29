package net.java.amateras.db.visual.editpart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.SWT;

/**
 * This class has been ported from AmaterasUML.
 * 
 * @author Takahiro Shida
 */
public class AnchorEditPart extends AbstractDBConnectionEditPart {

	protected void createEditPolicies() {
		super.createEditPolicies();
		removeEditPolicy(EditPolicy.DIRECT_EDIT_ROLE);
	}
	
	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		connection.setLineStyle(SWT.LINE_DASH);
		return connection;
	}
}
