package net.java.amateras.db.visual.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.draw2d.ChopboxAnchor;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.NodeEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.editpolicies.LayoutEditPolicy;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.requests.ReconnectRequest;

public abstract class AbstractDBEntityEditPart extends AbstractDBEditPart implements NodeEditPart {
	
	/**
	 * Creats a {@link CreateConnectionCommand} instance.
	 * Override to return an instance of extended class to customize connection creation.
	 * 
	 * @return the connection creation command
	 */
	protected CreateConnectionCommand newCreateConnectionCommand(){
		return new CreateConnectionCommand();
	}
	
	protected void refreshVisuals() {
		Object model = getModel();
		if(model instanceof AbstractDBEntityModel){
			Rectangle constraint = ((AbstractDBEntityModel)model).getConstraint();
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,getFigure(), constraint);
		}
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,new TableComponentEditPolicy());
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE,new NodeEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new EntityLayoutEditPolicy());
//		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new EntityDirectEditPolicy());
	}

	protected List<AbstractDBConnectionModel> getModelSourceConnections() {
		return ((AbstractDBEntityModel) getModel()).getModelSourceConnections();
	}
	
	protected List<AbstractDBConnectionModel> getModelTargetConnections() {
		return ((AbstractDBEntityModel) getModel()).getModelTargetConnections();
	}
	
	public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connection) {
		return new ChopboxAnchor(getFigure());
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		return new ChopboxAnchor(getFigure());
	}

	@SuppressWarnings("unchecked")
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
		refreshSourceConnections();
		refreshTargetConnections();
		
		invokePropertyChangeListener(evt, getSourceConnections());
		invokePropertyChangeListener(evt, getTargetConnections());
	}
	
	private void invokePropertyChangeListener(PropertyChangeEvent evt, List<PropertyChangeListener> listeners){
		for(PropertyChangeListener listener: listeners){
			listener.propertyChange(evt);
		}
	}
	
	private class TableComponentEditPolicy extends ComponentEditPolicy {
		
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setRootModel(getHost().getParent().getModel());
			command.setTargetModel(getHost().getModel());
			return command;
		}
		
	}
	
	private class DeleteCommand extends Command {
		
		private RootModel root;
		private AbstractDBEntityModel model;
		
		private List<AbstractDBConnectionModel> sourceConnections = new ArrayList<AbstractDBConnectionModel>();
		private List<AbstractDBConnectionModel> targetConnections = new ArrayList<AbstractDBConnectionModel>();
		
		public void execute() {
			sourceConnections.addAll(model.getModelSourceConnections());
			targetConnections.addAll(model.getModelTargetConnections());
			for (int i = 0; i < sourceConnections.size(); i++) {
				AbstractDBConnectionModel model = sourceConnections.get(i);
				model.detachSource();
				model.detachTarget();
			}
			for (int i = 0; i < targetConnections.size(); i++) {
				AbstractDBConnectionModel model = targetConnections.get(i);
				model.detachSource();
				model.detachTarget();
			}
			root.removeChild(model);
		}
		
		public void setRootModel(Object root) {
			this.root = (RootModel) root;
		}
		
		public void setTargetModel(Object model) {
			this.model = (AbstractDBEntityModel) model;
		}
		
		public void undo(){
			root.addChild(model);
			for (int i = 0; i < sourceConnections.size(); i++) {
				AbstractDBConnectionModel model = (AbstractDBConnectionModel) sourceConnections.get(i);
				model.attachSource();
				model.attachTarget();
			}
			for (int i = 0; i < targetConnections.size(); i++) {
				AbstractDBConnectionModel model = (AbstractDBConnectionModel) targetConnections.get(i);
				model.attachSource();
				model.attachTarget();
			}
			sourceConnections.clear();
			targetConnections.clear();
		}
	}
	
	private class NodeEditPolicy extends GraphicalNodeEditPolicy {
		
		protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
			AbstractDBConnectionModel conn = ((CreateConnectionCommand)request.getStartCommand()).getConnectionModel();
			AbstractDBEntityModel model = (AbstractDBEntityModel)getHost().getModel();
			if(!model.canTarget(conn)){
				return null;
			}
			CreateConnectionCommand command = (CreateConnectionCommand) request.getStartCommand();
			command.setTarget(model);
			return command;
		}
		
		protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
			AbstractDBConnectionModel conn = (AbstractDBConnectionModel)request.getNewObject();
			AbstractDBEntityModel model = (AbstractDBEntityModel)getHost().getModel();
			if(!model.canSource(conn)){
				return null;
			}
			CreateConnectionCommand command = newCreateConnectionCommand();
			command.setModel(getModel());
			command.setConnection(conn);
			command.setSource(model);
			request.setStartCommand(command);
			return command;
		}
		
		protected Command getReconnectTargetCommand(ReconnectRequest request) {
			AbstractDBConnectionModel conn = (AbstractDBConnectionModel)request.getConnectionEditPart().getModel();
			AbstractDBEntityModel model = (AbstractDBEntityModel)getHost().getModel();
			if(!model.canTarget(conn)){
				return null;
			}
			ReconnectTargetCommand command = new ReconnectTargetCommand();
			command.setConnection(conn);
			command.setTarget(model);
			return command;
		}
		
		protected Command getReconnectSourceCommand(ReconnectRequest request) {
			AbstractDBConnectionModel conn = (AbstractDBConnectionModel)request.getConnectionEditPart().getModel();
			AbstractDBEntityModel model = (AbstractDBEntityModel)getHost().getModel();
			if(!model.canSource(conn)){
				return null;
			}
			ReconnectSourceCommand command = new ReconnectSourceCommand();
			command.setConnection(conn);
			command.setSource(model);
			return command;
		}
	}
	
	protected static class CreateConnectionCommand extends Command {
		
		protected Object model;
		protected AbstractDBEntityModel source;
		protected AbstractDBEntityModel target;
		protected AbstractDBConnectionModel connection;
		
		public void setModel(Object model){
			this.model = model;
		}
		
		public Object getModel(){
			return this.model;
		}
		
		public AbstractDBConnectionModel getConnectionModel(){
			return connection;
		}
		
		public boolean canExecute() {
			if (source == null || target == null){
				return false;
			}
			if (source == target){
				return false;
			}
			return true;
		}
		
		public void execute() {
			connection.attachSource();
			connection.attachTarget();
		}
		
		public void setConnection(Object model) {
			connection = (AbstractDBConnectionModel) model;
		}
		
		public void setSource(Object model) {
			source = (AbstractDBEntityModel) model;
			connection.setSource(source);
		}
		
		public void setTarget(Object model) {
			target = (AbstractDBEntityModel) model;
			connection.setTarget(target);
		}
		
		public void undo() {
			connection.detachSource();
			connection.detachTarget();
		}
	}
	
	private class ReconnectTargetCommand extends Command {
		
		private AbstractDBEntityModel target;
		private AbstractDBEntityModel oldTarget;
		private AbstractDBConnectionModel connection;
		
		public void execute() {
			connection.detachTarget();
			connection.setTarget(target);
			connection.attachTarget();
		}
		
		public void setConnection(Object model) {
			connection = (AbstractDBConnectionModel) model;
			oldTarget = connection.getTarget();
		}
		
		public void setTarget(Object model) {
			target = (AbstractDBEntityModel) model;
		}
		
		public boolean canExecute() {
			if (connection.getSource() == null || target == null){
				return false;
			}
			if (connection.getSource().equals(target)){
				return false;
			}
			return true;
		}
		
		public void undo() {
			connection.detachTarget();
			connection.setTarget(oldTarget);
			connection.attachTarget();
		}
	}
	
	private class ReconnectSourceCommand extends Command {
		
		private AbstractDBEntityModel source;
		private AbstractDBEntityModel oldSource;
		private AbstractDBConnectionModel connection;
		
		public void execute() {
			connection.detachSource();
			connection.setSource(source);
			connection.attachSource();
		}
		
		public void setConnection(Object model) {
			connection = (AbstractDBConnectionModel) model;
			oldSource = connection.getSource();
		}
		
		public void setSource(Object model) {
			source = (AbstractDBEntityModel) model;
		}
		
		public boolean canExecute() {
			if (connection.getTarget() == null || source == null){
				return false;
			}
			if (connection.getTarget().equals(source)){
				return false;
			}
			return true;
		}
		
		public void undo() {
			connection.detachSource();
			connection.setSource(oldSource);
			connection.attachSource();
		}
	}
	
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
}
