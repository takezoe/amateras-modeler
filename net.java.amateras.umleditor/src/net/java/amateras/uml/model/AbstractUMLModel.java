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
 * GEF�̃G�f�B�^�Ŏg�p���郂�f���̊��N���X�B
 */
public abstract class AbstractUMLModel implements Serializable, IPropertySource {

	public static final String P_BACKGROUND_COLOR = "_background";
	
	public static final String P_FOREGROUND_COLOR = "_foreground";
	
	public static final String P_SHOW_ICON = "_showicon";

	private RGB backgroundColor;
	
	private RGB foregroundColor;
	
	private boolean showIcon = true;
	
	private AbstractUMLEntityModel parent;
	
	/** ���X�i�̃��X�g */
	private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

	/** ���X�i�̒ǉ� */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		listeners.addPropertyChangeListener(listener);
	}

	/** ���f���̕ύX��ʒm */
	public void firePropertyChange(String propName, Object oldValue,Object newValue) {
		listeners.firePropertyChange(propName, oldValue, newValue);
	}

	/** ���X�i�̍폜 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		listeners.removePropertyChangeListener(listener);
	}

	@Override
	public Object getEditableValue() {
		return this;
	}

	public void setParent(AbstractUMLEntityModel parent) {
		this.parent = parent;
	}
	
	public AbstractUMLEntityModel getParent() {
		return parent;
	}
	
	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.background")),
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground")),
				new BooleanPropertyDescriptor(P_SHOW_ICON, UMLPlugin
						.getDefault().getResourceString("property.showicon")) };
	}

	@Override
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

	@Override
	public boolean isPropertySet(Object id) {
		return P_BACKGROUND_COLOR.equals(id) || P_FOREGROUND_COLOR.equals(id)
				|| P_SHOW_ICON.equals(id);
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		if (P_BACKGROUND_COLOR.equals(id)) {
			setBackgroundColor((RGB) value);
		} else if (P_FOREGROUND_COLOR.equals(id)) {
			setForegroundColor((RGB) value);
		} else if (P_SHOW_ICON.equals(id)) {
			setShowIcon(((Boolean) value).booleanValue());
		}
	}

	@Override
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
//	 * �����œn���ꂽ�I�u�W�F�N�g�����̃I�u�W�F�N�g�Ɠ��������ǂ����𔻒肵�܂��B
//	 * �f�t�H���g�ł�RuntimeException��throw�����悤�ɂȂ��Ă���A
//	 * �r�W���A�����f���͂��̃��\�b�h��K�؂Ɏ�������K�v������܂��B
//	 */
//	public boolean equals(Object obj){
//		throw new RuntimeException("equals is not implemented!");
//	}
}
