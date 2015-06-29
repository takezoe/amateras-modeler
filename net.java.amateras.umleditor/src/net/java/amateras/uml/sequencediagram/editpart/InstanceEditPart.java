/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.sequencediagram.figure.ActorFigure;
import net.java.amateras.uml.sequencediagram.figure.InstanceFigure;
import net.java.amateras.uml.sequencediagram.figure.SequenceFigureFactory;
import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.ActorModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.InteractionModel;
import net.java.amateras.uml.sequencediagram.model.LifeLineModel;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.model.MessageOrderUtil;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;

/**
 * @author Takahiro Shida.
 * 
 */
public class InstanceEditPart extends AbstractUMLEntityEditPart {

	/*
	 * (”ñ Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		InstanceModel model = (InstanceModel) getModel();
		InstanceFigure figure = null;
		if (model instanceof ActorModel) {
			figure = new ActorFigure().build();
		} else {
			figure = SequenceFigureFactory.getInstanceFigure().build();
		}
		figure.setInstanceName(model.getName());
		if (model.getType() != null) {
			if(showSimpleName()){
				figure.setTypeName(model.getType().getSimpleName());
			} else {
				figure.setTypeName(model.getType().getName());
			}
		} else {
			figure.setTypeName("");
		}
		return figure;
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new ComponentEditPolicy() {
			protected Command createDeleteCommand(GroupRequest deleteRequest) {
				InstanceModel model = (InstanceModel) getModel();
				List list = model.getModel().getChildren();
				for (Iterator iter = list.iterator(); iter.hasNext();) {
					AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter
							.next();
					if (!element.getModelSourceConnections().isEmpty()) {
						return null;
					}
				}
				DeleteCommand command = new DeleteCommand();
				command.setModel(model);
				return command;
			}
		});
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new InstanceNodeEditPolicy());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(InstanceModel.P_NAME)
				|| evt.getPropertyName().equals(InstanceModel.P_TYPE)) {
			refreshName();
		}
		super.propertyChange(evt);
	}

	private boolean showSimpleName() {
		return UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_SEQUENCE_DIAGRAM_SHOW_SIMPLE_NAME);
	}
	
	private InstanceModel getInstance() {
		return (InstanceModel) getModel();
	}

	private void refreshName() {
		InstanceModel model = (InstanceModel) getModel();
		InstanceFigure figure = (InstanceFigure) getFigure();
		String name = model.getName();
		figure.setInstanceName(name);
		if (model.getType() != null) {
			if(showSimpleName()){
				figure.setTypeName(model.getType().getSimpleName());
			} else {
				figure.setTypeName(model.getType().getName());
			}
		} else {
			figure.setTypeName("");
		}
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		MessageModel model = (MessageModel) connection.getModel();
		if (model instanceof SyncMessageModel) {
			return new InstanceIncomingConnectionAnchor(getFigure(), model
					.getSource());
		}
		return null;
	}

	class InstanceNodeEditPolicy extends GraphicalNodeEditPolicy {

		protected Command getConnectionCompleteCommand(
				CreateConnectionRequest request) {
			CreateCompleteCommand command = new CreateCompleteCommand();
			ConnectionCommand locationCommand = (ConnectionCommand) request
					.getStartCommand();
			command.setPoint(locationCommand.getPoint());
			command.setModel(locationCommand.getModel());
			command.setSource(locationCommand.getSource());
			return locationCommand.chain(command);
		}

		protected Command getConnectionCreateCommand(
				CreateConnectionRequest request) {
			return null;
		}

		protected Command getReconnectTargetCommand(ReconnectRequest request) {
			return null;
		}

		protected Command getReconnectSourceCommand(ReconnectRequest request) {
			return null;
		}

	}

	private class CreateCompleteCommand extends ConnectionCommand {

		private ActivationModel newModel;

		public boolean canExecute() {
			InstanceModel model = getInstance();
			List list = model.getModel().getChildren();
			for (Iterator iter = list.iterator(); iter.hasNext();) {
				AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter
						.next();
				if (!element.getModelSourceConnections().isEmpty()) {
					return false;
				}
			}
			return true;
		}

		public void execute() {
			// Creates connections
			Rectangle rectangle = getInstance().getConstraint().getCopy();
			Point p = rectangle.getTopLeft();
			p.y = getPoint().y;
			rectangle.setLocation(p);
			getInstance().setConstraint(rectangle);
			getModel().setSource(getSource());
			getModel().setTarget(getInstance());
			getModel().attachSource();
			getModel().attachTarget();
			ActivationModel act = (ActivationModel) getSource();
			act.computeCaller();
			newModel = new ActivationModel();
			newModel.setMovable(false);
			Point actP = rectangle.getBottom().getCopy().getTranslated(
					-ActivationModel.DEFAULT_WIDTH / 2, 20);
			Rectangle newRect = new Rectangle(actP, new Dimension(
					ActivationModel.DEFAULT_WIDTH,
					ActivationModel.DEFAULT_HEIGHT));
			getInstance().copyPresentation(newModel);
			getInstance().getModel().addActivation(newModel);
			getInstance().setActive(newModel);
			newModel.setConstraint(newRect);
			MessageOrderUtil.computeMessageOrders(getInstance().getRoot());
			super.execute();
		}

		public void undo() {
			super.undo();
			ActivationModel act = (ActivationModel) getSource();
			getModel().detachSource();
			getModel().detachTarget();
			act.computeCaller();
			Rectangle rectangle = getInstance().getConstraint().getCopy();
			Point p = rectangle.getTopLeft();
			p.y = InstanceModel.DEFAULT_LOCATION;
			rectangle.setLocation(p);
			getInstance().setConstraint(rectangle);
			getInstance().getModel().removeActivation(newModel);
			getInstance().setActive(null);
			MessageOrderUtil.computeMessageOrders(getInstance().getRoot());
		}
	}

	class DeleteCommand extends Command {
		InstanceModel model;

		LifeLineModel lineModel;

		InteractionModel interactionModel;

		public void setModel(InstanceModel model) {
			this.model = model;
			lineModel = model.getModel();
			interactionModel = model.getRoot();
		}

		public void execute() {
			super.execute();
			interactionModel.removeInstance(model);
			interactionModel.removeLifeLine(lineModel);
		}

		public void undo() {
			super.undo();
			interactionModel.addInstance(model);
		}
	}

}
