package net.java.amateras.uml.classdiagram.action;

import net.java.amateras.uml.action.AbstractPasteAction;
import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;

/**
 * Paste entities in the class diagram editor.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public class PasteAction extends AbstractPasteAction {
	
	public PasteAction(ClassDiagramEditor editor) {
		super(editor);
		registerAllowType(ClassModel.class);
		registerAllowType(InterfaceModel.class);
		registerAllowType(EnumModel.class);
	}

}
