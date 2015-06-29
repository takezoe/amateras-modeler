package net.java.amateras.uml.classdiagram.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.ICloneableModel;
import net.java.amateras.uml.model.TypeEntityModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.PropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * Common model of class and interface model
 * @author <a HREF="mailto:huanze.xu@nsn.com">Xu Huanze(Wayne)</a>
 *
 */
public class CommonEntityModel extends AbstractUMLEntityModel implements TypeEntityModel, StereoTypeModel, ICloneableModel {

	protected IPropertyDescriptor[] propertyDescriptors;

	/**
	 * Default constructor
	 */
	public CommonEntityModel() {
		propertyDescriptors = new IPropertyDescriptor[] {
				new TextPropertyDescriptor(StereoTypeModel.P_STEREO_TYPE, UMLPlugin.getDefault().getResourceString("property.stereoType")),
				new PropertyDescriptor(P_SIMPLE_ENTITY_NAME, UMLPlugin.getDefault().getResourceString("property.simpleName")),
				new TextPropertyDescriptor(P_ENTITY_NAME, UMLPlugin.getDefault().getResourceString("property.name")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin.getDefault().getResourceString("property.background")),
				new PropertyDescriptor(P_ATTRIBUTES, UMLPlugin.getDefault().getResourceString("property.attributes")),
				new PropertyDescriptor(P_OPERATIONS, UMLPlugin.getDefault().getResourceString("property.operations")) };
	}

	/**
	 * Copy constructor, copy the given CommonEntityModel model to this
	 * @param toCopy CommonEntityModel to copy
	 */
	public CommonEntityModel(CommonEntityModel toCopy) {

		setBackgroundColor(toCopy.getBackgroundColor().getRGB());
		setConstraint(new Rectangle(toCopy.getConstraint()));
		setForegroundColor(toCopy.getForegroundColor().getRGB());
		setName(toCopy.getName());
		setParent(toCopy.getParent());
		setShowIcon(toCopy.isShowIcon());
		setStereoType(toCopy.getStereoType());

		List<AbstractUMLModel> children = toCopy.getChildren();
		for (int i = 0; i < children.size(); i++) {
			AbstractUMLModel child = children.get(i);
			if (child instanceof AttributeModel) {
				addChild((AttributeModel) ((AttributeModel) child).clone());
			} else if (child instanceof OperationModel) {
				addChild((OperationModel) ((OperationModel) child).clone());
			}
		}
	}

//	/**
//	 * Simple name of the class or interface
//	 */
//	public static final String P_SIMPLE_NAME = "_simple_name";
//
//	public static final String P_NAME = "_name";

	private String stereoType = "";

	protected static final String P_ATTRIBUTES = "_attrs";

	protected static final String P_OPERATIONS = "_operations";

	private String name = "";

	public void setName(String newName) {
		String oldName = this.name;
		this.name = newName;
		firePropertyChange(P_ENTITY_NAME, oldName, newName);
	}

	/**
	 * Set simple name of the class, full class name will also be updated
	 * @param simpleName
	 */
	public void setSimpleName(String simpleName) {
		String packageName = "";
		if (getName() != null) {
			int lastDotPosition = getName().lastIndexOf('.');
			if (lastDotPosition != -1) {
				packageName = getName().substring(0, lastDotPosition);
			}
		}
		if (packageName.length() > 0) {
			setName(packageName + "." + simpleName);
		} else {
			setName(simpleName);
		}
	}

	public String getName() {
		return this.name;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return propertyDescriptors;
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			return getStereoType();
		} else if (id.equals(P_SIMPLE_ENTITY_NAME)) {
			return getSimpleName();
		} else if (id.equals(P_ENTITY_NAME)) {
			return getName();
		} else if (id.equals(P_ATTRIBUTES)) {
			List<AttributeModel> rv = new ArrayList<AttributeModel>();
			for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof AttributeModel) {
					rv.add((AttributeModel) element);
				}
			}
			return new ListPropertyWrapper(rv);
		} else if (id.equals(P_OPERATIONS)) {
			List<OperationModel> rv = new ArrayList<OperationModel>();
			for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof OperationModel) {
					rv.add((OperationModel) element);
				}
			}
			return new ListPropertyWrapper(rv);
		}
		return super.getPropertyValue(id);
	}

	/**
	 * Gets the short name of the class which doesn't contain package name.
	 * @return Short name which doesn't contain package name.
	 */
	public String getSimpleName() {
		String simpleName = getName();
		if (getName() != null) {
			int lastDotPosition = getName().lastIndexOf('.');
			if (lastDotPosition != -1) {
				simpleName = getName().substring(lastDotPosition + 1);
			}
		}
		return simpleName;
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			return true;
		} else if (id.equals(P_SIMPLE_ENTITY_NAME)) {
			return true;
		} else if (id.equals(P_ENTITY_NAME)) {
			return true;
		}
		return super.isPropertySet(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			setStereoType((String) value);
		} else if (id.equals(P_ENTITY_NAME)) {
			setName((String) value);
		}
		super.setPropertyValue(id, value);
	}

	public String toString() {
		return getName();
	}

	public String getStereoType() {
		return stereoType == null ? "" : stereoType;
	}

	public void setStereoType(String stereoType) {
		this.stereoType = stereoType;
		firePropertyChange(StereoTypeModel.P_STEREO_TYPE, null, stereoType);
	}

	/**
	 * Clone, this method is @deprecated, use copy constructor instead
	 * @deprecated Use copy constructor instead
	 * @throws CloneNotSupportedException
	 */
	public Object clone() {
		throw new RuntimeException("Clone is not supported, use copy constructor instead");

	}

}
