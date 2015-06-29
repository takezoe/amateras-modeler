/**
 * 
 */
package net.java.amateras.uml.xmi.importWizards;

import net.java.amateras.uml.xmi.Activator;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryContentProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.edit.providers.UMLItemProviderAdapterFactory;

/**
 * @author shida
 * 
 */
public class SelectModelPage extends WizardPage {

	private CheckboxTreeViewer viewer;

	private IFile file;

	protected SelectModelPage(String pageName) {
		super(pageName);
		setDescription(Activator.getDefault().getResourceString(
				"wizard.model.description"));
	}

	public void setFile(IFile file) {
		this.file = file;
	}

	public void setVisible(boolean visible) {
		try {
			URI uri = URI.createFileURI(file.getLocation().toOSString());
			ResourceSet resourceSet = new ResourceSetImpl();
			Resource resource = resourceSet.getResource(uri, true);
			viewer.setInput(resource.getContents().get(0));
			viewer.setAutoExpandLevel(CheckboxTreeViewer.ALL_LEVELS);
			viewer.setAllChecked(true);
			setErrorMessage(null);
		} catch (Exception e) {
			e.printStackTrace();
			setErrorMessage("failed to load model");
			viewer.setInput("select the collect xmi file again.");
		}
		super.setVisible(visible);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite root = new Composite(parent, SWT.NONE);
		root.setLayout(new FillLayout());
		viewer = new CheckboxTreeViewer(root);
		// viewer.setAllChecked(true);
		UMLItemProviderAdapterFactory adapterFactory = new UMLItemProviderAdapterFactory();
		viewer.setContentProvider(new AdapterFactoryContentProvider(
				adapterFactory));
		viewer
				.setLabelProvider(new AdapterFactoryLabelProvider(
						adapterFactory));
		viewer.setAutoExpandLevel(CheckboxTreeViewer.ALL_LEVELS);
		viewer.addFilter(new ViewerFilter() {

			public boolean select(Viewer viewer, Object parentElement, Object element) {
				return element instanceof Classifier || element instanceof Association || element instanceof Package;
			}
			
		});
		setControl(root);
	}

	public Object[] getSelectedModel() {
		return viewer.getCheckedElements();
	}
}
