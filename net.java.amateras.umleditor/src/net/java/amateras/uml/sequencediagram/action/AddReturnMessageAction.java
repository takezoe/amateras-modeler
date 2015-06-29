/**
 * 
 */
package net.java.amateras.uml.sequencediagram.action;

import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.sequencediagram.editpart.ActivationEditPart;
import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.ReturnMessageModel;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author Takahiro Shida.
 *
 */
public class AddReturnMessageAction extends AbstractUMLEditorAction {

	private CommandStack stack;
	
	private ActivationModel target;
	
	private ActivationModel source;
	
	public AddReturnMessageAction(CommandStack stack, GraphicalViewer viewer) {
		super(UMLPlugin.getDefault().getResourceString("menu.addReturnMessage"), viewer);
		this.stack = stack;
	}

	/* (non-Javadoc)
	 * @see net.java.amateras.uml.action.AbstractUMLEditorAction#update(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void update(IStructuredSelection sel) {
		Object element = sel.getFirstElement();
		if (element instanceof ActivationEditPart) {
			ActivationEditPart editPart = (ActivationEditPart) element;
			target = getSourceActivation((ActivationModel) editPart.getModel());
			source = (ActivationModel) editPart.getModel(); 
		} else {
			source = null;
			target = null;
		}
		setEnabled(source != null && target != null);
	}

	public void run() {
		stack.execute(new CreateReturnMessageCommand(target, source));
	}
	
	private ActivationModel getSourceActivation(ActivationModel model) {
		List connections = model.getModelSourceConnections();
		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			Object element = iter.next();
			if (element instanceof ReturnMessageModel) {
				return null;
			}
		}
		List connection = model.getSyncTargetConnection();
		for (Iterator iter = connection.iterator(); iter.hasNext();) {
			SyncMessageModel messageModel = (SyncMessageModel) iter.next();
			if (!messageModel.isRecursive()) {
				return (ActivationModel) messageModel.getSource();
			}
		}
		return null;
	}
	
	private static class CreateReturnMessageCommand extends Command {
		
		private ReturnMessageModel msg;
		private ActivationModel target;
		private ActivationModel source;
		
		public CreateReturnMessageCommand(ActivationModel target, ActivationModel source) {
			super();
			this.target = target;
			this.source = source;
		}
		
		public void execute() {
			msg = new ReturnMessageModel();
			msg.setSource(source);
			msg.setTarget(target);
			msg.attachSource();
			msg.attachTarget();
		}
		
		public void undo() {
			msg.detachSource();
			msg.detachTarget();
		}
	}
}
