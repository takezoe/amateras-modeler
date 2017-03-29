package net.java.amateras.uml.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * The root model of the UML editor.
 * 
 * @author Naoki Takezoe
 */
public class RootModel extends AbstractUMLEntityModel {

	public static final String P_CHILDREN = "_children";
	
	public void copyFrom(RootModel model){
		getChildren().clear();
		getChildren().addAll(model.getChildren());
		firePropertyChange(P_CHILDREN, null, null);
	}
	
	@Override
	public void setBackgroundColor(RGB backgroundColor) {
		super.setBackgroundColor(backgroundColor);
		for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			element.setBackgroundColor(backgroundColor);
			List<AbstractUMLConnectionModel> connections = element.getModelSourceConnections();
			for (Iterator<AbstractUMLConnectionModel> iterator = connections.iterator(); iterator.hasNext();) {
				AbstractUMLConnectionModel connection = (AbstractUMLConnectionModel) iterator.next();
				connection.setBackgroundColor(backgroundColor);
			}
		}
	}

	@Override
	public void setForegroundColor(RGB foregroundColor) {
		super.setForegroundColor(foregroundColor);
		for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			element.setForegroundColor(foregroundColor);
			List<AbstractUMLConnectionModel> connections = element.getModelSourceConnections();
			for (Iterator<AbstractUMLConnectionModel> iterator = connections.iterator(); iterator.hasNext();) {
				AbstractUMLConnectionModel connection = (AbstractUMLConnectionModel) iterator.next();
				connection.setForegroundColor(foregroundColor);
			}
		}
	}

	@Override
	public void setFilterProperty(Map<String, Boolean> filterProperty) {
		super.setFilterProperty(filterProperty);
		for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			copyFilter(element);
		}
	}
	
	@Override
	public void setShowIcon(boolean showIcon) {
		super.setShowIcon(showIcon);
		for (Iterator<AbstractUMLModel> iter = getChildren().iterator(); iter.hasNext();) {
			AbstractUMLEntityModel element = (AbstractUMLEntityModel) iter.next();
			element.setShowIcon(showIcon);
		}
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new BooleanPropertyDescriptor(P_SHOW_ICON, UMLPlugin
						.getDefault().getResourceString("property.showicon")) };
	}

	public void copyFilter(AbstractUMLEntityModel model) {
		Map<String, Boolean> newMap = new HashMap<String, Boolean>();
		newMap.putAll(this.getFilterProperty());
		model.setFilterProperty(newMap);
	}
}
