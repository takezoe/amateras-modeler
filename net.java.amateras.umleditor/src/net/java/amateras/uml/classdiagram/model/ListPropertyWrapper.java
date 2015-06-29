/**
 *
 */
package net.java.amateras.uml.classdiagram.model;

import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Takahiro Shida.
 *
 */
@SuppressWarnings("rawtypes")
public class ListPropertyWrapper implements IPropertySource {

	private List content = null;

	public ListPropertyWrapper(List content) {
		super();
		this.content = content;
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		PropertyDescriptor[] descriptors = new PropertyDescriptor[content.size()];
		for (int i = 0; i < descriptors.length; i++) {
			 descriptors[i] = new PropertyDescriptor(String.valueOf(i), content.get(i).toString());
			 descriptors[i].setLabelProvider(new LabelProvider() {
				public String getText(Object element) {
					return "";
				}
			 });
		}
		return descriptors;
	}

	public Object getPropertyValue(Object id) {
		for (int i = 0; i < content.toArray().length; i++) {
			if (id.equals(String.valueOf(i))) {
				return content.get(i);
			}
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		return content.size() > Integer.parseInt(id.toString());
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}

	public String toString() {
		return "";
	}
}
