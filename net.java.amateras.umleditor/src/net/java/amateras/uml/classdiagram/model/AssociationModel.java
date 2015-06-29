package net.java.amateras.uml.classdiagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

public class AssociationModel extends AbstractUMLConnectionModel implements
		StereoTypeModel {

	private String stereoType = "";

	private String fromMultiplicity = "";

	private String toMultiplicity = "";

	public static final String P_FROM_MULTIPLICITY = "_from";

	public static final String P_TO_MULTIPLICITY = "_to";

	public void setStereoType(String stereoType) {
		this.stereoType = stereoType;
		firePropertyChange(StereoTypeModel.P_STEREO_TYPE, null, stereoType);
	}

	public String getStereoType() {
		return this.stereoType;
	}

	public void setFromMultiplicity(String fromMultiplicity) {
		this.fromMultiplicity = fromMultiplicity;
		firePropertyChange(P_FROM_MULTIPLICITY, null, fromMultiplicity);
	}

	public String getFromMultiplicity() {
		return this.fromMultiplicity;
	}

	public void setToMultiplicity(String toMultiplicity) {
		this.toMultiplicity = toMultiplicity;
		firePropertyChange(P_TO_MULTIPLICITY, null, toMultiplicity);
	}

	public String getToMultiplicity() {
		return this.toMultiplicity;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(StereoTypeModel.P_STEREO_TYPE, UMLPlugin
						.getDefault().getResourceString("property.stereoType")),
				new TextPropertyDescriptor(P_FROM_MULTIPLICITY, UMLPlugin
						.getDefault().getResourceString(
								"property.multiplicityA")),
				new TextPropertyDescriptor(P_TO_MULTIPLICITY, UMLPlugin
						.getDefault().getResourceString(
								"property.multiplicityB")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))
				};
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			return getStereoType();
		} else if (id.equals(P_FROM_MULTIPLICITY)) {
			return getFromMultiplicity();
		} else if (id.equals(P_TO_MULTIPLICITY)) {
			return getToMultiplicity();
		}
		return super.getPropertyValue(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			setStereoType((String) value);
		} else if (id.equals(P_FROM_MULTIPLICITY)) {
			setFromMultiplicity((String) value);
		} else if (id.equals(P_TO_MULTIPLICITY)) {
			setToMultiplicity((String) value);
		}
		super.setPropertyValue(id, value);
	}

}
