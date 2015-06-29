package net.java.amateras.uml.java;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.TypedViewerFilter;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class JavaExportWizardPage extends WizardPage {
	
	private IJavaProject project;
	private String[] classNames;
	private Table list;
	private Text srcdir;
	
	private static Set<String> unchecked = new HashSet<String>();
	
	public JavaExportWizardPage(IJavaProject project, String[] classNames) {
		super("Output Folder");
		setTitle(UMLJavaPlugin.getDefault().getResourceString("generate.dialog.title"));
		this.project = project;
		this.classNames = classNames;
	}
	
	public String getOutputFolder(){
		return srcdir.getText();
	}
	
	public String[] getGenerateClasses(){
		ArrayList<String> selection = new ArrayList<String>();
		TableItem[] items = list.getItems();
		for(int i=0;i<items.length;i++){
			if(items[i].getChecked()){
				selection.add(items[i].getText());
			} else {
				unchecked.add(items[i].getText());
			}
		}
		return selection.toArray(new String[selection.size()]);
	}
	
	public void createControl(Composite parent) {
		UMLJavaPlugin plugin = UMLJavaPlugin.getDefault();
		//getShell().setText(plugin.getResourceString("generate.dialog.title"));
		
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label label = new Label(composite, SWT.NULL);
		label.setText(plugin.getResourceString("generate.dialog.srcdir"));
		srcdir = new Text(composite, SWT.BORDER);
		srcdir.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Button button = new Button(composite, SWT.PUSH);
		button.setText("...");
		button.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e){
				selectFolder();
			}
		});
		
		label = new Label(composite, SWT.NULL);
		label.setText(plugin.getResourceString("generate.dialog.types"));
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 3;
		label.setLayoutData(gd);
		
		list = new Table(composite, SWT.V_SCROLL|SWT.BORDER|SWT.CHECK);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		list.setLayoutData(gd);
		
		for(int i=0;i<classNames.length;i++){
			TableItem item = new TableItem(list, SWT.NULL);
			item.setText(classNames[i]);
			if(!unchecked.contains(classNames[i])){
				item.setChecked(true);
			}
		}
		
		try {
			IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
			for(int i=0;i<roots.length;i++){
				IResource classpath = roots[i].getResource();
				if(classpath!=null && (classpath instanceof IFolder || classpath instanceof IProject)){
					srcdir.setText(classpath.getProjectRelativePath().toString());
					break;
				}
			}
		} catch(Exception ex){
			// ignore
		}
		
		setControl(composite);
	}
	
	private void selectFolder() {
		try {
			IProject currProject = project.getProject();
			IResource init = null;
			if(!srcdir.getText().equals("")){
				init = currProject.findMember(srcdir.getText());
			}
			Class<?>[] acceptedClasses = new Class<?>[] { IProject.class, IFolder.class };
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);
			
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] allProjects = wsroot.getProjects();
			
			ArrayList<IProject> rejectedElements = new ArrayList<IProject>(allProjects.length);
			for (int i = 0; i < allProjects.length; i++) {
				if (!allProjects[i].equals(currProject)) {
					rejectedElements.add(allProjects[i]);
				}
			}
			ViewerFilter filter = new TypedViewerFilter(acceptedClasses, rejectedElements.toArray());
			
			FolderSelectionDialog dialog = new FolderSelectionDialog(
					getShell(),
					new WorkbenchLabelProvider(), 
					new WorkbenchContentProvider());
			
			dialog.setTitle("Select output folder");
			dialog.setMessage("Select output folder:");
			
			dialog.setInput(wsroot);
			dialog.setValidator(validator);
			dialog.addFilter(filter);
			dialog.setInitialSelection(init);
			if (dialog.open() == Window.OK) {
				srcdir.setText(getFolderName(dialog.getFirstResult()));
			}
			
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	private String getFolderName(Object result) throws CoreException {
		if (result instanceof IFolder) {
			IFolder folder = (IFolder) result;
			return folder.getProjectRelativePath().toString();
		}
		return "";
	}

}
