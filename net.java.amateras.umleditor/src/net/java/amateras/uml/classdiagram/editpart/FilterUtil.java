/**
 *
 */
package net.java.amateras.uml.classdiagram.editpart;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.classdiagram.action.ToggleAction;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

/**
 * @author Takahiro Shida.
 *
 */
public class FilterUtil {

	public static List<AbstractUMLModel> getFilteredChildren(AbstractUMLEntityModel model) {
		List<AbstractUMLModel> rv = new ArrayList<AbstractUMLModel>();
		rv.addAll(getAttribute(model));
		rv.addAll(getOperation(model));
		return rv;
	}

	private static List<AttributeModel> getAttribute(AbstractUMLEntityModel model) {
		List<AttributeModel> rv = new ArrayList<AttributeModel>();
		List<AbstractUMLModel> children = model.getChildren();
		Map<String, Boolean> map = model.getFilterProperty();
		boolean v_public = isShow(ToggleAction.ATTRIBUTE + Visibility.PUBLIC, map);
		boolean v_protected = isShow(ToggleAction.ATTRIBUTE + Visibility.PROTECTED, map);
		boolean v_package = isShow(ToggleAction.ATTRIBUTE + Visibility.PACKAGE, map);
		boolean v_private = isShow(ToggleAction.ATTRIBUTE + Visibility.PRIVATE, map);
		for (Iterator<AbstractUMLModel> iter = children.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			if (element instanceof AttributeModel) {
				AttributeModel a = (AttributeModel) element;
				if (v_public && a.getVisibility().equals(Visibility.PUBLIC)) {
					rv.add(a);
				} else if (v_protected && a.getVisibility().equals(Visibility.PROTECTED)) {
					rv.add(a);
				} else if (v_package && a.getVisibility().equals(Visibility.PACKAGE)) {
					rv.add(a);
				} else if (v_private && a.getVisibility().equals(Visibility.PRIVATE)) {
					rv.add(a);
				}
			}
		}
		return rv;
	}

	private static List<OperationModel> getOperation(AbstractUMLEntityModel model) {
		List<OperationModel> rv = new ArrayList<OperationModel>();
		List<AbstractUMLModel> children = model.getChildren();
		Map<String, Boolean> map = model.getFilterProperty();
		boolean v_public = isShow(ToggleAction.OPERATION + Visibility.PUBLIC, map);
		boolean v_protected = isShow(ToggleAction.OPERATION + Visibility.PROTECTED, map);
		boolean v_package = isShow(ToggleAction.OPERATION + Visibility.PACKAGE, map);
		boolean v_private = isShow(ToggleAction.OPERATION + Visibility.PRIVATE, map);

		for (Iterator<AbstractUMLModel> iter = children.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			if (element instanceof OperationModel) {
				OperationModel a = (OperationModel) element;
				if (v_public && a.getVisibility().equals(Visibility.PUBLIC)) {
					rv.add(a);
				} else if (v_protected && a.getVisibility().equals(Visibility.PROTECTED)) {
					rv.add(a);
				} else if (v_package && a.getVisibility().equals(Visibility.PACKAGE)) {
					rv.add(a);
				} else if (v_private && a.getVisibility().equals(Visibility.PRIVATE)) {
					rv.add(a);
				}
			}
		}
		return rv;
	}

	private static boolean isShow(String key, Map<String, Boolean> map) {
		if (map == null || !map.containsKey(key)) {
			return true;
		}
		Boolean bool = (Boolean) map.get(key);
		return !bool.booleanValue();
	}
}
