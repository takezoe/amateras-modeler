/**
 * 
 */
package net.java.amateras.uml.sequencediagram.property;

import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.sequencediagram.action.ImportModelUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Takahiro Shida.
 *
 */
public class TypePropertyDescriptor extends PropertyDescriptor {

	public TypePropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public CellEditor createPropertyEditor(Composite parent) {
		DialogCellEditor cellEditor = new DialogCellEditor(parent) {

			protected Object openDialogBox(Control cellEditorWindow) {
				IFile file = ImportModelUtil.selectClassDiagram();
				if (file == null) {
					return null;
				}
				AbstractUMLEntityModel entityModel = ImportModelUtil.selectClassModel(file);
				if (entityModel == null) {
					return null;
				}
				return entityModel;
			}
			
		};
		return cellEditor;
	}
}
