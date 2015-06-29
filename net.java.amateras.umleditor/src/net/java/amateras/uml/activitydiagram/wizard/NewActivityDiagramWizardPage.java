package net.java.amateras.uml.activitydiagram.wizard;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.uml.DiagramSerializer;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.activitydiagram.model.ActivityModel;
import net.java.amateras.uml.sequencediagram.figure.InstanceFigure;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * @see net.java.amateras.uml.classdiagram.wizard.NewActivityDiagramWizard
 * @author Naoki Takezoe
 */
public class NewActivityDiagramWizardPage extends WizardNewFileCreationPage {
	
	public NewActivityDiagramWizardPage(ISelection selection) {
		super("wizardPage",(IStructuredSelection)selection);
		setTitle(UMLPlugin.getDefault().getResourceString("wizard.newActivityDiagram.title"));
		setDescription(UMLPlugin.getDefault().getResourceString("wizard.newActivityDiagram.description"));
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		this.setFileName("newfile.acd");
	}
	
	protected InputStream getInitialContents() {
		try {
			ActivityModel root = new ActivityModel();
			root.setShowIcon(true);
			root.setBackgroundColor(InstanceFigure.INSTANCE_COLOR.getRGB());
			root.setForegroundColor(ColorConstants.black.getRGB());
			return DiagramSerializer.serialize(root);
		} catch(UnsupportedEncodingException ex){
			return null;
		}
	}


}
