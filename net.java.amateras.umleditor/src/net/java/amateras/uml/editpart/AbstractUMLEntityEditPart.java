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

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new EntityComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new EntityLayoutEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new EntityDirectEditPolicy());
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
		Object model = getModel();
		if (model instanceof AbstractUMLEntityModel) {
			// 制約の取得
			Rectangle constraint = ((AbstractUMLEntityModel) model)
					.getConstraint();
			// Rectangleオブジェクトを制約としてビューに設定する
			// setLayoutConstraintメソッドは親EditPartから呼び出す
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,
					getFigure(), constraint);
		}
	}

	/** このEditPartを接続元とするコネクション・モデルのリストを返す */
	protected List<AbstractUMLConnectionModel> getModelSourceConnections() {
		return ((AbstractUMLEntityModel) getModel())
				.getModelSourceConnections();
	}

	/** このEditPartを接続先とするコネクション・モデルのリストを返す */
	protected List<AbstractUMLConnectionModel> getModelTargetConnections() {
		return ((AbstractUMLEntityModel) getModel())
				.getModelTargetConnections();
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AbstractUMLEntityModel.P_FORCE_UPDATE)) {
			refreshChildren();
		}
		refreshVisuals();
		refreshSourceConnections();
		refreshTargetConnections();
	}

	/** エンティティのエディットポリシー */
	private class EntityComponentEditPolicy extends ComponentEditPolicy {
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setRootModel(getHost().getParent().getModel());
			command.setTargetModel(getHost().getModel());
			return command;
		}
	}

	/** 削除コマンド */
	public static class DeleteCommand extends Command {

		private AbstractUMLEntityModel container;

		private AbstractUMLModel model;

		// 削除対象のモデルをソースとするコネクションのリスト
		private List<AbstractUMLConnectionModel> sourceConnections = new ArrayList<AbstractUMLConnectionModel>();

		// 削除対象のモデルをターゲットとするコネクションのリスト
		private List<AbstractUMLConnectionModel> targetConnections = new ArrayList<AbstractUMLConnectionModel>();

		public void execute() {
			// モデルを削除する前にそのモデルをソースとするコネクションを記録しておく
			sourceConnections.addAll(((AbstractUMLEntityModel) model)
					.getModelSourceConnections());
			// 削除対象のモデルをターゲットとするコネクションの記録
			targetConnections.addAll(((AbstractUMLEntityModel) model)
					.getModelTargetConnections());
			// 削除対象のモデルをソースとするコネクションの削除
			for (int i = 0; i < sourceConnections.size(); i++) {
				AbstractUMLConnectionModel model = (AbstractUMLConnectionModel) sourceConnections
						.get(i);
				model.detachSource();
				model.detachTarget();
			}
			// 削除対象のモデルをターゲットとするコネクションの削除
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

		public void undo() {
			container.addChild(model);
			// コネクションを元に戻す
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
			// コネクションを復元するために記録していたデータをクリアする
			sourceConnections.clear();
			targetConnections.clear();
		}
	}

	/** コネクションのエディットポリシー */
	private class NodeEditPolicy extends GraphicalNodeEditPolicy {

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
	 * 自己遷移の際に自動的にベンドポイントを追加する.
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

	/** コネクションの作成コマンド */
	private class CreateConnectionCommand extends Command {

		private AbstractUMLEntityModel source;

		private AbstractUMLEntityModel target;

		private AbstractUMLConnectionModel connection;

		public AbstractUMLConnectionModel getConnectionModel() {
			return connection;
		}

		public boolean canExecute() {
			// ソースかターゲットがnullの場合は実行不可
			if (source == null || target == null) {
				return false;
			}
			// // ソースとターゲットが同一の場合も実行不可
			// if (source == target) {
			// return false;
			// }
			return true;
		}

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

		public void undo() {
			// コネクションをソースとターゲットから取り外す
			connection.detachSource();
			connection.detachTarget();
		}
	}

	/** コネクションのターゲットを再接続するコマンド */
	private class ReconnectTargetCommand extends Command {

		private AbstractUMLEntityModel target;

		private AbstractUMLEntityModel oldTarget;

		private AbstractUMLConnectionModel connection;

		private List<ConnectionBendpoint> oldBendpoints;

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

		public boolean canExecute() {
			// ソースかターゲットがnullの場合は実行不可
			if (connection.getSource() == null || target == null) {
				return false;
			}
			// ソースとターゲットが同一の場合も実行不可
			if (connection.getSource().equals(target)) {
				return false;
			}
			return true;
		}

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

	/** コネクションのソースを再接続するコマンド */
	private class ReconnectSourceCommand extends Command {

		private AbstractUMLEntityModel source;

		private AbstractUMLEntityModel oldSource;

		private AbstractUMLConnectionModel connection;

		private List<ConnectionBendpoint> oldBendpoints;

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

		public boolean canExecute() {
			// ソースかターゲットがnullの場合は実行不可
			if (connection.getTarget() == null || source == null) {
				return false;
			}
			// ソースとターゲットが同一の場合も実行不可
			if (connection.getTarget().equals(source)) {
				return false;
			}
			return true;
		}

		public void undo() {
			connection.detachSource();
			connection.setSource(oldSource);
			connection.attachSource();
			for (int i = 0; i < oldBendpoints.size(); i++) {
				connection.addBendpoint(i, (ConnectionBendpoint) oldBendpoints.get(i));
			}
		}
	}

	/** レイアウトのエディットポリシー */
	private class EntityLayoutEditPolicy extends LayoutEditPolicy {
		protected Command getMoveChildrenCommand(Request request) {
			return null;
		}

		protected EditPolicy createChildEditPolicy(EditPart child) {
			return new NonResizableEditPolicy();
		}

		protected Command getCreateCommand(CreateRequest request) {
			return null;
		}

		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}
	}

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

		public void execute() {
			EntityModel model = (EntityModel) getModel();
			oldName = model.getName();
			model.setName(newName);
		}

		public void setName(String name) {
			newName = name;
		}

		public void undo() {
			EntityModel model = (EntityModel) getModel();
			model.setName(oldName);
		}
	}

	/**
	 * DirectEditPolicy
	 */
	private class EntityDirectEditPolicy extends DirectEditPolicy {

		protected Command getDirectEditCommand(DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			command.setName((String) request.getCellEditor().getValue());
			return command;
		}

		protected void showCurrentEditValue(DirectEditRequest request) {
		}
	}
}
