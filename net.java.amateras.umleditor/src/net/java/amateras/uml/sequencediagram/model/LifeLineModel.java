/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import java.util.Iterator;

import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

/**
 * 生存線を示すモデルオブジェクト.
 * 
 * @author Takahiro Shida.
 */
public class LifeLineModel extends AbstractUMLEntityModel {
	
	public static final String P_CHILDREN = "_children";
	
	public static final int DEFAULT_HEIGHT = 200;
	
	public static final String P_ADJUST = "_adjust";
	
	private final InstanceModel owner;
	
	private Rectangle old;
	
	public LifeLineModel(InstanceModel owner) {
		this.owner = owner;
	}
	
	public InstanceModel getOwner() {
		return owner;
	}
	
	public void addActivation(ActivationModel model) {
		addChild(model);
		this.owner.getRoot().addActivation(model);
		model.setOwnerLine(this);
		firePropertyChange(P_CHILDREN,null,model);
	}
	
	public void removeActivation(ActivationModel model) {
		removeChild(model);
		this.owner.getRoot().removeActivation(model);
		model.setOwnerLine((LifeLineModel) null);
	}
	public void adjustLocation(Rectangle delta) {
		Rectangle rectangle = getConstraint();
		if (rectangle != null) {
			rectangle.translate(delta.getLocation());
			rectangle.translate(delta.width / 2, 0);
			setConstraint(rectangle);
		}
		delta.y = 0;
		for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
			ActivationModel element = (ActivationModel) iter.next();
			element.adjustLocation(delta);
		}
	}
	
	public void adjustLocationWithNoEffect(Rectangle delta) {
		Rectangle rectangle = getConstraint();
		if (rectangle != null) {
			rectangle.translate(delta.getLocation());
			rectangle.translate(delta.width / 2, 0);
			setConstraint(rectangle);
		}		
	}
	
	public void computeSize() {
		Rectangle rectangle = getConstraint();
		if (rectangle != null) {
			int max = 0;
			for (Iterator iter = getChildren().iterator(); iter.hasNext();) {
				ActivationModel element = (ActivationModel) iter.next();
				int cmax = element.getConstraint().y + element.getConstraint().height;
				if (max < cmax) {
					max = cmax;
				}
			}
			if (max > DEFAULT_HEIGHT) {
				this.old = rectangle.getCopy();
				rectangle.height = max - rectangle.y + 10;
				setConstraint(rectangle);
			}
		}
	}

	public void undoSize() {
		if (old != null) {
			setConstraint(old);
		}
	}
	
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[]{};
	}
}
