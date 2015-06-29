package net.java.amateras.db.visual.generate;

import net.java.amateras.db.DBPlugin;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class FolderSelectWizardPage extends WizardPage {
	
	private Text txtOutputFolder;
	protected IFile erdFile;
	
	public FolderSelectWizardPage(IFile erdFile, String pageName) {
		super(pageName);
		setTitle(pageName);
		this.erdFile = erdFile;
	}
	
	public IResource getOutputFolderResource(){
		String outputDir = txtOutputFolder.getText();
		IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
		return wsroot.findMember(outputDir);
	}
	
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(DBPlugin.getResourceString("wizard.generate.folder"));
		txtOutputFolder = new Text(composite, SWT.BORDER);
		txtOutputFolder.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtOutputFolder.setText(erdFile.getParent().getFullPath().toString());
		txtOutputFolder.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				doValidate();
			}
		});
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText(DBPlugin.getResourceString("button.browse"));
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				selectFolder();
			}
		});
		
		setControl(composite);
	}
	
	protected void doValidate(){
		setErrorMessage(null);
		setPageComplete(true);
		
//		if(txtOutputFolder.getText().length() == 0){
//			setErrorMessage("Choose a folder");
//			setPageComplete(false);
//		}
	}
	
	private void selectFolder() {
		try {
			IResource init = null;
			if(!txtOutputFolder.getText().equals("")){
				init = getOutputFolderResource();
			}
			Class<?>[] acceptedClasses = new Class<?>[] { IProject.class, IFolder.class };
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);
			
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			FolderSelectionDialog dialog = new FolderSelectionDialog(
					getShell(),
					new WorkbenchLabelProvider(), 
					new WorkbenchContentProvider());
			
			ViewerFilter filter = new ViewerFilter(){
				@Override public boolean select(Viewer viewer, Object parentElement,
						Object element) {
					return (element instanceof IContainer);
				}
			};
			
			dialog.setTitle(DBPlugin.getResourceString("wizard.generate.browse.title"));
			dialog.setMessage(DBPlugin.getResourceString("wizard.generate.browse.message"));
			dialog.addFilter(filter);
			dialog.setInput(wsroot);
			dialog.setValidator(validator);
			dialog.setInitialSelection(init);
			if (dialog.open() == Window.OK) {
				txtOutputFolder.setText(getFolderName(dialog.getFirstResult()));
			}
			
		} catch (Exception ex) {
			DBPlugin.logException(ex);
		}
	}
	
	private String getFolderName(Object result) throws CoreException {
		if (result instanceof IContainer) {
			IContainer folder = (IContainer) result;
			return folder.getFullPath().toString();
		}
		return "";
	}

}
