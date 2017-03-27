package net.java.amateras.uml.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.EntityModel;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

public abstract class AbstractUMLEntityEditPart extends AbstractUMLEditPart
		implements NodeEditPart {

	private EntityDirectEditManager directManager = null;

	@Override
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new EntityComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new EntityLayoutEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new EntityDirectEditPolicy());
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		Object model = getModel();
		if (model instanceof AbstractUMLEntityModel) {
			// ����̎擾
			Rectangle constraint = ((AbstractUMLEntityModel) model)
					.getConstraint();
			// Rectangle�I�u�W�F�N�g�𐧖�Ƃ��ăr���[�ɐݒ肷��
			// setLayoutConstraint���\�b�h�͐eEditPart����Ăяo��
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,
					getFigure(), constraint);
		}
	}

	/** ����EditPart��ڑ����Ƃ���R�l�N�V�����E���f���̃��X�g��Ԃ� */
	@Override
	protected List<AbstractUMLConnectionModel> getModelSourceConnections() {
		return ((AbstractUMLEntityModel) getModel())
				.getModelSourceConnections();
	}

	/** ����EditPart��ڑ���Ƃ���R�l�N�V�����E���f���̃��X�g��Ԃ� */
	@Override
	protected List<AbstractUMLConnectionModel> getModelTargetConnections() {
		return ((AbstractUMLEntityModel) getModel())
				.getModelTargetConnections();
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AbstractUMLEntityModel.P_FORCE_UPDATE)) {
			refreshChildren();
		}
		refreshVisuals();
		refreshSourceConnections();
		refreshTargetConnections();
	}

	/** �G���e�B�e�B�̃G�f�B�b�g�|���V�[ */
	private class EntityComponentEditPolicy extends ComponentEditPolicy {
		@Override
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setRootModel(getHost().getParent().getModel());
			command.setTargetModel(getHost().getModel());
			return command;
		}
	}

	/** �폜�R�}���h */
	public static class DeleteCommand extends Command {

		private AbstractUMLEntityModel container;

		private AbstractUMLModel model;

		// �폜�Ώۂ̃��f�����\�[�X�Ƃ���R�l�N�V�����̃��X�g
		private List<AbstractUMLConnectionModel> sourceConnections = new ArrayList<AbstractUMLConnectionModel>();

		// �폜�Ώۂ̃��f�����^�[�Q�b�g�Ƃ���R�l�N�V�����̃��X�g
		private List<AbstractUMLConnectionModel> targetConnections = new ArrayList<AbstractUMLConnectionModel>();

		@Override
		public void execute() {
			// ���f�����폜����O�ɂ��̃��f�����\�[�X�Ƃ���R�l�N�V�������L�^���Ă���
			sourceConnections.addAll(((AbstractUMLEntityModel) model)
					.getModelSourceConnections());
			// �폜�Ώۂ̃��f�����^�[�Q�b�g�Ƃ���R�l�N�V�����̋L�^
			targetConnections.addAll(((AbstractUMLEntityModel) model)
					.getModelTargetConnections());
			// �폜�Ώۂ̃��f�����\�[�X�Ƃ���R�l�N�V�����̍폜
			for (int i = 0; i < sourceConnections.size(); i++) {
				AbstractUMLConnectionModel model = (AbstractUMLConnectionModel) sourceConnections
						.get(i);
				model.detachSource();
				model.detachTarget();
			}
			// �폜�Ώۂ̃��f�����^�[�Q�b�g�Ƃ���R�l�N�V�����̍폜
			for (int i = 0; i < targetConnections.size(); i++) {
				AbstractUMLConnectionModel model = (AbstractUMLConnectionModel) targetConnections
						.get(i);
				model.detachSource();
				model.detachTarget();
			}
			container.removeChild(model);
		}

		public void setRootModel(Object root) {
			this.container = (AbstractUMLEntityModel) root;
		}

		public void setTargetModel(Object model) {
			this.model = (AbstractUMLModel) model;
		}

		@Override
		public void undo() {
			container.addChild(model);
			// �R�l�N�V���������ɖ߂�
			for (int i = 0; i < sourceConnections.size(); i++) {
				AbstractUMLConnectionModel model = (AbstractUMLConnectionModel) sourceConnections
						.get(i);
				model.attachSource();
				model.attachTarget();
			}
			for (int i = 0; i < targetConnections.size(); i++) {
				AbstractUMLConnectionModel model = (AbstractUMLConnectionModel) targetConnections
						.get(i);
				model.attachSource();
				model.attachTarget();
			}
			// �R�l�N�V�����𕜌����邽�߂ɋL�^���Ă����f�[�^���N���A����
			sourceConnections.clear();
			targetConnections.clear();
		}
	}

	/** �R�l�N�V�����̃G�f�B�b�g�|���V�[ */
	private class NodeEditPolicy extends GraphicalNodeEditPolicy {

		@Override
		protected Command getConnectionCompleteCommand(
				CreateConnectionRequest request) {
			AbstractUMLConnectionModel conn = ((CreateConnectionCommand) request
					.getStartCommand()).getConnectionModel();
			AbstractUMLEntityModel model = (AbstractUMLEntityModel) getHost()
					.getModel();
			// if(!model.canTarget(conn)){
			// return null;
			// }
			CreateConnectionCommand command = (CreateConnectionCommand) request
					.getStartCommand();
			command.setTarget(model);
			CompoundCommand compoundCommand = new CompoundCommand();
			compoundCommand.add(command);
			if (conn.getSource().equals(model)) {
				IFigure hostFigure = getHostFigure();
				Rectangle bounds = hostFigure.getBounds();
				CreateBendPointCommand pointCommand = new CreateBendPointCommand();
				pointCommand.setModel(conn);
				pointCommand.setSourceBounds(bounds);
				compoundCommand.add(pointCommand);
			}
			return compoundCommand.unwrap();
		}

		@Override
		protected Command getConnectionCreateCommand(
				CreateConnectionRequest request) {
			AbstractUMLConnectionModel conn = (AbstractUMLConnectionModel) request
					.getNewObject();
			AbstractUMLEntityModel model = (AbstractUMLEntityModel) getHost()
					.getModel();
			CreateConnectionCommand command = new CreateConnectionCommand();
			command.setConnection(conn);
			command.setSource(model);
			request.setStartCommand(command);
			return command;
		}

		@Override
		protected Command getReconnectTargetCommand(ReconnectRequest request) {
			AbstractUMLConnectionModel conn = (AbstractUMLConnectionModel) request
					.getConnectionEditPart().getModel();
			AbstractUMLEntityModel model = (AbstractUMLEntityModel) getHost()
					.getModel();
			// if(!model.canTarget(conn)){
			// return null;
			// }
			ReconnectTargetCommand command = new ReconnectTargetCommand();
			command.setConnection(conn);
			command.setTarget(model);
			return command;
		}

		@Override
		protected Command getReconnectSourceCommand(ReconnectRequest request) {
			AbstractUMLConnectionModel conn = (AbstractUMLConnectionModel) request
					.getConnectionEditPart().getModel();
			AbstractUMLEntityModel model = (AbstractUMLEntityModel) getHost()
					.getModel();
			// if(!model.canSource(conn)){
			// return null;
			// }
			ReconnectSourceCommand command = new ReconnectSourceCommand();
			command.setConnection(conn);
			command.setSource(model);
			return command;
		}

	}

	/**
	 * ���ȑJ�ڂ̍ۂɎ����I�Ƀx���h�|�C���g��ǉ�����.
	 *
	 * @author shidat
	 *
	 */
	private class CreateBendPointCommand extends Command {
		AbstractUMLConnectionModel model;

		private Rectangle bounds;

		public void setModel(AbstractUMLConnectionModel model) {
			this.model = model;
		}

		public void setSourceBounds(Rectangle bounds) {
			this.bounds = bounds;
		}

		@Override
		public void execute() {
			super.execute();
			int width = bounds.width / 2 + 20;
			int height = -bounds.height / 2 - 20;
			ConnectionBendpoint bendpoint = new ConnectionBendpoint(
					new Dimension(0, 0), new Dimension(width, 0));
			model.addBendpoint(0, bendpoint);
			bendpoint = new ConnectionBendpoint(new Dimension(width, 0),
					new Dimension(width, height));
			model.addBendpoint(1, bendpoint);
			bendpoint = new ConnectionBendpoint(new Dimension(width, height),
					new Dimension(0, height));
			model.addBendpoint(2, bendpoint);
		}
	}

	/** �R�l�N�V�����̍쐬�R�}���h */
	private class CreateConnectionCommand extends Command {

		private AbstractUMLEntityModel source;

		private AbstractUMLEntityModel target;

		private AbstractUMLConnectionModel connection;

		public AbstractUMLConnectionModel getConnectionModel() {
			return connection;
		}

		@Override
		public boolean canExecute() {
			// �\�[�X���^�[�Q�b�g��null�̏ꍇ�͎��s�s��
			if (source == null || target == null) {
				return false;
			}
			// // �\�[�X�ƃ^�[�Q�b�g������̏ꍇ�����s�s��
			// if (source == target) {
			// return false;
			// }
			return true;
		}

		@Override
		public void execute() {
			connection.attachSource();
			connection.attachTarget();
			// connection.setForegroundColor(source.getForegroundColor().getRGB());
		}

		public void setConnection(Object model) {
			connection = (AbstractUMLConnectionModel) model;
		}

		public void setSource(Object model) {
			source = (AbstractUMLEntityModel) model;
			connection.setSource(source);
		}

		public void setTarget(Object model) {
			target = (AbstractUMLEntityModel) model;
			connection.setTarget(target);
		}

		@Override
		public void undo() {
			// �R�l�N�V�������\�[�X�ƃ^�[�Q�b�g������O��
			connection.detachSource();
			connection.detachTarget();
		}
	}

	/** �R�l�N�V�����̃^�[�Q�b�g���Đڑ�����R�}���h */
	private class ReconnectTargetCommand extends Command {

		private AbstractUMLEntityModel target;

		private AbstractUMLEntityModel oldTarget;

		private AbstractUMLConnectionModel connection;

		private List<ConnectionBendpoint> oldBendpoints;

		@Override
		public void execute() {
			oldBendpoints = new ArrayList<ConnectionBendpoint>(connection.getBendpoints());
			connection.detachTarget();
			connection.setTarget(target);
			connection.attachTarget();
			for (Iterator<ConnectionBendpoint> iterator = oldBendpoints.iterator(); iterator.hasNext();) {
				ConnectionBendpoint point = (ConnectionBendpoint) iterator.next();
				connection.removeBendpoint(point);
			}
		}

		public void setConnection(Object model) {
			connection = (AbstractUMLConnectionModel) model;
			oldTarget = connection.getTarget();
		}

		public void setTarget(Object model) {
			target = (AbstractUMLEntityModel) model;
		}

		@Override
		public boolean canExecute() {
			// �\�[�X���^�[�Q�b�g��null�̏ꍇ�͎��s�s��
			if (connection.getSource() == null || target == null) {
				return false;
			}
			// �\�[�X�ƃ^�[�Q�b�g������̏ꍇ�����s�s��
			if (connection.getSource().equals(target)) {
				return false;
			}
			return true;
		}

		@Override
		public void undo() {
			connection.detachTarget();
			connection.setTarget(oldTarget);
			connection.attachTarget();
			for (int i = 0; i < oldBendpoints.size(); i++) {
				connection.addBendpoint(i, (ConnectionBendpoint) oldBendpoints
						.get(i));
			}
		}
	}

	/** �R�l�N�V�����̃\�[�X���Đڑ�����R�}���h */
	private class ReconnectSourceCommand extends Command {

		private AbstractUMLEntityModel source;

		private AbstractUMLEntityModel oldSource;

		private AbstractUMLConnectionModel connection;

		private List<ConnectionBendpoint> oldBendpoints;

		@Override
		public void execute() {
			oldBendpoints = new ArrayList<ConnectionBendpoint>(connection.getBendpoints());
			connection.detachSource();
			connection.setSource(source);
			connection.attachSource();
			for (Iterator<ConnectionBendpoint> iterator = oldBendpoints.iterator(); iterator.hasNext();) {
				ConnectionBendpoint point = (ConnectionBendpoint) iterator.next();
				connection.removeBendpoint(point);
			}
		}

		public void setConnection(Object model) {
			connection = (AbstractUMLConnectionModel) model;
			oldSource = connection.getSource();
		}

		public void setSource(Object model) {
			source = (AbstractUMLEntityModel) model;
		}

		@Override
		public boolean canExecute() {
			// �\�[�X���^�[�Q�b�g��null�̏ꍇ�͎��s�s��
			if (connection.getTarget() == null || source == null) {
				return false;
			}
			// �\�[�X�ƃ^�[�Q�b�g������̏ꍇ�����s�s��
			if (connection.getTarget().equals(source)) {
				return false;
			}
			return true;
		}

		@Override
		public void undo() {
			connection.detachSource();
			connection.setSource(oldSource);
			connection.attachSource();
			for (int i = 0; i < oldBendpoints.size(); i++) {
				connection.addBendpoint(i, (ConnectionBendpoint) oldBendpoints.get(i));
			}
		}
	}

	/** ���C�A�E�g�̃G�f�B�b�g�|���V�[ */
	
	private class EntityLayoutEditPolicy extends LayoutEditPolicy {
		@Override
		protected Command getMoveChildrenCommand(Request request) {
			return null;
		}

		@Override
		protected EditPolicy createChildEditPolicy(EditPart child) {
			return new NonResizableEditPolicy();
		}

		@Override
		protected Command getCreateCommand(CreateRequest request) {
			return null;
		}

		@Override
		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}
	}

	@Override
	public void performRequest(Request req) {
		if (getModel() instanceof EntityModel) {
			if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
					|| req.getType().equals(RequestConstants.REQ_OPEN)) {
				performDirectEdit();
				return;
			}
		}
		super.performRequest(req);
	}

	private void performDirectEdit() {
		if (directManager == null) {
			directManager = new EntityDirectEditManager();
		}
		directManager.show();
	}

	/**
	 * DirectEditManager
	 */
	private class EntityDirectEditManager extends DirectEditManager {

		public EntityDirectEditManager() {
			super(AbstractUMLEntityEditPart.this, TextCellEditor.class,
					new EntityCellEditorLocator());
		}

		@Override
		protected void initCellEditor() {
			getCellEditor().setValue(((EntityModel) getModel()).getName());
			Text text = (Text) getCellEditor().getControl();
			text.selectAll();
		}
	}

	/**
	 * CellEditorLocator
	 */
	private class EntityCellEditorLocator implements CellEditorLocator {
		@Override
		public void relocate(CellEditor celleditor) {
			EntityFigure figure = (EntityFigure) getFigure();
			Text text = (Text) celleditor.getControl();
			// Point pref = text.computeSize(-1, -1);

			Rectangle rect = figure.getCellEditorRectangle();
			figure.translateToAbsolute(rect);
			text.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
	}

	/**
	 * DirectEditCommand
	 */
	private class DirectEditCommand extends Command {

		private String oldName;

		private String newName;

		@Override
		public void execute() {
			EntityModel model = (EntityModel) getModel();
			oldName = model.getName();
			model.setName(newName);
		}

		public void setName(String name) {
			newName = name;
		}

		@Override
		public void undo() {
			EntityModel model = (EntityModel) getModel();
			model.setName(oldName);
		}
	}

	/**
	 * DirectEditPolicy
	 */
	private class EntityDirectEditPolicy extends DirectEditPolicy {

		@Override
		protected Command getDirectEditCommand(DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			command.setName((String) request.getCellEditor().getValue());
			return command;
		}

		@Override
		protected void showCurrentEditValue(DirectEditRequest request) {
		}
	}
}
