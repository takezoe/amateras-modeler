/**
 * 
 */
package net.java.amateras.uml.usecasediagram.edit;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.NamedEntityEditPart;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.usecasediagram.figure.UsecaseFigure;
import net.java.amateras.uml.usecasediagram.figure.UsecaseFigureFactory;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

/**
 * @author shida
 * 
 */
public class UsecaseEditPart extends NamedEntityEditPart {

	@Override
	protected EntityFigure createEntityFigure() {
		return UsecaseFigureFactory.getUsecaseFigure((UsecaseModel) getModel());
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(UsecaseModel.P_RESOURCE)) {
			UsecaseModel model = (UsecaseModel) getModel();
			UsecaseFigure figure = (UsecaseFigure) getFigure();
			figure.setLink(model.getResource() != null
					&& model.getFileResource().exists());
		}
	}

	@Override
	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_OPEN)) {
			if (openEditor()) {
				return;
			}
		}
		super.performRequest(req);
	}

	private boolean openEditor() {
		UsecaseModel model = (UsecaseModel) getModel();
		if (model.getResource() != null && model.getFileResource().exists()) {
			IWorkbenchWindow window = PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow();
			if (window != null) {
				IWorkbenchPage activePage = window.getActivePage();
				try {
					IDE.openEditor(activePage, model.getFileResource());
					return true;
				} catch (PartInitException e) {
					MessageDialog.openError(window.getShell(),
							UMLPlugin.getDefault().getResourceString(
									"open.resource.title"), UMLPlugin
									.getDefault().getResourceString(
											"open.resource.message"));
				}
			}
		}
		return false;
	}
}
