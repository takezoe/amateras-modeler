package net.java.amateras.uml.classdiagram.action;

import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.editpart.ClassEditPart;
import net.java.amateras.uml.classdiagram.editpart.InterfaceEditPart;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * 
 * @author Naoki Takezoe
 */
public class DownAction extends AbstractAttributeAndOperationAction {

	public DownAction(CommandStack stack,  GraphicalViewer viewer){
		super(UMLPlugin.getDefault().getResourceString("menu.down"), stack, viewer);
	}
	
	public void run(){
		stack.execute(new DownCommand(targetPart));
	}
	
	public void update(IStructuredSelection sel) {
		super.update(sel);
		if(isEnabled()){
			if(getFigureIndex(targetPart) == getFigureCount(targetPart) - 1){
				setEnabled(false);
				targetPart = null;
			}
		}
	}

	private static class DownCommand extends Command {
		
		private int orgIndex;
		private int figureIndex;
		private AbstractUMLEditPart targetPart;
		
		public DownCommand(AbstractUMLEditPart targetPart){
			this.targetPart = targetPart;
		}
		
		private UMLClassFigure getFigure(){
			EditPart parent = targetPart.getParent();
			if(parent instanceof ClassEditPart){
				return (UMLClassFigure)((ClassEditPart)parent).getFigure();
			} else if(parent instanceof InterfaceEditPart){
				return (UMLClassFigure)((InterfaceEditPart)parent).getFigure();
			}
			return null;
		}
		
		public void execute(){
			AbstractUMLModel targetModel = (AbstractUMLModel)targetPart.getModel();
			AbstractUMLEntityModel parent = targetModel.getParent();
			List<AbstractUMLModel> children = parent.getChildren();
			figureIndex = getFigureIndex(targetPart);
			orgIndex = -1;
			
			// swap models
			if(targetModel instanceof OperationModel){
				int refIndex = -1;
				for(int i=0;i<children.size();i++){
					Object child = children.get(i);
					if(child instanceof OperationModel){
						if(child == targetModel){
							orgIndex = i;
						} else if(orgIndex != -1){
							refIndex = i;
							children.remove(targetModel);
							children.add(refIndex, targetModel);
							break;
						}
					}
				}
			} else if(targetModel instanceof AttributeModel){
				int refIndex = -1;
				for(int i=0;i<children.size();i++){
					Object child = children.get(i);
					if(child instanceof AttributeModel){
						if(child == targetModel){
							orgIndex = i;
						} else if(orgIndex != -1){
							refIndex = i;
							children.remove(targetModel);
							children.add(refIndex, targetModel);
							break;
						}
					}
				}
			}
			
			// swap figures
			UMLClassFigure figure = getFigure();
			if(targetModel instanceof OperationModel){
				figure.moveOperation(figureIndex, false);
			} else {
				figure.moveAttribute(figureIndex, false);
			}
		}
		
		public void undo() {
			AbstractUMLModel targetModel = (AbstractUMLModel)targetPart.getModel();
			
			// swap figures
			UMLClassFigure figure = getFigure();
			if(targetModel instanceof OperationModel){
				figure.moveOperation(figureIndex + 1, true);
			} else {
				figure.moveAttribute(figureIndex + 1, true);
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
