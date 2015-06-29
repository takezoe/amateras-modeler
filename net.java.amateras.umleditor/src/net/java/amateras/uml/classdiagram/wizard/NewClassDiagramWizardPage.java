package net.java.amateras.uml.classdiagram.wizard;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.uml.DiagramSerializer;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

public class NewClassDiagramWizardPage extends WizardNewFileCreationPage {
	
	public NewClassDiagramWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle(UMLPlugin.getDefault().getResourceString("wizard.newClassDiagram.title"));
		setDescription(UMLPlugin.getDefault().getResourceString("wizard.newClassDiagram.description"));
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("newfile.cld");
	}
	
	protected InputStream getInitialContents() {
		try {
			RootModel root = new RootModel();
			root.setShowIcon(true);
			root.setBackgroundColor(UMLClassFigure.classColor.getRGB());
			root.setForegroundColor(ColorConstants.black.getRGB());
			return DiagramSerializer.serialize(root);
		} catch(UnsupportedEncodingException ex){
			return null;
		}
	}


}
