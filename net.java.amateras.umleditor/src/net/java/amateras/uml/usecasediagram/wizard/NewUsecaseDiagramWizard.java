package net.java.amateras.uml.usecasediagram.wizard;

import net.java.amateras.uml.UMLPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * ごめんなさい、コピペです.
 * @see net.java.amateras.uml.classdiagram.wizard.NewClassDiagramWizard
 * @author Takahiro Shida.
 *
 */
public class NewUsecaseDiagramWizard extends Wizard implements INewWizard {
	
	private NewUsecaseDiagramWizardPage page;
	private ISelection selection;
	
	public NewUsecaseDiagramWizard() {
		super();
	}
	
	public void addPages() {
		page = new NewUsecaseDiagramWizardPage(selection);
		addPage(page);
	}

	public boolean performFinish() {
		IFile file = page.createNewFile();
		if(file==null){
			return false;
		}
		try {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			IDE.openEditor(page, file, true);
		} catch(PartInitException ex){
			UMLPlugin.logException(ex);
			return false;
		}
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}
