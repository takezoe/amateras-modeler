package net.java.amateras.uml.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.classdiagram.model.StereoTypeModel;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.BendpointConnectionRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RelativeBendpoint;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.BendpointEditPolicy;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.BendpointRequest;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractUMLConnectionEditPart extends
		AbstractConnectionEditPart implements PropertyChangeListener {

	private StereoTypeDirectEditManager directManager;

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new EntityComponentEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new UMLConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new StereoTypeDirectEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_BENDPOINTS_ROLE,
				new UMLConnectionBendpointEditPolicy());
	}

	@Override
	public void activate() {
		super.activate();
		((AbstractUMLModel) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((AbstractUMLModel) getModel()).removePropertyChangeListener(this);
	}

	/** EditPolicy for Entity */
	private class EntityComponentEditPolicy extends ComponentEditPolicy {
		@Override
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setModel((AbstractUMLConnectionModel) getModel());
			return command;
		}
	}

	/** Delete connection command */
	private class DeleteCommand extends Command {
		private AbstractUMLConnectionModel model;

		public void setModel(AbstractUMLConnectionModel model) {
			this.model = model;
		}

		@Override
		public void execute() {
			model.detachSource();
			model.detachTarget();
		}

		@Override
		public void undo(){
			model.attachSource();
			model.attachTarget();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}

	// ベンド・ポイントをモデルの情報を使用して更新する
	protected void refreshBendpoints() {
		getConnectionFigure().setConnectionRouter(new BendpointConnectionRouter());
		List<ConnectionBendpoint> bendpoints = ((AbstractUMLConnectionModel) getModel()).getBendpoints();
		List<RelativeBendpoint> constraint = new ArrayList<RelativeBendpoint>();
		for (int i = 0; i < bendpoints.size(); i++) {
			ConnectionBendpoint wbp = (ConnectionBendpoint) bendpoints.get(i);
			RelativeBendpoint rbp = new RelativeBendpoint(getConnectionFigure());
			rbp.setRelativeDimensions(wbp.getFirstRelativeDimension(), wbp.getSecondRelativeDimension());
			rbp.setWeight((i + 1) / ((float) constraint.size() + 1));
			constraint.add(rbp);
		}
		getConnectionFigure().setRoutingConstraint(constraint);
	}

	@Override
	protected void refreshVisuals() {
		if (getFigure() instanceof PresentationFigure) {
			PresentationFigure figure = (PresentationFigure) getFigure();
			figure.updatePresentation((AbstractUMLModel) getModel());
		}
		refreshBendpoints();
		super.refreshVisuals();
	}

	protected Label getStereoTypeLabel() {
		return null;
	}

	@Override
	public void performRequest(Request req) {
		if (getStereoTypeLabel() != null || getModel() instanceof StereoTypeModel) {
			if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT) || req.getType().equals(RequestConstants.REQ_OPEN)) {
				performDirectEdit();
				return;
			}
		}
		super.performRequest(req);
	}

	private void performDirectEdit() {
		if (directManager == null) {
			directManager = new StereoTypeDirectEditManager();
		}
		directManager.show();
	}

	/**
	 * DirectEditManager
	 */
	private class StereoTypeDirectEditManager extends DirectEditManager {

		public StereoTypeDirectEditManager() {
			super(AbstractUMLConnectionEditPart.this, TextCellEditor.class,
					new CompositeCellEditorLocator());
		}

		@Override
		protected void initCellEditor() {
			getCellEditor().setValue(
					((StereoTypeModel) getModel()).getStereoType());
			Text text = (Text) getCellEditor().getControl();
			text.selectAll();
		}
	}

	/**
	 * CellEditorLocator
	 */
	private class CompositeCellEditorLocator implements CellEditorLocator {
		@Override
		public void relocate(CellEditor celleditor) {
			Text text = (Text) celleditor.getControl();
			// Point pref = text.computeSize(-1, -1);
			Rectangle rect = getStereoTypeLabel().getBounds().getCopy();
			figure.translateToAbsolute(rect);
			if (rect.width == 0) {
				text.setBounds(rect.x - 20, rect.y, 40, rect.height);
			} else {
				text.setBounds(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/**
	 * DirectEditCommand
	 */
	private class DirectEditCommand extends Command {

		private String oldStereoType;

		private String newStereoType;

		@Override
		public void execute() {
			StereoTypeModel model = (StereoTypeModel) getModel();
			oldStereoType = model.getStereoType();
			model.setStereoType(newStereoType);
		}

		public void setStereoType(String stereoType) {
			newStereoType = stereoType;
		}

		@Override
		public void undo() {
			StereoTypeModel model = (StereoTypeModel) getModel();
			model.setStereoType(oldStereoType);
		}
	}

	/**
	 * DirectEditPolicy
	 */
	private class StereoTypeDirectEditPolicy extends DirectEditPolicy {

		@Override
		protected Command getDirectEditCommand(DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			command.setStereoType((String) request.getCellEditor().getValue());
			return command;
		}

		@Override
		protected void showCurrentEditValue(DirectEditRequest request) {
		}
	}

	private class UMLConnectionEndpointEditPolicy extends ConnectionEndpointEditPolicy {
	}
	
	private class UMLConnectionBendpointEditPolicy extends BendpointEditPolicy {

		@Override
		protected Command getCreateBendpointCommand(BendpointRequest request) {
            CreateBendpointCommand command = new CreateBendpointCommand();
            Point p = request.getLocation();
            Connection conn = getConnection();

            conn.translateToRelative(p);

            command.setLocation(p);
            Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
            Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

            conn.translateToRelative(ref1);
            conn.translateToRelative(ref2);

            command.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
            command.setConnectionModel((AbstractUMLConnectionModel) request.getSource().getModel());
            command.setIndex(request.getIndex());
            return command;
		}

		@Override
		protected Command getDeleteBendpointCommand(BendpointRequest request) {
            BendpointCommand command = new DeleteBendpointCommand();
            Point p = request.getLocation();
            command.setLocation(p);
            command.setConnectionModel((AbstractUMLConnectionModel) request.getSource().getModel());
            command.setIndex(request.getIndex());
            return command;
		}

		@Override
		protected Command getMoveBendpointCommand(BendpointRequest request) {
            MoveBendpointCommand command = new MoveBendpointCommand();
            Point p = request.getLocation();
            Connection conn = getConnection();

            conn.translateToRelative(p);

            command.setLocation(p);

            Point ref1 = getConnection().getSourceAnchor().getReferencePoint();
            Point ref2 = getConnection().getTargetAnchor().getReferencePoint();

            conn.translateToRelative(ref1);
            conn.translateToRelative(ref2);

            command.setRelativeDimensions(p.getDifference(ref1), p.getDifference(ref2));
            command.setConnectionModel((AbstractUMLConnectionModel) request.getSource().getModel());
            command.setIndex(request.getIndex());
            return command;
		}

	}

	private class CreateBendpointCommand extends BendpointCommand {

		@Override
		public void execute() {
            ConnectionBendpoint rbp = new ConnectionBendpoint(
                    getFirstRelativeDimension(), getSecondRelativeDimension());
            getConnectionModel().addBendpoint(getIndex(), rbp);
            super.execute();
		}

		@Override
		public void undo() {
			super.undo();
            getConnectionModel().removeBendpoint(getIndex());
		}
	}

	private class MoveBendpointCommand extends BendpointCommand {

		private ConnectionBendpoint oldBendpoint = null;

		@Override
		public void execute() {
			ConnectionBendpoint bp = new ConnectionBendpoint(
                    getFirstRelativeDimension(), getSecondRelativeDimension());
            setOldBendpoint((ConnectionBendpoint) getConnectionModel().getBendpoints().get(getIndex()));
            getConnectionModel().replaceBendpoint(getIndex(), bp);
            super.execute();
		}

        protected ConnectionBendpoint getOldBendpoint() {
            return oldBendpoint;
        }

        public void setOldBendpoint(ConnectionBendpoint bp) {
            oldBendpoint = bp;
        }

        @Override
        public void undo() {
            super.undo();
            getConnectionModel().replaceBendpoint(getIndex(), getOldBendpoint());
        }
	}

	private class DeleteBendpointCommand extends BendpointCommand {
		private ConnectionBendpoint bendpoint = null;

		@Override
		public void execute() {
			bendpoint = (ConnectionBendpoint) getConnectionModel().getBendpoints().get(getIndex());
			getConnectionModel().removeBendpoint(getIndex());
			super.execute();
		}

		@Override
        public void undo() {
            super.undo();
            getConnectionModel().addBendpoint(getIndex(), bendpoint);
        }
	}

	/** The base class of commands which operates bendpoint */
    private class BendpointCommand extends Command {

        protected int index = 0;
        protected Point location = null;
        protected AbstractUMLConnectionModel connectionModel = null;
        private Dimension d1 = null;
        private Dimension d2 = null;

        protected Dimension getFirstRelativeDimension() {
            return d1;
        }

        protected Dimension getSecondRelativeDimension() {
            return d2;
        }

        protected int getIndex() {
            return index;
        }

        @SuppressWarnings("unused")
        protected Point getLocation() {
            return location;
        }

        protected AbstractUMLConnectionModel getConnectionModel() {
            return connectionModel;
        }

        @Override
        public void redo() {
            execute();
        }

        public void setRelativeDimensions(Dimension dim1, Dimension dim2) {
            d1 = dim1;
            d2 = dim2;
        }

        public void setIndex(int i) {
            index = i;
        }

        public void setLocation(Point p) {
            location = p;
        }

        public void setConnectionModel(AbstractUMLConnectionModel connection) {
            connectionModel = connection;
        }
    }
}
