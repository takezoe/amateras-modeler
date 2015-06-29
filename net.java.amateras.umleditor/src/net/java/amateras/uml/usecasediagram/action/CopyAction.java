package net.java.amateras.uml.usecasediagram.action;

import net.java.amateras.uml.action.AbstractCopyAction;
import net.java.amateras.uml.action.AbstractPasteAction;
import net.java.amateras.uml.usecasediagram.UsecaseDiagramEditor;
import net.java.amateras.uml.usecasediagram.model.SystemModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseActorModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

/**
 * Copy selected entities in the usecase diagram.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class CopyAction extends AbstractCopyAction {

	public CopyAction(UsecaseDiagramEditor editor, AbstractPasteAction pasteAction) {
		super(editor, pasteAction);
		registerAllowType(SystemModel.class);
		registerAllowType(UsecaseActorModel.class);
		registerAllowType(UsecaseModel.class);
	}

}
