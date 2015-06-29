package net.java.amateras.uml.classdiagram.model;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.property.EnumPropertyDescriptor;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * 属性を表すモデルオブジェクト。
 * 
 * @author Naoki Takezoe
 */
public class AttributeModel extends AbstractUMLModel implements Cloneable {
	
	private Visibility visibility = Visibility.PRIVATE;
	private String name = "";
	private String type = "int";
	private boolean isStatic;
	
	public static final String P_VISIBILITY = "_visibility";
	public static final String P_NAME = "_name";
	public static final String P_TYPE = "_type";
	public static final String P_STATIC = "_static";
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
		firePropertyChange(P_NAME,null,name);
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
		firePropertyChange(P_TYPE,null,type);
	}
	
	public Visibility getVisibility() {
		return visibility;
	}
	
	public void setVisibility(Visibility visibility) {
		this.visibility = visibility;
		firePropertyChange(P_VISIBILITY,null,visibility);
		if (getParent() != null) {
			getParent().forceUpdate();
		}
	}
	
	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		firePropertyChange(P_STATIC,null,new Boolean(isStatic));
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[]{
				new TextPropertyDescriptor(P_NAME, 
						UMLPlugin.getDefault().getResourceString("property.name")),
				new TextPropertyDescriptor(P_TYPE,
						UMLPlugin.getDefault().getResourceString("property.type")),
				new EnumPropertyDescriptor(P_VISIBILITY,
						UMLPlugin.getDefault().getResourceString("property.visibility"),
						Visibility.getVisibilities()),
				new BooleanPropertyDescriptor(P_STATIC,
						UMLPlugin.getDefault().getResourceString("property.static"))
		};
	}

	public Object getPropertyValue(Object id) {
		if(id.equals(P_NAME)){
			return getName();
		} else if(id.equals(P_TYPE)){
			return getType();
		} else if(id.equals(P_VISIBILITY)){
			return getVisibility();
		} else if(id.equals(P_STATIC)){
			return new Boolean(isStatic());
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		if(id.equals(P_NAME)){
			return true;
		} else if(id.equals(P_TYPE)){
			return true;
		} else if(id.equals(P_VISIBILITY)){
			return true;
		} else if(id.equals(P_STATIC)){
			return true;
		}
		return false;
	}

	public void setPropertyValue(Object id, Object value) {
		if(id.equals(P_NAME)){
			setName((String)value);
		} else if(id.equals(P_TYPE)){
			setType((String)value);
		} else if(id.equals(P_VISIBILITY)){
			setVisibility((Visibility)value);
		} else if(id.equals(P_STATIC)){
			setStatic(((Boolean)value).booleanValue());
		}
	}

	
	public String toString(){
		StringBuffer sb = new StringBuffer();
//		sb.append(visibility.getSign());
		sb.append(getName());
		sb.append(": ");
		sb.append(getType());
		return sb.toString();
	}

	public Object clone() {
		AttributeModel newModel = new AttributeModel();
		newModel.setName(getName());
		newModel.setType(getType());
		newModel.setVisibility(getVisibility());
		newModel.setStatic(isStatic());
		return newModel;
	}
	
}
