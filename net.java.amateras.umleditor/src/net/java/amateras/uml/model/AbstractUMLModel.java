package net.java.amateras.uml.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;

import net.java.amateras.uml.UMLColorRegistry;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.properties.BooleanPropertyDescriptor;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * GEFのエディタで使用するモデルの基底クラス。
 */
public abstract class AbstractUMLModel implements Serializable, IPropertySource {

	public static final String P_BACKGROUND_COLOR = "_background";
	
	public static final String P_FOREGROUND_COLOR = "_foreground";
	
	public static final String P_SHOW_ICON = "_showicon";

	private RGB backgroundColor;
	
	private RGB foregroundColor;
	
	private boolean showIcon = true;
	
	private AbstractUMLEntityModel parent;
	
	/** リスナのリスト */
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/** リスナの追加 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/** モデルの変更を通知 */
	public void firePropertyChange(String propName, Object oldValue,Object newValue) {
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	/** リスナの削除 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	public Object getEditableValue() {
		return this;
	}

	public void setParent(AbstractUMLEntityModel parent) {
		this.parent = parent;
	}
	
	public AbstractUMLEntityModel getParent() {
		return parent;
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground")),
				new BooleanPropertyDescriptor(P_SHOW_ICON, UMLPlugin
						.getDefault().getResourceString("property.showicon")) };
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(P_BACKGROUND_COLOR)) {
			return backgroundColor;
		} else if (P_FOREGROUND_COLOR.equals(id)) {
			return foregroundColor;
		} else if (P_SHOW_ICON.equals(id)) {
			return new Boolean(isShowIcon());
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		return P_BACKGROUND_COLOR.equals(id) || P_FOREGROUND_COLOR.equals(id)
				|| P_SHOW_ICON.equals(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (P_BACKGROUND_COLOR.equals(id)) {
			setBackgroundColor((RGB) value);
		} else if (P_FOREGROUND_COLOR.equals(id)) {
			setForegroundColor((RGB) value);
		} else if (P_SHOW_ICON.equals(id)) {
			setShowIcon(((Boolean) value).booleanValue());
		}
	}

	public void resetPropertyValue(Object id) {
	}

	public Color getBackgroundColor() {
		return UMLColorRegistry.getColor(backgroundColor);
	}

	public void setBackgroundColor(RGB backgroundColor) {
		this.backgroundColor = backgroundColor;
		firePropertyChange(P_BACKGROUND_COLOR, null, backgroundColor);
	}

	public Color getForegroundColor() {
		return UMLColorRegistry.getColor(foregroundColor);
	}

	public void setForegroundColor(RGB foregroundColor) {
		this.foregroundColor = foregroundColor;
		firePropertyChange(P_FOREGROUND_COLOR, null, foregroundColor);
	}

	public boolean isShowIcon() {
		return showIcon;
	}

	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
		firePropertyChange(P_SHOW_ICON, null, new Boolean(showIcon));
	}
	
	public void copyPresentation(AbstractUMLModel model) {
		if (backgroundColor != null) {
			model.setBackgroundColor(backgroundColor);
		}
		if (foregroundColor != null) {
			model.setForegroundColor(foregroundColor);
		}
		model.setShowIcon(showIcon);
	}
//	/**
//	 * 引数で渡されたオブジェクトがこのオブジェクトと等しいかどうかを判定します。
//	 * デフォルトではRuntimeExceptionがthrowされるようになっており、
//	 * ビジュアルモデルはこのメソッドを適切に実装する必要があります。
//	 */
//	public boolean equals(Object obj){
//		throw new RuntimeException("equals is not implemented!");
//	}
}
