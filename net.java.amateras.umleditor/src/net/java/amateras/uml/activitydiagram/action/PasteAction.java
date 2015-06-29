package net.java.amateras.uml.activitydiagram.action;

import net.java.amateras.uml.action.AbstractPasteAction;
import net.java.amateras.uml.activitydiagram.ActivityDiagramEditor;
import net.java.amateras.uml.activitydiagram.model.ActionModel;
import net.java.amateras.uml.activitydiagram.model.DecisionModel;
import net.java.amateras.uml.activitydiagram.model.FinalStateModel;
import net.java.amateras.uml.activitydiagram.model.ForkNodeModel;
import net.java.amateras.uml.activitydiagram.model.InitialStateModel;
import net.java.amateras.uml.activitydiagram.model.JoinNodeModel;
import net.java.amateras.uml.activitydiagram.model.ObjectModel;

/**
 * Paste entities in the activity diagram.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class PasteAction extends AbstractPasteAction {
	
	public PasteAction(ActivityDiagramEditor editor) {
		super(editor);
		registerAllowType(ActionModel.class);
		registerAllowType(DecisionModel.class);
		registerAllowType(FinalStateModel.class);
		registerAllowType(ForkNodeModel.class);
		registerAllowType(InitialStateModel.class);
		registerAllowType(JoinNodeModel.class);
		registerAllowType(ObjectModel.class);
	}

}
