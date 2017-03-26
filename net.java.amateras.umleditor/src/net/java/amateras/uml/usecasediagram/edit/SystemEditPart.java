/**
 * 
 */
package net.java.amateras.uml.usecasediagram.edit;

import java.beans.PropertyChangeEvent;
import java.util.List;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

import net.java.amateras.uml.editpart.NamedEntityEditPart;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.usecasediagram.figure.SystemFigure;
import net.java.amateras.uml.usecasediagram.figure.UsecaseFigureFactory;
import net.java.amateras.uml.usecasediagram.model.SystemModel;

/**
 * @author shida
 *
 */
public class SystemEditPart extends NamedEntityEditPart {

	@Override
	protected void createEditPolicies() {
		super.createEditPolicies();
		removeEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE);
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new SystemLayoutEditPolicy());
	}
	
	@Override
	public IFigure getContentPane() {
		SystemFigure figure = (SystemFigure) getFigure();
		return figure.getPanel();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(SystemModel.P_CHILDREN)) {
			refreshChildren();
		}
		
	}
	
	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
	}
	
	@Override
	protected EntityFigure createEntityFigure() {
		return UsecaseFigureFactory.getSystemFigure();
	}
	
	@Override
	protected List<AbstractUMLModel> getModelChildren() {
		SystemModel model = (SystemModel) getModel();
		return model.getChildren();
	}
	
	class SystemLayoutEditPolicy extends XYLayoutEditPolicy {

		@Override
		protected Command createAddCommand(EditPart child, Object constraint) {
			CreateAddCommand command = new CreateAddCommand();
			command.setModel((AbstractUMLEntityModel) child.getModel());
			command.setTarget((AbstractUMLEntityModel) getHost().getModel());
			
			ChangeConstraintCommand nextCommand = new ChangeConstraintCommand();
			nextCommand.setModel((AbstractUMLEntityModel)child.getModel());
			nextCommand.setConstraint((Rectangle)constraint);
			
			return command.chain(nextCommand);
		}
		
		@Override
		protected Command createChangeConstraintCommand(EditPart child,Object constraint) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			command.setModel((AbstractUMLEntityModel)child.getModel());
			command.setConstraint((Rectangle)constraint);
			return command;
		}
		
		@Override
		protected Command getCreateCommand(CreateRequest request) {
			CreateCommand command = new CreateCommand();
			Rectangle constraint = (Rectangle) getConstraintFor(request);
			AbstractUMLEntityModel model = (AbstractUMLEntityModel) request.getNewObject();
			model.setConstraint(constraint);
			
			command.setRootModel(getHost().getModel());
			command.setModel(model);
			return command;
		}
		
		@Override
		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}
	}
	
	/** 制約の変更コマンド */
	private class ChangeConstraintCommand extends Command {
		
		private AbstractUMLEntityModel model;
		private Rectangle constraint;
		private Rectangle oldConstraint;
		
		@Override
		public void execute(){
			model.setConstraint(constraint);
		}
		
		public void setConstraint(Rectangle constraint){
			this.constraint = constraint;
		}
		
		public void setModel(AbstractUMLEntityModel model){
			this.model = model;
			oldConstraint = model.getConstraint();
		}
		
		@Override
		public void undo() {
			model.setConstraint(oldConstraint);
		}
	}
	
	/** モデルの新規作成コマンド */
	private class CreateCommand extends Command {
		
		private AbstractUMLEntityModel root;
		private AbstractUMLEntityModel model;
		
		@Override
		public void execute() {
			root.copyPresentation(model);
			root.addChild(model);
		}
		
		public void setRootModel(Object root) {
			this.root = (AbstractUMLEntityModel)root;
		}
		
		public void setModel(Object model) {
			this.model = (AbstractUMLEntityModel) model;
		}
		
		@Override
		public void undo() {
			root.removeChild(model);
		}
	}

	private class CreateAddCommand extends Command {
		private AbstractUMLEntityModel target;
		private AbstractUMLEntityModel model;
		private AbstractUMLEntityModel container;
		
		@Override
		public void execute() {
			this.container = model.getParent();
			container.removeChild(model);
			target.addChild(model);
		}
		
		@Override
		public void undo() {
			target.removeChild(model);
			container.addChild(model);
		}
		public void setTarget(AbstractUMLEntityModel target) {
			this.target = target;
		}
		
		public void setModel(AbstractUMLEntityModel model) {
			this.model = model;
		}
	}

}
