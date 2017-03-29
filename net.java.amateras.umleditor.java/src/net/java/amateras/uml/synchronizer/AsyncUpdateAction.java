package net.java.amateras.uml.synchronizer;

import java.util.List;

import net.java.amateras.uml.action.AsyncSyncAction;
import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

/**
 * Action to do when a java file referred by class diagram is modified
 */
public class AsyncUpdateAction implements AsyncSyncAction {

	private final List<AbstractUMLEntityModel> targetClasses;
	private final ClassDiagramEditor classDiagEditor;
	
	public AsyncUpdateAction(List<AbstractUMLEntityModel> targetClasses, ClassDiagramEditor classDiagEditor) {
		this.targetClasses = targetClasses;
		this.classDiagEditor = classDiagEditor;
	}
	
	@Override
	public void doSyncAction() {
		//Synchronize class diagram
		SyncAction syncAction = new SyncAction();
		syncAction.setTargetList(targetClasses, classDiagEditor);
		syncAction.run(null);
	}

}
