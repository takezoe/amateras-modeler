package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

/**
 * The action to toggle diagram mode (logical / physical).
 *
 * @author Naoki Takezoe
 */
public class ToggleModelAction extends Action {

	private GraphicalViewer viewer;

	public ToggleModelAction(GraphicalViewer viewer) {
		super(DBPlugin.getResourceString("action.toggleMode"));
		setAccelerator(SWT.CTRL | 'D');
		this.viewer = viewer;
	}

	@Override public void run(){
		CommandStack stack = viewer.getEditDomain().getCommandStack();

		stack.execute(new Command("Toggle display mode"){
		    @Override public void execute() {
				RootModel root = (RootModel) viewer.getContents().getModel();
				root.setLogicalMode(!root.getLogicalMode());
			}

		    @Override public void undo() {
				RootModel root = (RootModel) viewer.getContents().getModel();
				root.setLogicalMode(!root.getLogicalMode());
			}
		});
	}

}
