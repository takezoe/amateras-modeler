/**
 * 
 */
package net.java.amateras.uml.activitydiagram.model;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.EntityModel;

/**
 * 
 * @author Takahiro Shida
 * @author Naoki Takezoe
 */
public class VerticalPartitionModel extends AbstractUMLEntityModel implements EntityModel {

	private String partitionName = "パーテーション";
	
	public static final String P_PARTITION_NAME = "_partitionName";

	public String getPartitionName() {
		return partitionName;
	}

	public void setPartitionName(String partitionName) {
		this.partitionName = partitionName;
		firePropertyChange(P_PARTITION_NAME, null, partitionName);
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_PARTITION_NAME, UMLPlugin
						.getDefault().getResourceString("property.name")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))};
	}
	
	public Object getPropertyValue(Object id) {
		if (P_PARTITION_NAME.equals(id)) {
			return getPartitionName();
		}
		return super.getPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value) {
		if (P_PARTITION_NAME.equals(id)) {
			setPartitionName((String) value);
		}
		super.setPropertyValue(id, value);
	}
	
	public boolean isPropertySet(Object id) {
		return P_PARTITION_NAME.equals(id);
	}

	public String getName() {
		return getPartitionName();
	}

	public void setName(String name) {
		setPartitionName(name);
	}

	public void addChild(AbstractUMLModel model) {
		getParent().addChild(model);
	}
	
}
