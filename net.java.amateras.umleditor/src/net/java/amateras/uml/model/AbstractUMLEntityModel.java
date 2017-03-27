package net.java.amateras.uml.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.RGB;

public abstract class AbstractUMLEntityModel extends AbstractUMLModel {
	
	private Rectangle constraint;
	// ���̃��f������L�тĂ���R�l�N�V�����̃��X�g
	private List<AbstractUMLConnectionModel> sourceConnections = new ArrayList<AbstractUMLConnectionModel>();
	// ���̃��f���Ɍ������Ē����Ă���R�l�N�V�����̃��X�g
	private List<AbstractUMLConnectionModel> targetConnections = new ArrayList<AbstractUMLConnectionModel>();
	
	private List<AbstractUMLModel> children = new ArrayList<AbstractUMLModel>();
	//flags of show/hide property
	private Map<String, Boolean> filterProperty = new HashMap<String, Boolean>();
		
	public static final String P_CONSTRAINT = "_constraint";
	public static final String P_SOURCE_CONNECTION = "_source_connection";
	public static final String P_TARGET_CONNECTION = "_target_connection";
	public static final String P_CHILDREN = "_children";
	public static final String P_FILTER = "_filter";
	public static final String P_FORCE_UPDATE = "_force_update";
	
	public Map<String, Boolean> getFilterProperty() {
		return filterProperty;
	}
	
	public void setFilterProperty(Map<String, Boolean> filterProperty) {
		this.filterProperty = filterProperty;
		firePropertyChange(P_FILTER, null, filterProperty);
	}
	
	public Rectangle getConstraint() {
		return constraint;
	}
	
	public void addChild(AbstractUMLModel model) {
		children.add(model);
		model.setParent(this);
		firePropertyChange(P_CHILDREN,null,model);
	}
	
	public void removeChild(AbstractUMLModel model) {
		children.remove(model);
		model.setParent(null);
		firePropertyChange(P_CHILDREN,model,null);
	}
	
	public void forceUpdate() {
		firePropertyChange(P_FORCE_UPDATE, null ,null);
	}

	public List<AbstractUMLModel> getChildren(){
		return this.children;
	}
	
	public void setConstraint(Rectangle constraint) {
		if(constraint.x < 0){
			constraint.x = 0;
		}
		if(constraint.y < 0){
			constraint.y = 0;
		}
		this.constraint = constraint;
		firePropertyChange(P_CONSTRAINT, null, constraint);
	}
	
	/** ���̃��f������o��R�l�N�V���� ���f���̒ǉ� */
	public void addSourceConnection(AbstractUMLConnectionModel connx) {
		sourceConnections.add(connx);
		firePropertyChange(P_SOURCE_CONNECTION, null, connx);
	}
	
	/** ���̃��f���ɐڑ������R�l�N�V���� ���f���̒ǉ� */
	public void addTargetConnection(AbstractUMLConnectionModel connx) {
		targetConnections.add(connx);
		firePropertyChange(P_TARGET_CONNECTION, null, connx);
	}
	
	/** ���̃��f����ڑ����Ƃ���R�l�N�V�����̃��X�g��Ԃ� */
	public List<AbstractUMLConnectionModel> getModelSourceConnections() {
		return sourceConnections;
	}
	
	/** ���̃��f����ڑ���Ƃ���R�l�N�V�����̃��X�g��Ԃ� */
	public List<AbstractUMLConnectionModel> getModelTargetConnections() {
		return targetConnections;
	}
	
	/** ���̃��f�����R�l�N�V�����̃\�[�X����؂藣�� */
	public void removeSourceConnection(AbstractUMLConnectionModel connx) {
		sourceConnections.remove(connx);
		firePropertyChange(P_SOURCE_CONNECTION, connx, null);
	}
	
	/** ���̃��f�����R�l�N�V�����̃^�[�Q�b�g����؂藣�� */
	public void removeTargetConnection(AbstractUMLConnectionModel connx) {
		targetConnections.remove(connx);
		firePropertyChange(P_TARGET_CONNECTION, connx, null);
	}
	
	@Override
	public void setBackgroundColor(RGB backgroundColor) {
		for (Iterator<AbstractUMLModel> iter = children.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			element.setBackgroundColor(backgroundColor);
		}
		super.setBackgroundColor(backgroundColor);
	}
	
	@Override
	public void setForegroundColor(RGB foregroundColor) {
		for (Iterator<AbstractUMLModel> iter = children.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			element.setForegroundColor(foregroundColor);
		}
		super.setForegroundColor(foregroundColor);
	}
	
	@Override
	public void setShowIcon(boolean showIcon) {
		for (Iterator<AbstractUMLModel> iter = children.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			element.setShowIcon(showIcon);
		}
		super.setShowIcon(showIcon);
	}
}
