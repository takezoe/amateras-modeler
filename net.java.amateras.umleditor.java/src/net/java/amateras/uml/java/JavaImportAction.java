package net.java.amateras.uml.java;

import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.SelectionDialog;

/**
 * 
 * @author Naoki Takezoe
 */
public class JavaImportAction implements IEditorActionDelegate {
	
	private ClassDiagramEditor editor;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = (ClassDiagramEditor)targetEditor;
	}

	public void run(IAction action) {
		IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
		IJavaProject project = JavaCore.create(input.getFile().getProject());
		
		Shell shell = editor.getSite().getShell();
		try {
			SelectionDialog dialog = JavaUI.createTypeDialog(
					shell,new ProgressMonitorDialog(shell),
					SearchEngine.createJavaSearchScope(new IJavaElement[]{project}),
					IJavaElementSearchConstants.CONSIDER_CLASSES|IJavaElementSearchConstants.CONSIDER_INTERFACES,
					false);
			
			if(dialog.open()==SelectionDialog.OK){
				Object[] result = dialog.getResult();
				IType type = (IType)result[0];
				RootModel root = (RootModel)this.editor.getAdapter(RootModel.class);
				CommandStack stack = (CommandStack)editor.getAdapter(CommandStack.class);
				stack.execute(new ImportClassModelCommand(root, type));
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
