/**
 * 
 */
package net.java.amateras.uml.classdiagram.action;

import java.util.HashMap;
import java.util.Map;

import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.classdiagram.editpart.AttributeEditPart;
import net.java.amateras.uml.classdiagram.editpart.OperationEditPart;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.editpart.RootEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * @author shida
 * 
 */
public class ToggleAction extends AbstractUMLEditorAction {
	public static final String ATTRIBUTE = "attr";

	public static final String OPERATION = "operation";

	private CommandStack stack;

	private AbstractUMLEntityModel target;

	private String type;

	private Visibility visibility;

	public ToggleAction(String name, GraphicalViewer viewer, String type,
			Visibility visibility) {
		super(name, IAction.AS_CHECK_BOX, viewer);
		this.stack = viewer.getEditDomain().getCommandStack();
		this.type = type;
		this.visibility = visibility;
		setChecked(true);
	}

	public void run() {
		this.stack.execute(new TogglePresentCommand(target));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.amateras.uml.action.AbstractUMLEditorAction#update(org.eclipse.jface.viewers.IStructuredSelection)
	 */
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
		setChecked(calcChecked());
	}

	private boolean calcChecked() {
		if (target == null) {
			return false;
		}
		Map<String, Boolean> map = target.getFilterProperty();
		if (map == null) {
			target.setFilterProperty(new HashMap<String, Boolean>());
			return true;
		}
		if (!map.containsKey(type + visibility)) {
			return true;
		}
		Boolean value = (Boolean) map.get(type + visibility);
		return !value.booleanValue();
	}

	private class TogglePresentCommand extends Command {

		private boolean oldValue;
		private AbstractUMLEntityModel target;
		
		public TogglePresentCommand(AbstractUMLEntityModel target){
			this.target = target;
		}

		public void execute() {
			Map<String, Boolean> map = target.getFilterProperty();
			if (!map.containsKey(type + visibility)) {
				oldValue = false;
				map.put(type + visibility, new Boolean(true));
			} else {
				Boolean value = (Boolean) map.get(type + visibility);
				oldValue = value.booleanValue();
				map.put(type + visibility, new Boolean(!value.booleanValue()));
			}
			target.setFilterProperty(map);
		}

		public void undo() {
			Map<String, Boolean> map = target.getFilterProperty();
			map.put(type + visibility, new Boolean(oldValue));
			target.setFilterProperty(map);
		}
	}
}
