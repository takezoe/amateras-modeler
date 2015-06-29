/**
 * 
 */
package net.java.amateras.uml.usecasediagram.property;

import net.java.amateras.uml.UMLPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author shida
 *
 */
public class ResourcePropertyDescriptor extends PropertyDescriptor {

	public ResourcePropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		ResourceSelectionDialogCellEditor editor = new ResourceSelectionDialogCellEditor(parent);
		if (getValidator() != null){
			editor.setValidator(getValidator());
		}
		return editor;
	}

	private class ResourceSelectionDialogCellEditor extends DialogCellEditor {
		
		
		public ResourceSelectionDialogCellEditor(Composite parent) {
			super(parent);
		}

		protected Object openDialogBox(Control cellEditorWindow) {
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					new WorkbenchLabelProvider(), new WorkbenchContentProvider());
			dialog.setInput(ResourcesPlugin.getWorkspace());
			dialog.setAllowMultiple(false);
			dialog.setTitle(UMLPlugin.getDefault().getResourceString("fileSelectionDialog.title"));
			if (dialog.open() == Dialog.OK) {
				IFile file = (IFile) dialog.getFirstResult();
				return file.getFullPath().toPortableString();
			}
			return null;
		}

	}
}
