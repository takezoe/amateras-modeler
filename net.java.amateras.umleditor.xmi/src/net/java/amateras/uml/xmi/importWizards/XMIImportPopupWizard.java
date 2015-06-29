/**
 * 
 */
package net.java.amateras.uml.xmi.importWizards;

import java.util.Iterator;

import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;
import net.java.amateras.uml.xmi.Activator;
import net.java.amateras.uml.xmi.XMIImporter;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.uml2.uml.NamedElement;

/**
 * @author shida
 * 
 */
public class XMIImportPopupWizard extends Wizard {

	private RootModel rootModel;

	private SelectResourcePage resourcePage;

	private SelectModelPage modelPage;

	private CommandStack stack;

	public XMIImportPopupWizard(RootModel rootModel, CommandStack stack) {
		super();
		this.stack = stack;
		setWindowTitle(Activator.getDefault().getResourceString(
				"wizard.window.title"));
		this.rootModel = rootModel;
		resourcePage = new SelectResourcePage(Activator.getDefault()
				.getResourceString("wizard.resource.pagename"));
		modelPage = new SelectModelPage(Activator.getDefault()
				.getResourceString("wizard.model.pagename"));
	}

	public void addPages() {
		super.addPages();
		addPage(resourcePage);
		addPage(modelPage);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#performFinish()
	 */
	public boolean performFinish() {
		if (modelPage.getSelectedModel() == null) {
			return false;
		}
		final XMIImporter convertor = new XMIImporter();
		final Object[] objects = modelPage.getSelectedModel();
		Job job = new Job(Activator.getDefault().getResourceString(
				"import.job.name")) {

			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Activator.getDefault().getResourceString(
						"import.job.task.name"), objects.length * 2 + 1);
				for (int i = 0; i < objects.length; i++) {
					Object object = objects[i];
					if (object instanceof NamedElement) {
						NamedElement elem = (NamedElement) object;
						monitor.subTask(Activator.getDefault()
								.getResourceString(
										"import.job.task.convert.node")
								+ elem.getName());
						convertor.convertNodes(elem);
						monitor.worked(1);
					}
				}
				for (int i = 0; i < objects.length; i++) {
					Object object = objects[i];
					if (object instanceof NamedElement) {
						NamedElement elem = (NamedElement) object;
						monitor.subTask(Activator.getDefault()
								.getResourceString(
										"import.job.task.convert.link")
								+ elem.getName());
						convertor.convertLinks(elem);
						monitor.worked(1);
					}
				}
				monitor.subTask(Activator.getDefault().getResourceString(
						"import.job.task.convert.end"));
				Display.getDefault().syncExec(new Runnable() {

					public void run() {
						for (Iterator iterator = convertor.getConvertedModel()
								.iterator(); iterator.hasNext();) {
							AbstractUMLModel model = (AbstractUMLModel) iterator
									.next();
							ImportCommand command = new ImportCommand();
							command.setModel(model);
							XMIImportPopupWizard.this.stack.execute(command);
						}
					}

				});
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.schedule();
		return true;
	}

	private class ImportCommand extends Command {

		private AbstractUMLModel model;

		public void setModel(AbstractUMLModel model) {
			this.model = model;
		}

		public void execute() {
			XMIImportPopupWizard.this.rootModel.copyPresentation(model);
			XMIImportPopupWizard.this.rootModel.addChild(model);
		}

		public void undo() {
			XMIImportPopupWizard.this.rootModel.removeChild(model);
		}
	}
	//
	// public boolean canFinish() {
	// return loadPage.isPageComplete();
	// }
}
