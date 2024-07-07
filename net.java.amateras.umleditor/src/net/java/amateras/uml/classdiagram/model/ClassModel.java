package net.java.amateras.uml.classdiagram.model;

import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * Class model that contains the data of a class shown in class diagram.
 *
 * @author Naoki Takezoe
 */
public class ClassModel extends CommonEntityModel {

	public static final String P_ABSTRACT = "_abstract";

	public static final String P_FILTER = "_filter";

	private boolean isAbstract = false;

	private static int number = 1;

	/**
	 * Default constructor
	 */
	public ClassModel() {
		setName("Class" + number);
		number++;
		int newLength = propertyDescriptors.length + 1;

		IPropertyDescriptor[] newPropertyDescriptors = new IPropertyDescriptor[newLength];
		System.arraycopy(propertyDescriptors, 0, newPropertyDescriptors, 0, propertyDescriptors.length);

		newPropertyDescriptors[newLength - 1] =
			new BooleanPropertyDescriptor(P_ABSTRACT, UMLPlugin.getDefault().getResourceString("property.abstract"));

		propertyDescriptors = newPropertyDescriptors;
	}

	/**
	 * Copy constructor, copy the given ClassModel model to this
	 * @param toCopy ClassModel to copy
	 */
	public ClassModel(ClassModel toCopy) {
		super(toCopy);
		setAbstract(toCopy.isAbstract());
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
		firePropertyChange(P_ABSTRACT, null, isAbstract);
	}

	@Override
	public Object getPropertyValue(Object id) {
		if (id.equals(P_ABSTRACT)) {
			return isAbstract();
		}
		return super.getPropertyValue(id);
	}

	@Override
	public boolean isPropertySet(Object id) {
		if (id.equals(P_ABSTRACT)) {
			return true;
		}
		return super.isPropertySet(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (id.equals(P_ABSTRACT)) {
			setAbstract(((Boolean) value).booleanValue());
		}
		super.setPropertyValue(id, value);
	}

	/**
	 * Clone this object. TODO, duplicated code with InterfaceModel.clone(), be careful to override clone!
	 *@deprecated
	 */
	@Override
	public Object clone() {
		ClassModel newModel = new ClassModel();

		newModel.setAbstract(isAbstract());
		newModel.setBackgroundColor(getBackgroundColor().getRGB());
		newModel.setConstraint(new Rectangle(getConstraint()));
		newModel.setForegroundColor(getForegroundColor().getRGB());
		newModel.setName(getName());
		newModel.setSimpleName(getSimpleName());
		newModel.setParent(getParent());
		newModel.setShowIcon(isShowIcon());
		newModel.setStereoType(getStereoType());

		List<AbstractUMLModel> children = getChildren();
		for (int i = 0; i < children.size(); i++) {
			AbstractUMLModel child = children.get(i);
			if (child instanceof AttributeModel) {
				newModel.addChild((AttributeModel) ((AttributeModel) child).clone());
			} else if (child instanceof OperationModel) {
				newModel.addChild((OperationModel) ((OperationModel) child).clone());
			}
		}

		return newModel;
	}

}
