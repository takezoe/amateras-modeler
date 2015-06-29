/**
 *
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.sequencediagram.figure.RecursiveRouter;
import net.java.amateras.uml.sequencediagram.figure.SequenceFigureFactory;
import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.LifeLineModel;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.model.MessageOrderUtil;
import net.java.amateras.uml.sequencediagram.model.ReturnMessageModel;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.graphics.Color;

/**
 * @author Takahiro Shida.
 *
 */
public class ActivationEditPart extends AbstractUMLEntityEditPart {

	private static Color highLightColor = new Color(null, 200, 200, 240);

	/*
	 * (非 Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		return SequenceFigureFactory.getActivationFigure();
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,
				new ActivationNodeEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE,
				new HighlightEditPolicy());
	}

	/*
	 * (非 Javadoc)
	 *
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(ActivationModel.P_CONSTRAINT)) {
			refreshVisuals();
		}
		if (evt.getPropertyName().equals(ActivationModel.P_SOURCE_CONNECTION)) {
			refreshSourceConnections();
		}
		if (evt.getPropertyName().equals(ActivationModel.P_TARGET_CONNECTION)) {
			refreshTargetConnections();
		}
	}

	private ActivationModel getActivation() {
		return (ActivationModel) getModel();
	}

	private class ActivationNodeEditPolicy extends GraphicalNodeEditPolicy {

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
			ConnectionCommand command = new ConnectionCommand();
			command.setSource(getActivation());
			Point p = request.getLocation().getCopy();
			getFigure().translateToRelative(p);
			command.setPoint(p);
			command.setModel((MessageModel) request.getNewObject());
			request.setStartCommand(command);
			return command;
		}

		protected Command getReconnectTargetCommand(ReconnectRequest request) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		protected Command getReconnectSourceCommand(ReconnectRequest request) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

	}

	private class CreateCompleteCommand extends ConnectionCommand {
		private ActivationModel target;

		private ReturnMessageModel returnMessage;

		/**
		 * 意味的に合わない場合は作成不可.
		 *
		 */
		public boolean canExecute() {
			List<AbstractUMLConnectionModel> connections = getSource().getModelSourceConnections();
			for (Iterator<AbstractUMLConnectionModel> iter = connections.iterator(); iter.hasNext();) {
				MessageModel element = (MessageModel) iter.next();
				if (element instanceof SyncMessageModel) {
					if (getActivation().equals(element.getTarget())) {
						return false;
					}
				}
			}
			if (getSource() instanceof ActivationModel) {
				ActivationModel model = (ActivationModel) getSource();
				LifeLineModel sourceLine = model.getOwnerLine();
				LifeLineModel ownerLine = getActivation().getOwnerLine();
				if (sourceLine == null) {
					return false;
				}
				if (sourceLine.equals(ownerLine)
						&& !model.equals(getActivation())) {
					return false;
				}
			}
			return true;
		}

		public void execute() {
			// 活性区間自動生成.
			Rectangle rectangle = createActivation();
			// コネクションはり.
			getModel().setSource(getSource());
			getModel().setTarget(target);
			getModel().attachSource();
			getModel().attachTarget();
			getActivation().copyPresentation(getModel());

			SyncMessageModel sync = (SyncMessageModel) getModel();
			if (sync.isRecursive()) {
				rectangle.translate(0, RecursiveRouter.DELTA_Y);
			} else {
				if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_SEQUENCE_DIAGRAM_CREATE_RETURN)) {
					createReturnMessage();
				}
			}
			computeConstraint(rectangle);
		}

		/**
		 * 活性区間サイズ計算.
		 *
		 * @param rectangle
		 */
		private void computeConstraint(Rectangle rectangle) {
			target.setConstraint(rectangle.translate(
					ActivationModel.DEFAULT_WIDTH, 0));
			MessageOrderUtil.computeMessageOrders(target.getRoot());
			target.getRoot().adjustLifeLine();
		}

		/**
		 * 返却メッセージ自動生成.
		 *
		 */
		private void createReturnMessage() {
			returnMessage = new ReturnMessageModel();
			returnMessage.setSource(target);
			returnMessage.setTarget(getSource());
			returnMessage.attachSource();
			returnMessage.attachTarget();
			target.copyPresentation(returnMessage);
		}

		/**
		 * 活性区間自動生成.
		 *
		 * @return
		 */
		private Rectangle createActivation() {
			target = new ActivationModel();
			Dimension dimension = new Dimension(ActivationModel.DEFAULT_WIDTH,
					ActivationModel.DEFAULT_HEIGHT);
			Point p = getActivation().getConstraint().getTopLeft().getCopy();
			Point mouseP = getPoint().getCopy();
		//	InteractionEditPart.getLayer().translateToRelative(mouseP);
			p.y = mouseP.y;
			Rectangle rectangle = new Rectangle(p.translate(
					-ActivationModel.DEFAULT_WIDTH / 2, 0), dimension);
			//プレゼンテーション設定
			getActivation().copyPresentation(target);
			getActivation().addActivation(target);
			return rectangle;
		}

		public void undo() {
			// メッセージ削除
			SyncMessageModel sync = (SyncMessageModel) getModel();
			if (!sync.isRecursive() && returnMessage != null) {
				returnMessage.detachSource();
				returnMessage.detachTarget();
			}
			ActivationModel act = (ActivationModel) getModel().getSource();
			getModel().detachSource();
			getModel().detachTarget();
			act.computeCaller();
			getActivation().removeActivation(target);
			getActivation().getRoot().adjustLifeLine();
			MessageOrderUtil.computeMessageOrders(getActivation().getRoot());
		}
	}

	private class HighlightEditPolicy extends GraphicalEditPolicy {
		private Color revertColor;

		/**
		 * @see org.eclipse.gef.EditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
		 */
		public void eraseTargetFeedback(Request request) {
			if (revertColor != null) {
				setContainerBackground(revertColor);
				revertColor = null;
			}
		}

		private Color getContainerBackground() {
			return getContainerFigure().getBackgroundColor();
		}

		private IFigure getContainerFigure() {
			return ((GraphicalEditPart) getHost()).getFigure();
		}

		/**
		 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
		 */
		public EditPart getTargetEditPart(Request request) {
			return request.getType().equals(
					RequestConstants.REQ_SELECTION_HOVER) ? getHost() : null;
		}

		private void setContainerBackground(Color c) {
			getContainerFigure().setBackgroundColor(c);
		}

		/**
		 * Changes the background color of the container to the highlight color
		 */
		protected void showHighlight() {
			if (revertColor == null) {
				revertColor = getContainerBackground();
				setContainerBackground(highLightColor);
			}
		}

		/**
		 * @see org.eclipse.gef.EditPolicy#showTargetFeedback(org.eclipse.gef.Request)
		 */
		public void showTargetFeedback(Request request) {
			if (request.getType().equals(RequestConstants.REQ_CONNECTION_END)
					|| request.getType().equals(
							RequestConstants.REQ_CONNECTION_START))
				showHighlight();
		}
	}

	protected List<AbstractUMLConnectionModel> getModelSourceConnections() {
		return getActivation().getModelSourceConnections();
	}

	protected List<AbstractUMLConnectionModel> getModelTargetConnections() {
		return getActivation().getModelTargetConnections();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		MessageModel model = (MessageModel) connection.getModel();
		if (model instanceof SyncMessageModel) {
			if (model.getTarget() instanceof InstanceModel) {
				return new InstanceOutgoingConnectionAnchor(getFigure(), model
						.getTarget());
			} else {
				return new MessageOutgoingConnectionAnchor(getFigure(), model
						.getTarget());
			}
		} else if (model instanceof ReturnMessageModel) {
			return new ReturnMessageOutgoingAnchor(getFigure(), model
					.getTarget());
		}
		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		MessageModel model = (MessageModel) connection.getModel();
		if (model instanceof SyncMessageModel) {
			return new MessageIncomingConnectionAnchor(getFigure(), model
					.getSource());
		} else if (model instanceof ReturnMessageModel) {
			return new ReturnMessageIncomingAnchor(getFigure(), model
					.getSource());
		}
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (request instanceof CreateConnectionRequest) {
			DropRequest connectionRequest = (DropRequest) request;
			Point point = connectionRequest.getLocation();
			Point in = point.getCopy();
			getFigure().translateToRelative(in);
			return new OutgoingFeedbackAnchor(getFigure(), in);
		}
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;
			ConnectionCommand command = (ConnectionCommand) connectionRequest
					.getStartCommand();
			return new IncomingFeedbackAnchor(getFigure(), command.getPoint()
					.getCopy());
		}
		return null;
	}


}
