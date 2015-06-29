/**
 * 
 */
package net.java.amateras.uml.sequencediagram.property;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author shida
 *
 */
public class MessagePropertyDescriptor extends PropertyDescriptor {

	List models;
	
	public MessagePropertyDescriptor(Object id, String displayName) {
		super(id, displayName);
	}

	public void setUMLModels(List models) {
		this.models = models;
	}
	public CellEditor createPropertyEditor(Composite parent) {
		MessageTextCellEditor cellEditor = new MessageTextCellEditor(parent);
		cellEditor.setUMLModel(models);
		if (getValidator() != null){
			cellEditor.setValidator(getValidator());
		}
		return cellEditor;
	}
}
