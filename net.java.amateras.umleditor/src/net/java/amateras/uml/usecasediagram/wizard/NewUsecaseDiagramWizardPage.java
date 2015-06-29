package net.java.amateras.uml.usecasediagram.wizard;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.uml.DiagramSerializer;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.sequencediagram.figure.InstanceFigure;
import net.java.amateras.uml.usecasediagram.model.UsecaseRootModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * ‚Ù‚ÚƒRƒsƒy‚Å‚·.
 * @see net.java.amateras.uml.classdiagram.wizard.NewClassDiagramWizardPage
 * @author Takahiro Shida.
 *
 */
public class NewUsecaseDiagramWizardPage extends WizardNewFileCreationPage {
	
	public NewUsecaseDiagramWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle(UMLPlugin.getDefault().getResourceString("wizard.newUsecaseDiagram.title"));
		setDescription(UMLPlugin.getDefault().getResourceString("wizard.newUsecaseDiagram.description"));
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("newfile.ucd");
	}
	
	protected InputStream getInitialContents() {
		try {
			UsecaseRootModel root = new UsecaseRootModel();
			root.setShowIcon(true);
			root.setBackgroundColor(InstanceFigure.INSTANCE_COLOR.getRGB());
			root.setForegroundColor(ColorConstants.black.getRGB());
			return DiagramSerializer.serialize(root);
		} catch(UnsupportedEncodingException ex){
			return null;
		}
	}


}
