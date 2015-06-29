package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.editor.VisualDBInformationControl;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

public class QuickOutlineAction extends Action {

	public QuickOutlineAction() {
		super(DBPlugin.getResourceString("action.quickOutline"));
		setAccelerator(SWT.CTRL | 'O');
		setId(QuickOutlineAction.class.getName());
		setActionDefinitionId(QuickOutlineAction.class.getName());
	}

	@Override
	public void run(){
		GraphicalViewer viewer = (GraphicalViewer)
			UIUtils.getActiveEditor().getAdapter(GraphicalViewer.class);

		VisualDBInformationControl quickOutline = new VisualDBInformationControl(
				viewer.getControl().getShell(), viewer);

		quickOutline.setVisible(true);
	}

}
