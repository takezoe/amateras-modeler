package net.java.amateras.uml.classdiagram.action;

import java.util.List;

import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.classdiagram.editpart.AttributeEditPart;
import net.java.amateras.uml.classdiagram.editpart.ClassEditPart;
import net.java.amateras.uml.classdiagram.editpart.InterfaceEditPart;
import net.java.amateras.uml.classdiagram.editpart.OperationEditPart;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The base class for attribute / operation actions.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractAttributeAndOperationAction extends AbstractUMLEditorAction {
	
	protected CommandStack stack;
	protected AbstractUMLEditPart targetPart;
	
	public AbstractAttributeAndOperationAction(String name, CommandStack stack,  GraphicalViewer viewer){
		super(name, viewer);
		this.stack = stack;
	}
	
	/**
	 * If the selection contains attribute or method, this action would be enabled.
	 * 
	 * @param sel the selection
	 */
	public void update(IStructuredSelection sel) {
		Object obj = sel.getFirstElement();
		if(obj!=null && obj instanceof OperationEditPart){
			setEnabled(true);
			targetPart = (OperationEditPart)obj;
		} else if(obj!=null && obj instanceof AttributeEditPart){
			setEnabled(true);
			targetPart = (AttributeEditPart)obj;
		} else {
			setEnabled(false);
			targetPart = null;
		}
	}
	
	protected static int getFigureCount(AbstractUMLEditPart targetPart){
		AbstractUMLModel targetModel = (AbstractUMLModel)targetPart.getModel();
		AbstractUMLEntityModel parent = targetModel.getParent();
		List<AbstractUMLModel> children = parent.getChildren();
		int count = 0;
		for(int i=0;i<children.size();i++){
			Object obj = children.get(i);
			if(targetModel instanceof OperationModel){
				if(obj instanceof OperationModel){
					count++;
				}
			} else if(targetModel instanceof AttributeModel){
				if(obj instanceof AttributeModel){
					count++;
				}
			}
		}
		return count;
	}
	
	protected static int getFigureIndex(AbstractUMLEditPart targetPart){
		AbstractUMLModel targetModel = (AbstractUMLModel)targetPart.getModel();
		AbstractUMLEntityModel parent = targetModel.getParent();
		List<AbstractUMLModel> children = parent.getChildren();
		int count = 0;
		for(int i=0;i<children.size();i++){
			Object obj = children.get(i);
			if(obj == targetModel){
				break;
			}
			if(targetModel instanceof OperationModel){
				if(obj instanceof OperationModel){
					count++;
				}
			} else if(targetModel instanceof AttributeModel){
				if(obj instanceof AttributeModel){
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * Returns the <code>UMLClassFigure</code> instance.
	 * 
	 * @param targetPart the editpart of attribute or operation
	 * @return the <code>UMLClassFigure</code> instance
	 */
	protected static UMLClassFigure getFigure(AbstractUMLEditPart targetPart){
		EditPart parent = targetPart.getParent();
		if(parent instanceof ClassEditPart){
			return (UMLClassFigure)((ClassEditPart)parent).getFigure();
		} else if(parent instanceof InterfaceEditPart){
			return (UMLClassFigure)((InterfaceEditPart)parent).getFigure();
		}
		return null;
	}



}
