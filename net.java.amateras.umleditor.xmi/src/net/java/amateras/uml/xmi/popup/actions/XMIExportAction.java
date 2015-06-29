/**
 * 
 */
package net.java.amateras.uml.xmi.popup.actions;

import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.model.RootModel;
import net.java.amateras.uml.xmi.importWizards.XMIExportWizard;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;

/**
 * @author shida
 *
 */
public class XMIExportAction implements IEditorActionDelegate {

	private ClassDiagramEditor editor;

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IEditorActionDelegate#setActiveEditor(org.eclipse.jface.action.IAction, org.eclipse.ui.IEditorPart)
	 */
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = (ClassDiagramEditor)targetEditor;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
	 */
	public void run(IAction action) {
		RootModel root = (RootModel)this.editor.getAdapter(RootModel.class);
		XMIExportWizard wizard = new XMIExportWizard();
		wizard.setModel(root);
		WizardDialog dialog = new WizardDialog(null, wizard);
		dialog.open();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
