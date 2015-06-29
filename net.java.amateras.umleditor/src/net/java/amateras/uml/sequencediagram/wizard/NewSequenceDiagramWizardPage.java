package net.java.amateras.uml.sequencediagram.wizard;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.uml.DiagramSerializer;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.sequencediagram.figure.InstanceFigure;
import net.java.amateras.uml.sequencediagram.model.InteractionModel;

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
public class NewSequenceDiagramWizardPage extends WizardNewFileCreationPage {
	
	public NewSequenceDiagramWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle(UMLPlugin.getDefault().getResourceString("wizard.newSequenceDiagram.title"));
		setDescription(UMLPlugin.getDefault().getResourceString("wizard.newSequenceDiagram.description"));
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("newfile.sqd");
	}
	
	protected InputStream getInitialContents() {
		try {
			InteractionModel root = new InteractionModel();
			root.setShowIcon(true);
			root.setBackgroundColor(InstanceFigure.INSTANCE_COLOR.getRGB());
			root.setForegroundColor(ColorConstants.black.getRGB());
			return DiagramSerializer.serialize(root);
		} catch(UnsupportedEncodingException ex){
			return null;
		}
	}


}
