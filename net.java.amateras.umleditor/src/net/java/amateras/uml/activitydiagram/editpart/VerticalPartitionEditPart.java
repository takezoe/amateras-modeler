package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.activitydiagram.figure.ActivityFigureFactory;
import net.java.amateras.uml.activitydiagram.figure.VerticalPartitionFigure;
import net.java.amateras.uml.activitydiagram.model.VerticalPartitionModel;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;

/**
 * 
 * @author Naoki Takezoe
 */
public class VerticalPartitionEditPart extends AbstractUMLEntityEditPart {

	protected IFigure createFigure() {
		VerticalPartitionFigure figure = ActivityFigureFactory.getPartitionFigure();
		figure.updatePresentation((VerticalPartitionModel)getModel());
		return figure;
	}
	
	protected void refreshVisuals() {
		super.refreshVisuals();
		VerticalPartitionFigure figure = (VerticalPartitionFigure)getFigure();
		figure.updatePresentation((VerticalPartitionModel)getModel());
	}
	
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new RootEditPolicy());
		removeEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE);
	}	
	
	/** エディットポリシー */
	private class RootEditPolicy extends XYLayoutEditPolicy {
		
		public Command getCommand(Request request) {
			return getHost().getParent().getCommand(request);
		}
		protected Command createChangeConstraintCommand(EditPart child,Object constraint) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			command.setModel((AbstractUMLEntityModel)child.getModel());
			command.setConstraint((Rectangle)constraint);
			return command;
		}
		
		protected Command getCreateCommand(CreateRequest request) {
			return getHost().getParent().getCommand(request);
		}
		
		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}
	}
	
	/** 制約の変更コマンド */
	private class ChangeConstraintCommand extends Command {
		
		private AbstractUMLEntityModel model;
		private Rectangle constraint;
		private Rectangle oldConstraint;
		
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
		
		public void undo() {
			model.setConstraint(oldConstraint);
		}
	}

}
