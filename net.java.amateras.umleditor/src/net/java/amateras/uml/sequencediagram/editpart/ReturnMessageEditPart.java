/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.sequencediagram.figure.ReturnMessageConnectionFigure;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.model.ReturnMessageModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

/**
 * @author Takahiro Shida.
 * 
 */
public class ReturnMessageEditPart extends MessageEditPart {

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command getDeleteCommand(GroupRequest request) {
				return new DeleteReturnCommand((ReturnMessageModel) getModel());
			}
		});
	}
	protected IFigure createFigure() {
		ReturnMessageModel model = (ReturnMessageModel) getModel();
		ReturnMessageConnectionFigure figure = new ReturnMessageConnectionFigure();
		figure.getLabel().setText(model.getName());
		figure.locateLabel(model.isDirection());
		return figure;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		EntityFigure figure = (EntityFigure) getFigure();
		if (evt.getPropertyName().equals(MessageModel.P_NAME)) {
			ReturnMessageModel model = (ReturnMessageModel) getModel();
			figure.getLabel().setText(model.getName());
		} else if (evt.getPropertyName().equals(MessageModel.P_DIRECTION)) {
			ReturnMessageModel model = (ReturnMessageModel) getModel();
			((ReturnMessageConnectionFigure) figure).locateLabel(model.isDirection());
		}
		
		super.propertyChange(evt);
	}

	class DeleteReturnCommand extends Command {
		
		private ReturnMessageModel model;
		
		public DeleteReturnCommand(ReturnMessageModel model) {
			super();
			this.model = model;
		}

		public void execute() {
			model.detachSource();
			model.detachTarget();
		}
		
		public void undo() {
			model.attachSource();
			model.attachTarget();
		}
	}
}
