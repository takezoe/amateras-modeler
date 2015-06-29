package net.java.amateras.uml.classdiagram.model;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.property.ArgumentsPropertyDescriptor;
import net.java.amateras.uml.classdiagram.property.EnumPropertyDescriptor;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * オペレーションを表すモデルオブジェクト。
 *
 * @author Naoki Takezoe
 */
public class OperationModel extends AbstractUMLModel implements Cloneable {

	private Visibility visibility = Visibility.PUBLIC;
	private String name = "";
	private String type = "void";
	private List<Argument> params = new ArrayList<Argument>();
	private boolean isAbstract = false;
	private boolean isStatic = false;

	public static final String P_VISIBILITY = "_visibility";
	public static final String P_NAME = "_name";
	public static final String P_TYPE = "_type";
	public static final String P_PARAMS = "_params";
	public static final String P_ABSTRACT = "_abstract";
	public static final String P_STATIC = "_static";

	public boolean isConstructor(){
		if(getType().length() == 0 || getType().equals("void")){
			AbstractUMLEntityModel parent = getParent();
			if(parent != null && parent instanceof ClassModel){
				String className = ((ClassModel) parent).getName();
				int index = className.lastIndexOf('.');
				if(index >= 0){
					className = className.substring(index + 1);
				}
				return className.equals(getName());
			}
		}
		return false;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
		firePropertyChange(P_ABSTRACT,null,new Boolean(isAbstract));
	}

	public boolean isStatic() {
		return isStatic;
	}

	public void setStatic(boolean isStatic) {
		this.isStatic = isStatic;
		firePropertyChange(P_STATIC,null,new Boolean(isStatic));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		firePropertyChange(P_NAME,null,name);
	}

	public List<Argument> getParams() {
		return params;
	}

	public void setParams(List<Argument> params) {
		this.params = params;
		firePropertyChange(P_PARAMS,null,params);
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

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[]{
				new TextPropertyDescriptor(P_NAME,
						UMLPlugin.getDefault().getResourceString("property.name")),
				new TextPropertyDescriptor(P_TYPE,
						UMLPlugin.getDefault().getResourceString("property.type")),
				new EnumPropertyDescriptor(P_VISIBILITY,
						UMLPlugin.getDefault().getResourceString("property.visibility"),
						Visibility.getVisibilities()),
				new ArgumentsPropertyDescriptor(P_PARAMS,
						UMLPlugin.getDefault().getResourceString("property.arguments")),
				new BooleanPropertyDescriptor(P_STATIC,
						UMLPlugin.getDefault().getResourceString("property.static")),
				new BooleanPropertyDescriptor(P_ABSTRACT,
						UMLPlugin.getDefault().getResourceString("property.abstract")),
						new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
								.getDefault().getResourceString("property.foreground"))
		};
	}

	public Object getPropertyValue(Object id) {
		if(id.equals(P_NAME)){
			return getName();
		} else if(id.equals(P_TYPE)){
			return getType();
		} else if(id.equals(P_VISIBILITY)){
			return getVisibility();
		} else if(id.equals(P_PARAMS)){
			return getParams();
		} else if(id.equals(P_STATIC)){
			return new Boolean(isStatic());
		} else if(id.equals(P_ABSTRACT)){
			return new Boolean(isAbstract());
		}
		return super.getPropertyValue(id);
	}

	public boolean isPropertySet(Object id) {
		if(id.equals(P_NAME)){
			return true;
		} else if(id.equals(P_TYPE)){
			return true;
		} else if(id.equals(P_VISIBILITY)){
			return true;
		} else if(id.equals(P_PARAMS)){
			return true;
		} else if(id.equals(P_STATIC)){
			return true;
		} else if(id.equals(P_ABSTRACT)){
			return true;
		}
		return super.isPropertySet(id);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public void setPropertyValue(Object id, Object value) {
		if(id.equals(P_NAME)){
			setName((String)value);
		} else if(id.equals(P_TYPE)){
			setType((String)value);
		} else if(id.equals(P_VISIBILITY)){
			setVisibility((Visibility)value);
		} else if(id.equals(P_PARAMS)){
			setParams((List) value);
		} else if(id.equals(P_STATIC)){
			setStatic(((Boolean)value).booleanValue());
		} else if(id.equals(P_ABSTRACT)){
			setAbstract(((Boolean)value).booleanValue());
		}
		super.setPropertyValue(id, value);
	}


	public String toString(){
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		sb.append("(");
		for(int i=0;i<params.size();i++){
			if(i!=0){
				sb.append(", ");
			}
			Argument arg = (Argument)params.get(i);
			sb.append(arg.toString());
		}
		sb.append(")");
		if(!isConstructor()){
			sb.append(": ");
			sb.append(getType());
		}
		return sb.toString();
	}

	public Object clone() {
		OperationModel newModel = new OperationModel();

		newModel.setName(getName());
		newModel.setType(getType());
		newModel.setVisibility(getVisibility());

		List<Argument> args = getParams();
		List<Argument> newArgs = new ArrayList<Argument>();
		for(int i=0;i<args.size();i++){
			newArgs.add((Argument) args.get(i).clone());
		}

		newModel.setParams(newArgs);
		newModel.setStatic(isStatic());
		newModel.setAbstract(isAbstract());

		return newModel;
	}


}
