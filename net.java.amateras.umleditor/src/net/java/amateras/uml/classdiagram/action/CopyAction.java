package net.java.amateras.uml.classdiagram.action;

import net.java.amateras.uml.action.AbstractCopyAction;
import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;

/**
 * Copy selected entities in the class diagram editor.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class CopyAction extends AbstractCopyAction {
	
	public CopyAction(ClassDiagramEditor editor, PasteAction pasteAction) {
		super(editor, pasteAction);
		registerAllowType(ClassModel.class);
		registerAllowType(InterfaceModel.class);
	}

}
