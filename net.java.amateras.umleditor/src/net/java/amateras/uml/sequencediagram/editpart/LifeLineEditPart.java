/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.LifeLineModel;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.model.MessageOrderUtil;
import net.java.amateras.uml.sequencediagram.model.ReturnMessageModel;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Takahiro Shida.
 *
 */
public class LifeLineEditPart extends AbstractUMLEditPart implements NodeEditPart {
	
	private static Color highLightColor = new Color(null, 0, 0, 240);

	private int width = 2;
	
	/* (非 Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		Figure figure = new Figure() {
			
			public void paint(Graphics graphics) {
				if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
					graphics.setAntialias(SWT.ON);
					graphics.setTextAntialias(SWT.ON);
				}
				super.paint(graphics);
			}
			protected void paintFigure(Graphics graphics) {
				Rectangle rectangle = getClientArea().getCopy();
				graphics.setLineStyle(SWT.LINE_DOT);
				graphics.setLineWidth(width);
				graphics.drawLine(rectangle.getTopLeft().translate(0, 10), rectangle.getBottomLeft());
			}
		};
		return figure;
	}

	/* (非 Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new LifeLineNodeEditPolicy());
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, new HighlightEditPolicy());
	}

	/* (非 Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(LifeLineModel.P_CONSTRAINT)) {
			refreshVisuals();
		}
	}

	protected void refreshVisuals() {
		Object model = getModel();
		if(model instanceof AbstractUMLEntityModel){
			Rectangle constraint = ((AbstractUMLEntityModel)model).getConstraint();
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,getFigure(), constraint);
		}
	}

	private LifeLineModel getLifeLine() {
		return (LifeLineModel) getModel();
	}
	
	/**
	 * 線を引けるように見せる.
	 * モデルに対する変更は実施しない.
	 * @author Takahiro Shida.
	 *
	 */
	private class LifeLineNodeEditPolicy extends GraphicalNodeEditPolicy  {

		protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
			CreateCompleteCommand command = new CreateCompleteCommand();
			ConnectionCommand locationCommand = (ConnectionCommand) request.getStartCommand();
			command.setSource(locationCommand.getSource());
			command.setPoint(locationCommand.getPoint());
			command.setModel(locationCommand.getModel());
			return locationCommand.chain(command);
		}

		protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
			CreateConnectionFeedbackCommand command = new CreateConnectionFeedbackCommand();
			ActivationModel newModel = new ActivationModel();
			//プレゼンテーション設定.
			getLifeLine().copyPresentation(newModel);
			command.setPoint(request.getLocation());
			command.setModel((MessageModel) request.getNewObject());
			command.setSource(newModel);
			request.setStartCommand(command);
			return command;
		}

		protected Command getReconnectTargetCommand(ReconnectRequest request) {
			return null;
		}

		protected Command getReconnectSourceCommand(ReconnectRequest request) {
			return null;
		}

	}
	
	private class CreateConnectionFeedbackCommand extends ConnectionCommand {

		public boolean canExecute() {
			return true;
		}
		
		public void execute() {
			Dimension dimension = new Dimension(ActivationModel.DEFAULT_WIDTH,ActivationModel.DEFAULT_HEIGHT);
			Point p = getLifeLine().getConstraint().getTopLeft().getCopy();
			Point mouseP = getPoint().getCopy();
			InteractionEditPart.getLayer().translateToRelative(mouseP);
			p.y = mouseP.y;
			getSource().setConstraint(new Rectangle(p.translate(-ActivationModel.DEFAULT_WIDTH / 2,0), dimension));
			getLifeLine().addActivation((ActivationModel) getSource());
		}
		
		public void undo() {
			getLifeLine().removeActivation((ActivationModel) getSource());
		}
	}

	private class CreateCompleteCommand extends ConnectionCommand {
		private ActivationModel newModel;

		private ReturnMessageModel returnMessage;
		
		public boolean canExecute() {
			AbstractUMLEntityModel entityModel = getSource();
			if (entityModel instanceof ActivationModel) {
				ActivationModel model = (ActivationModel) entityModel;
				if (getLifeLine().equals(model.getOwnerLine())) {
					return false;
				}
			}
			return super.canExecute();
		}
		
		public void execute() {
			//活性区間自動生成.
			newModel = new ActivationModel();
			Dimension dimension = new Dimension(ActivationModel.DEFAULT_WIDTH,ActivationModel.DEFAULT_HEIGHT);
			Point p = getLifeLine().getConstraint().getTopLeft().getCopy();
			Point mouseP = getPoint().getCopy();
	//		InteractionEditPart.getLayer().translateToRelative(mouseP);
			p.y = mouseP.y;
			getLifeLine().addActivation(newModel);
			//コネクションはり.
			getModel().setSource(getSource());
			getModel().setTarget(newModel);
			getModel().attachSource();
			getModel().attachTarget();
			//ReturnMessage作成.
			if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_SEQUENCE_DIAGRAM_CREATE_RETURN)) {
				createReturnMessage();
			}
			newModel.setConstraint(new Rectangle(p.translate(-ActivationModel.DEFAULT_WIDTH / 2,0), dimension));
			newModel.computeCaller();
			newModel.getRoot().adjustLifeLine();
			//プレゼンテーション設定.
			getLifeLine().copyPresentation(newModel);
			MessageOrderUtil.computeMessageOrders(getLifeLine().getOwner().getRoot());
		}

		private void createReturnMessage() {
			returnMessage = new ReturnMessageModel();
			returnMessage.setSource(newModel);
			returnMessage.setTarget(getSource());
			returnMessage.attachSource();
			returnMessage.attachTarget();
		}
		
		public void undo() {
			ActivationModel model = (ActivationModel) getModel().getSource();
			getModel().detachSource();
			getModel().detachTarget();
			if (returnMessage != null) {
				returnMessage.detachSource();
				returnMessage.detachTarget();
			}
//			model.computeChild();
			model.computeCaller();
			newModel.getRoot().adjustLifeLine();
			getLifeLine().removeActivation(newModel);
			MessageOrderUtil.computeMessageOrders(getLifeLine().getOwner().getRoot());
		}		
	}
	
	private class HighlightEditPolicy extends GraphicalEditPolicy {

		private Color revertColor;

		/**
		 * @see org.eclipse.gef.EditPolicy#eraseTargetFeedback(org.eclipse.gef.Request)
		 */
		public void eraseTargetFeedback(Request request) {
			if (revertColor != null) {
				width = 2;
				setContainerForeground(revertColor);
				revertColor = null;
			}
		}

		private Color getContainerForeground() {
			return getContainerFigure().getForegroundColor();
		}

		private IFigure getContainerFigure() {
			return ((GraphicalEditPart)getHost()).getFigure();
		}

		/**
		 * @see org.eclipse.gef.EditPolicy#getTargetEditPart(org.eclipse.gef.Request)
		 */
		public EditPart getTargetEditPart(Request request) {
			return request.getType().equals(RequestConstants.REQ_SELECTION_HOVER)
				? getHost() : null;
		}

		private void setContainerForeground(Color c) {
			getContainerFigure().setForegroundColor(c);
		}

		/**
		 * Changes the background color of the container to the highlight color
		 */
		protected void showHighlight() {
			if (revertColor == null) {
				width = 3;
				revertColor = getContainerForeground();
				setContainerForeground(highLightColor);
			}
		}

		/**
		 * @see org.eclipse.gef.EditPolicy#showTargetFeedback(org.eclipse.gef.Request)
		 */
		public void showTargetFeedback(Request request) {
			if (request.getType().equals(RequestConstants.REQ_CONNECTION_START)
				|| request.getType().equals(RequestConstants.REQ_CONNECTION_END))
				showHighlight();
		}

	}
	
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {

		return null;
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return null;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (request instanceof CreateConnectionRequest) {
			DropRequest connectionRequest = (DropRequest) request;
			Point point = connectionRequest.getLocation();
			return new OutgoingFeedbackAnchor(getFigure(), point.getCopy());
		}
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		if (request instanceof CreateConnectionRequest) {
			CreateConnectionRequest connectionRequest = (CreateConnectionRequest) request;
			ConnectionCommand command = (ConnectionCommand) connectionRequest.getStartCommand();
				return new IncomingFeedbackAnchor(getFigure(), command.getPoint().getCopy());				
		}
		return null;
	}
}
