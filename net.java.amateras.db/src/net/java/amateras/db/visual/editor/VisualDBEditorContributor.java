package net.java.amateras.db.visual.editor;

import org.eclipse.gef.ui.actions.ActionBarContributor;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInRetargetAction;
import org.eclipse.gef.ui.actions.ZoomOutRetargetAction;
import org.eclipse.jface.action.IToolBarManager;

public class VisualDBEditorContributor extends ActionBarContributor {

	public VisualDBEditorContributor() {
		super();
	}
	
	protected void buildActions() {
	    addRetargetAction(new UndoRetargetAction());
	    addRetargetAction(new RedoRetargetAction());
	    addRetargetAction(new DeleteRetargetAction());
	    addRetargetAction(new ZoomInRetargetAction());
	    addRetargetAction(new ZoomOutRetargetAction());
	}

	public void contributeToToolBar(IToolBarManager toolBarManager) {
		super.contributeToToolBar(toolBarManager);
//	    toolBarManager.add(getAction(ActionFactory.DELETE.getId()));
//	    toolBarManager.add(getAction(ActionFactory.UNDO.getId()));
//	    toolBarManager.add(getAction(ActionFactory.REDO.getId()));
//	    toolBarManager.add(new Separator());
	    toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
	    toolBarManager.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
//	    toolBarManager.add(new ZoomComboContributionItem(getPage()));
	}
	
	protected void declareGlobalActionKeys() {
	}
}

