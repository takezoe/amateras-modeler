package net.java.amateras.uml.classdiagram.model;

import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.geometry.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class model that contains the data of a class shown in class diagram.
 *
 * @author Naoki Takezoe
 */
public class InterfaceModel extends CommonEntityModel {

	private static int number = 1;

	public InterfaceModel() {
		setName("Interface" + number);
		number++;
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(CommonEntityModel.P_ATTRIBUTES)) {
			List<AbstractUMLModel> rv = new ArrayList<AbstractUMLModel>();
			for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
				AbstractUMLModel element = iter.next();
				if (element instanceof AttributeModel) {
					AttributeModel attribute = (AttributeModel) element;
					attribute.setVisibility(Visibility.PUBLIC);
					rv.add(element);
				}
			}
			return new ListPropertyWrapper(rv);
		} else if (id.equals(CommonEntityModel.P_OPERATIONS)) {
			List<AbstractUMLModel> rv = new ArrayList<AbstractUMLModel>();
			for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
				AbstractUMLModel element = iter.next();
				if (element instanceof OperationModel) {
					OperationModel operation = (OperationModel) element;
					operation.setVisibility(Visibility.PUBLIC);
					rv.add(element);
				}
			}
			return new ListPropertyWrapper(rv);
		}
		return super.getPropertyValue(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(StereoTypeModel.P_STEREO_TYPE)) {
			setStereoType((String) value);
		} else if (id.equals(P_ENTITY_NAME)) {
			setName((String) value);
		}
		super.setPropertyValue(id, value);
	}

	/**
	 * Clone this object. TODO, duplicated code with InterfaceModel.clone(), be careful to override clone!
	 *@deprecated
	 */
	public Object clone() {
		InterfaceModel newModel = new InterfaceModel();

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
