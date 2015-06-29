package net.java.amateras.uml.classdiagram.property;

import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * java.util.List型プロパティのためのプロパティデスクリプタ。
 *
 * @author Naoki Takezoe
 */
public class ArgumentsPropertyDescriptor extends PropertyDescriptor {

	public ArgumentsPropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		FormPropertiesDialogCellEditor editor = new FormPropertiesDialogCellEditor(parent);
		if (getValidator() != null){
			editor.setValidator(getValidator());
		}
		return editor;
	}

	/**
	 * このPropertyDescriptorで使用するセルエディタ。
	 * ListEditDialogを使用してリストの編集を行います。
	 */
	private class FormPropertiesDialogCellEditor extends DialogCellEditor {

		public FormPropertiesDialogCellEditor(Composite parent) {
			super(parent);
		}

		@SuppressWarnings("unchecked")
		protected Object openDialogBox(Control cellEditorWindow) {
			List value = (List) getValue();

			ArgumentsEditDialog dialog = new ArgumentsEditDialog(cellEditorWindow.getShell(), value);
			if(dialog.open()==Dialog.OK){
				value = dialog.getArguments();
			}

			return value;
		}

	}

}
