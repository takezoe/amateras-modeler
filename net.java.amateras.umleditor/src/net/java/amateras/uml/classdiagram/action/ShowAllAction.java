package net.java.amateras.uml.classdiagram.action;

import java.util.HashMap;
import java.util.Map;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.classdiagram.editpart.AttributeEditPart;
import net.java.amateras.uml.classdiagram.editpart.OperationEditPart;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.editpart.RootEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

public class ShowAllAction extends AbstractUMLEditorAction {

	private CommandStack stack;

	private AbstractUMLEntityModel target;
	
	public ShowAllAction(GraphicalViewer viewer) {
		super(UMLPlugin.getDefault().getResourceString("filter.all"), viewer);
		this.stack = viewer.getEditDomain().getCommandStack();
	}

	public void update(IStructuredSelection sel) {
		Object obj = sel.getFirstElement();
		if (obj != null && obj instanceof AbstractUMLEntityEditPart) {
			target = (AbstractUMLEntityModel) ((AbstractUMLEntityEditPart) obj)
					.getModel();
			setEnabled(true);
		} else if (obj != null && obj instanceof OperationEditPart) {
			target = (AbstractUMLEntityModel) ((OperationEditPart) obj)
					.getParent().getModel();
			setEnabled(true);
		} else if (obj != null && obj instanceof AttributeEditPart) {
			target = (AbstractUMLEntityModel) ((AttributeEditPart) obj)
					.getParent().getModel();
			setEnabled(true);
		} else if (obj != null && obj instanceof RootEditPart) {
			target = (RootModel) ((RootEditPart) obj).getModel();
			setEnabled(true);
		} else {
			setEnabled(false);
			target = null;
		}
	}
	
	public void run() {
		this.stack.execute(new ShowAllCommand(target));
	}

	private static class ShowAllCommand extends Command {

		private Map<String, Boolean> oldValue;
		private AbstractUMLEntityModel target;
		
		public ShowAllCommand(AbstractUMLEntityModel target){
			this.target = target;
		}

		public void execute() {
			oldValue = target.getFilterProperty();
			target.setFilterProperty(new HashMap<String, Boolean>());
		}

		public void undo() {
			target.setFilterProperty(oldValue);
		}
	}
}
