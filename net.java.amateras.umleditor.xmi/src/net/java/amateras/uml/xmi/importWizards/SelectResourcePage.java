/**
 * 
 */
package net.java.amateras.uml.xmi.importWizards;

import net.java.amateras.uml.xmi.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author shida
 * 
 */
public class SelectResourcePage extends WizardPage {

	private TreeViewer viewer;

	protected SelectResourcePage(String pageName) {
		super(pageName);
		setDescription(Activator.getDefault().getResourceString(
				"wizard.resource.description"));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite root = new Composite(parent,SWT.NONE);
		root.setLayout(new FillLayout());
		viewer = new TreeViewer(root);
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());
		setControl(root);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
				if (selection.getFirstElement() instanceof IFile) {
					IFile file = (IFile) selection.getFirstElement();
					SelectModelPage page = (SelectModelPage) getNextPage();
					page.setFile(file);
					setPageComplete(true);
				}
			}

		});
	}
}
