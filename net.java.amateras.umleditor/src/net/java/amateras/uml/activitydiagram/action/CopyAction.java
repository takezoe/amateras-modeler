package net.java.amateras.uml.activitydiagram.action;

import net.java.amateras.uml.action.AbstractCopyAction;
import net.java.amateras.uml.activitydiagram.ActivityDiagramEditor;
import net.java.amateras.uml.activitydiagram.model.ActionModel;
import net.java.amateras.uml.activitydiagram.model.DecisionModel;
import net.java.amateras.uml.activitydiagram.model.FinalStateModel;
import net.java.amateras.uml.activitydiagram.model.ForkNodeModel;
import net.java.amateras.uml.activitydiagram.model.InitialStateModel;
import net.java.amateras.uml.activitydiagram.model.JoinNodeModel;
import net.java.amateras.uml.activitydiagram.model.ObjectModel;

/**
 * Copy selected entities in the activity diagram.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class CopyAction extends AbstractCopyAction {

	public CopyAction(ActivityDiagramEditor editor, PasteAction pasteAction) {
		super(editor, pasteAction);
		registerAllowType(ActionModel.class);
		registerAllowType(DecisionModel.class);
		registerAllowType(FinalStateModel.class);
		registerAllowType(ForkNodeModel.class);
		registerAllowType(InitialStateModel.class);
		registerAllowType(JoinNodeModel.class);
		registerAllowType(ObjectModel.class);
	}

}
