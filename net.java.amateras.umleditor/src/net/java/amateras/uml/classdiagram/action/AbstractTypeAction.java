package net.java.amateras.uml.classdiagram.action;

import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.classdiagram.editpart.AttributeEditPart;
import net.java.amateras.uml.classdiagram.editpart.OperationEditPart;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * The base class for class actions.
 * 
 * @author Naoki Takezoe
 */
public abstract class AbstractTypeAction extends AbstractUMLEditorAction {
	
	protected CommandStack stack;
	protected AbstractUMLEntityModel target;
	
	public AbstractTypeAction(String name, CommandStack stack,  GraphicalViewer viewer){
		super(name, viewer);
		this.stack = stack;
	}
	
	/**
	 * If the selection contains attribute, method or type, this action would be enabled.
	 * 
	 * @param sel the selection
	 */
	public void update(IStructuredSelection sel){
		Object obj = sel.getFirstElement();
		if(obj!=null && obj instanceof AbstractUMLEntityEditPart){
			setEnabled(true);
			target = (AbstractUMLEntityModel)((AbstractUMLEntityEditPart)obj).getModel();
		} else if(obj!=null && obj instanceof OperationEditPart){
			setEnabled(true);
			target = (AbstractUMLEntityModel)((OperationEditPart)obj).getParent().getModel();
		} else if(obj!=null && obj instanceof AttributeEditPart){
			setEnabled(true);
			target = (AbstractUMLEntityModel)((AttributeEditPart)obj).getParent().getModel();
		} else {
			setEnabled(false);
			target = null;
		}
	}

}
