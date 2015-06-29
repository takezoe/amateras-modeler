package net.java.amateras.uml.java;

import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class JavaExportAction implements IEditorActionDelegate {
	
	private ClassDiagramEditor editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = (ClassDiagramEditor)targetEditor;
	}

	public void run(IAction action) {
		RootModel root = (RootModel)this.editor.getAdapter(RootModel.class);
		
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
		IJavaProject project = JavaCore.create(input.getFile().getProject());
		
		JavaExportWizard wizard = new JavaExportWizard(project, root);
		WizardDialog dialog = new WizardDialog(null,wizard);
		dialog.open();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
