package net.java.amateras.uml.activitydiagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * 
 * @author Naoki Takezoe
 */
public class ObjectModel extends AbstractUMLEntityModel implements EntityModel, ICloneableModel {
	
	private String stereoType = "";
	private String objectName = "Object";
	private String objectState = "";
	
	public static final String P_STEREO_TYPE = "_stereoType";
	public static final String P_OBJECT_NAME = "_objectName";
	public static final String P_OBJECT_STATE = "_objectState";
	
	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
		firePropertyChange(P_OBJECT_NAME, null, objectName);
	}

	public String getObjectState() {
		return objectState;
	}

	public void setObjectState(String objectState) {
		this.objectState = objectState;
		firePropertyChange(P_OBJECT_STATE, null, objectState);
	}

	public String getStereoType() {
		return stereoType;
	}

	public void setStereoType(String stereoType) {
		this.stereoType = stereoType;
		firePropertyChange(P_STEREO_TYPE, null, stereoType);
	}

	public String getName() {
		return getObjectName();
	}
	
	public void setName(String name) {
		setObjectName(name);
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_OBJECT_NAME, UMLPlugin
						.getDefault().getResourceString("property.name")),
				new TextPropertyDescriptor(P_OBJECT_STATE, UMLPlugin
						.getDefault().getResourceString("property.state")),
				new TextPropertyDescriptor(P_STEREO_TYPE, UMLPlugin
						.getDefault().getResourceString("property.stereoType")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))};
	}
	
	public Object getPropertyValue(Object id) {
		if (P_OBJECT_NAME.equals(id)) {
			return getObjectName();
		} else if(P_OBJECT_STATE.equals(id)){
			return getObjectState();
		} else if(P_STEREO_TYPE.equals(id)){
			return getStereoType();
		}
		return super.getPropertyValue(id);
	}
	
	public void setPropertyValue(Object id, Object value) {
		if (P_OBJECT_NAME.equals(id)) {
			setObjectName((String) value);
		} else if(P_OBJECT_STATE.equals(id)){
			setObjectState((String)value);
		} else if(P_STEREO_TYPE.equals(id)){
			setStereoType((String)value);
		}
		super.setPropertyValue(id, value);
	}
	
	public boolean isPropertySet(Object id) {
		return P_OBJECT_NAME.equals(id) || P_OBJECT_STATE.equals(id) ||
			P_STEREO_TYPE.equals(id) || super.isPropertySet(id);
	}
	
	public Object clone(){
		ObjectModel model = new ObjectModel();
		model.setParent(getParent());
		model.setObjectName(getObjectName());
		model.setObjectState(getObjectState());
		model.setStereoType(getStereoType());
		model.setForegroundColor(getForegroundColor().getRGB());
		model.setBackgroundColor(getBackgroundColor().getRGB());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
