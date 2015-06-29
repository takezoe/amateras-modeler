package net.java.amateras.uml.usecasediagram.action;

import net.java.amateras.uml.action.AbstractPasteAction;
import net.java.amateras.uml.usecasediagram.UsecaseDiagramEditor;
import net.java.amateras.uml.usecasediagram.model.SystemModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseActorModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

/**
 * Paste clipped entities in the usecase diagram.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class PasteAction extends AbstractPasteAction {

	public PasteAction(UsecaseDiagramEditor editor) {
		super(editor);
		registerAllowType(SystemModel.class);
		registerAllowType(UsecaseActorModel.class);
		registerAllowType(UsecaseModel.class);
	}

}
