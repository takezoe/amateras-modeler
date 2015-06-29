/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.sequencediagram.figure.SyncMessageConnectionFigure;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.gef.EditPolicy;

/**
 * @author Takahiro Shida.
 *
 */
public class SyncMessageEditPart extends MessageEditPart {

	protected IFigure createFigure() {
		SyncMessageModel model = (SyncMessageModel) getModel();
		SyncMessageConnectionFigure figure = new SyncMessageConnectionFigure();
		figure.build(model.isRecursive());
		figure.locateLabel(model.isDirection());
		figure.getLabel().setText(model.getOrder() + "." + model.getName());
		return figure;
	}
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new MessageComponentEditPolicy());
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		SyncMessageConnectionFigure figure = (SyncMessageConnectionFigure) getFigure();
		Label label = figure.getLabel();
		SyncMessageModel model = (SyncMessageModel) getModel();
		if (evt.getPropertyName().equals(MessageModel.P_NAME)) {
			label.setText(model.getOrder() + "." + model.getName());
		}
		if (evt.getPropertyName().equals(SyncMessageModel.P_ORDER)) {
			label.setText(model.getOrder() + "." + model.getName());
		}
		if (evt.getPropertyName().equals(SyncMessageModel.P_DIRECTION)) {
			figure.locateLabel(model.isDirection());
		}
		super.propertyChange(evt);
	}
}
