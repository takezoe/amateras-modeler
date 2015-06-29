package net.java.amateras.uml.classdiagram.action;

import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Naoki Takezoe
 */
public class UpAction extends AbstractAttributeAndOperationAction {

	public UpAction(CommandStack stack,  GraphicalViewer viewer){
		super(UMLPlugin.getDefault().getResourceString("menu.up"), stack, viewer);
	}
	
	public void run(){
		stack.execute(new UpCommand(targetPart));
	}
	
	public void update(IStructuredSelection sel) {
		super.update(sel);
		if(isEnabled()){
			if(getFigureIndex(targetPart) == 0){
				setEnabled(false);
				targetPart = null;
			}
		}
	}
	
	private class UpCommand extends Command {
		
		private int orgIndex;
		private int figureIndex;
		private AbstractUMLEditPart targetPart;
		
		public UpCommand(AbstractUMLEditPart targetPart){
			this.targetPart = targetPart;
		}
		
		public void execute(){
			AbstractUMLModel targetModel = (AbstractUMLModel)targetPart.getModel();
			AbstractUMLEntityModel parent = targetModel.getParent();
			List<AbstractUMLModel> children = parent.getChildren();
			orgIndex = 0;
			figureIndex = getFigureIndex(targetPart);
			
			// swap models
			if(targetModel instanceof OperationModel){
				int refIndex = 0;
				for(int i=0;i<children.size();i++){
					Object child = children.get(i);
					if(child instanceof OperationModel){
						if(child == targetModel){
							children.remove(targetModel);
							children.add(refIndex, targetModel);
							orgIndex = i;
							break;
						} else {
							refIndex = i;
						}
					}
				}
			} else if(targetModel instanceof AttributeModel){
				int refIndex = 0;
				for(int i=0;i<children.size();i++){
					Object child = children.get(i);
					if(child instanceof AttributeModel){
						if(child == targetModel){
							children.remove(targetModel);
							children.add(refIndex, targetModel);
							orgIndex = i;
							break;
						} else {
							refIndex = i;
						}
					}
				}
			}
			
			// swap figures
			UMLClassFigure figure = getFigure(targetPart);
			if(targetModel instanceof OperationModel){
				figure.moveOperation(figureIndex, true);
			} else {
				figure.moveAttribute(figureIndex, true);
			}
		}
		
		public void undo() {
			AbstractUMLModel targetModel = (AbstractUMLModel)targetPart.getModel();
			
			// swap figures
			UMLClassFigure figure = getFigure(targetPart);
			if(targetModel instanceof OperationModel){
				figure.moveOperation(figureIndex - 1, false);
			} else {
				figure.moveAttribute(figureIndex - 1, false);
			}
			
			// swap models
			AbstractUMLEntityModel parent = targetModel.getParent();
			List<AbstractUMLModel> children = parent.getChildren();
			children.remove(targetModel);
			children.add(orgIndex, targetModel);
			
			targetPart.refresh();
		}
		
	}
	
	
}
