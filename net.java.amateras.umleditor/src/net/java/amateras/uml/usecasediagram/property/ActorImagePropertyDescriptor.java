/**
 * 
 */
package net.java.amateras.uml.usecasediagram.property;

import net.java.amateras.uml.UMLPlugin;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author shida
 * 
 */
public class ActorImagePropertyDescriptor extends PropertyDescriptor {

	public ActorImagePropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		ImageSelectionDialogCellEditor editor = new ImageSelectionDialogCellEditor(
				parent);
		if (getValidator() != null) {
			editor.setValidator(getValidator());
		}
		return editor;
	}

	/**
	 * このPropertyDescriptorで使用するセルエディタ。 ListEditDialogを使用してリストの編集を行います。
	 */
	private class ImageSelectionDialogCellEditor extends DialogCellEditor {

		public ImageSelectionDialogCellEditor(Composite parent) {
			super(parent);
		}

		protected Object openDialogBox(Control cellEditorWindow) {
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow()
							.getShell(), new WorkbenchLabelProvider(),
					new WorkbenchContentProvider());
			dialog.setInput(ResourcesPlugin.getWorkspace());
			dialog.setAllowMultiple(false);
			dialog.setTitle(UMLPlugin.getDefault().getResourceString(
					"imageSelectionDialog.title"));
			dialog.setValidator(new ISelectionStatusValidator() {

				public IStatus validate(Object[] selection) {
					for (int i = 0; i < selection.length; i++) {
						Object object = selection[i];
						if (object instanceof IFile) {
							IFile file = (IFile) object;
							String extension = file.getFullPath()
									.getFileExtension();
							if ("gif".equals(extension)
									|| "jpg".equals(extension)
									|| "jpeg".equals(extension)
									|| "png".equals(extension)
									|| "bmp".equals(extension)) {
								return Status.OK_STATUS;
							}
							return new Status(IStatus.ERROR,
									UMLPlugin.PLUGIN_ID, 0, "", null);
						}
					}
					return new Status(IStatus.ERROR, UMLPlugin.PLUGIN_ID, 0,
							"", null);
				}

			});
			if (dialog.open() == Dialog.OK) {
				IFile file = (IFile) dialog.getFirstResult();
				return file.getFullPath().toPortableString();
			}
			return null;
		}

	}
}
