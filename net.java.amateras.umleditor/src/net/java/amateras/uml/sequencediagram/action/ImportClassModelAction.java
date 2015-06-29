/**
 * 
 */
package net.java.amateras.uml.sequencediagram.action;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.TypeEntityModel;
import net.java.amateras.uml.sequencediagram.editpart.InstanceEditPart;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * Load RootModel to sequence diagram editor.
 * 
 * @author Takahiro Shida.
 * 
 */
public class ImportClassModelAction extends AbstractUMLEditorAction {

	private CommandStack stack;

	private InstanceModel model;

	public ImportClassModelAction(CommandStack stack, GraphicalViewer viewer) {
		super(
				UMLPlugin.getDefault().getResourceString(
						"menu.importClass"), viewer);
		this.stack = stack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.java.amateras.uml.action.AbstractUMLEditorAction#update(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	public void update(IStructuredSelection sel) {
		Object element = sel.getFirstElement();
		if (element instanceof InstanceEditPart) {
			InstanceEditPart editPart = (InstanceEditPart) element;
			model = (InstanceModel) editPart.getModel();
			setEnabled(true);
		} else {
			setEnabled(false);
		}
	}

	public void run() {
		IFile file = ImportModelUtil.selectClassDiagram();
		if (file == null) {
			return;
		}
		
		final InstanceModel model = this.model;
		final AbstractUMLEntityModel entityModel = ImportModelUtil.selectClassModel(file);
		
		if (entityModel == null) {
			return;
		}
		
		stack.execute(new Command() {
		
			private TypeEntityModel old;
			
			public void execute() {
				old = model.getType();
				model.setType((TypeEntityModel) entityModel);
			}
			
			public void undo() {
				model.setType(old);
			}
		});
		super.run();
	}

}
