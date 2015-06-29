package net.java.amateras.db.wizard;

import net.java.amateras.db.DBPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;

public class NewDiagramWizard extends Wizard implements INewWizard {
	
	private NewDiagramWizardPage1 page1;
	private NewDiagramWizardPage2 page2;
	private IWorkbench workbench;
	private ISelection selection;
	
	public NewDiagramWizard() {
		super();
		setNeedsProgressMonitor(true);
		setWindowTitle(DBPlugin.getResourceString("wizard.new.erd.title"));
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
		this.workbench = workbench;
	}

	public void addPages() {
		page2 = new NewDiagramWizardPage2();
		page1 = new NewDiagramWizardPage1((IStructuredSelection)selection, page2);
		addPage(page1);
		addPage(page2);
	}

	public boolean performFinish() {
		try {
			IFile file = this.page1.createNewFile();
			if (file == null){
				return false;
			}
			try {
				IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(page, file, true);
			} catch(PartInitException ex){
				DBPlugin.logException(ex);
				return false;
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		
		return true;
	}
	
}
