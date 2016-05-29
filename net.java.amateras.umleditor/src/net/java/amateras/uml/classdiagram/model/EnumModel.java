package net.java.amateras.uml.classdiagram.model;

import java.util.List;

import org.eclipse.draw2d.geometry.Rectangle;

import net.java.amateras.uml.model.AbstractUMLModel;

/**
 * Enum model that contains the data of a java enum shown in class diagram.
 * Java enum is a class with some static final fields.
 * 
 * @author jdelarbre
 */
public class EnumModel extends CommonEntityModel {
	
	private static int number = 1;

	/**
	 * Default constructor
	 */
	public EnumModel() {
		setName("Enum" + number);
		number++;
	}

	/**
	 * Copy constructor, copy the given EnumModel model to this
	 * @param toCopy EnumModel to copy
	 */
	public EnumModel(EnumModel toCopy) {
		super(toCopy);
	}

	public Object getPropertyValue(Object id) {
		return super.getPropertyValue(id);
	}

	public boolean isPropertySet(Object id) {
		return super.isPropertySet(id);
	}

	public void setPropertyValue(Object id, Object value) {
		super.setPropertyValue(id, value);
	}

	/**
	 * Clone this object. TODO, duplicated code with InterfaceModel.clone(), be careful to override clone!
	 *@deprecated
	 */
	public Object clone() {
		EnumModel newModel = new EnumModel();

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
