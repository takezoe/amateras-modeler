package net.java.amateras.uml.synchronizer;

import java.util.List;

import org.eclipse.gef.commands.CommandStack;

import net.java.amateras.uml.action.AsyncSyncAction;
import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart.DeleteCommand;
import net.java.amateras.uml.java.UMLJavaUtils;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;

/**
 * Action to do when a java file referred by class diagram is deleted
 */
public class AsyncDeleteAction implements AsyncSyncAction {
	
	private final List<AbstractUMLEntityModel> targetClasses;
	private final ClassDiagramEditor classDiagEditor;
	
	public AsyncDeleteAction(List<AbstractUMLEntityModel> targetClasses, ClassDiagramEditor classDiagEditor) {
		this.targetClasses = targetClasses;
		this.classDiagEditor = classDiagEditor;
	}

	@Override
	public void doSyncAction() {
		//Remove element in class diagram
		for (AbstractUMLEntityModel targetClass : targetClasses) {
			CommonEntityModel modelComEntity = (CommonEntityModel)targetClass;
			String className = modelComEntity.getName();
			className = UMLJavaUtils.stripGenerics(className);
			
			RootModel root = (RootModel) classDiagEditor.getAdapter(RootModel.class);
			CommandStack stack = (CommandStack) classDiagEditor.getAdapter(CommandStack.class);
			
			DeleteCommand deleteCommand = new DeleteCommand();
			deleteCommand.setRootModel(root);
			deleteCommand.setTargetModel(targetClass);
			
			stack.execute(deleteCommand);
		}
	}

}
