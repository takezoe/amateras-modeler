/**
 * 
 */
package net.java.amateras.uml.xmi.importWizards;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;
import net.java.amateras.uml.xmi.Activator;
import net.java.amateras.uml.xmi.XMIExporter;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @author shida
 *
 */
public class XMIExportWizard extends Wizard {

	private WizardNewFileCreationPage creationPage;
	
	private RootModel model;
	
	public XMIExportWizard() {
		creationPage = new WizardNewFileCreationPage(Activator.getDefault().getResourceString("export.wizard.name"), new StructuredSelection());
		creationPage.setTitle(Activator.getDefault().getResourceString("export.wizard.name"));
		creationPage.setDescription(Activator.getDefault().getResourceString("export.wizard.description"));
	}
	
	public void setModel(RootModel model) {
		this.model = model;
	}
	
	public void addPages() {
		super.addPages();
		addPage(creationPage);
	}
	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		XMIExporter exporter = new XMIExporter();
		
		List children = model.getChildren();
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			exporter.convertType(element);
		}
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			exporter.convertStructure(element);
		}
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			exporter.convertLink(element);
		}
		
		String name = creationPage.getContainerFullPath().toOSString() + "/" + creationPage.getFileName();
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource resource = resourceSet.createResource(URI.createPlatformResourceURI(name));
		resource.getContents().add(exporter.getRoot());
		Map options = new HashMap();
		options.put(XMIResource.OPTION_ENCODING, "UTF-8");
		try {
			resource.save(options);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		return true;
	}

}
