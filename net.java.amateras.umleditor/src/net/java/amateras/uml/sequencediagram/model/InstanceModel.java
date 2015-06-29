/**
 * 
 */
package net.java.amateras.uml.sequencediagram.model;

import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.TypeEntityModel;
import net.java.amateras.uml.sequencediagram.property.TypePropertyDescriptor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * インスタンスを示すモデルオブジェクト.
 * 
 * @author Takahiro Shida.
 */
public class InstanceModel extends MessageAcceptableModel implements
		EntityModel {

	public static final String P_NAME = "_instance_name";

	public static final String P_TYPE = "_type";

	public static final int DEFAULT_LOCATION = 20;

	private String name = "";

	private TypeEntityModel type;

	private static int number = 1;

	private LifeLineModel lifeLine;

	private InteractionModel root;

	private SyncMessageModel creator;

	private Rectangle oldRectangle;

	private ActivationModel active;

	public ActivationModel getActive() {
		return active;
	}

	public void setActive(ActivationModel active) {
		this.active = active;
	}

	public InstanceModel() {
		setName("Instance" + number);
		number++;
		lifeLine = new LifeLineModel(this);
		copyPresentation(lifeLine);
		addChild(lifeLine);
	}

	public void setName(String name) {
		String oldName = this.name;
		this.name = name;
		firePropertyChange(P_NAME, oldName, name);
	}

	public String getName() {
		return this.name;
	}

	public void setType(TypeEntityModel type) {
		EntityModel old = this.type;
		this.type = type;
		firePropertyChange(P_TYPE, old, type);
	}

	public TypeEntityModel getType() {
		return type;
	}

	public LifeLineModel getModel() {
		return lifeLine;
	}

	public void setRoot(InteractionModel root) {
		this.root = root;
		root.addLifeLine(this.getModel());

	}

	public InteractionModel getRoot() {
		return root;
	}

	public SyncMessageModel getCreator() {
		return creator;
	}

	public void setCreator(SyncMessageModel creator) {
		this.creator = creator;
	}

	public void setConstraint(Rectangle constraint) {
		Rectangle old = getConstraint();
		if (old != null) {
			List connections = getModelTargetConnections();
			for (Iterator iter = connections.iterator(); iter.hasNext();) {
				SyncMessageModel element = (SyncMessageModel) iter.next();
				if (element.getSource().getConstraint().y > constraint.y) {
					constraint.y = element.getSource().getConstraint().y;
				}
			}
			Rectangle delta = new Rectangle();
			delta.x = constraint.x - old.x;
			delta.y = constraint.y - old.y;
			delta.width = constraint.width - old.width;
			delta.height = 0;
			if (active != null) {
				Rectangle ac = active.getConstraint().getCopy();
				ac.translate(0, delta.y);
				active.setConstraint(ac);
			}
			lifeLine.adjustLocation(delta);

		}
		super.setConstraint(constraint);
		List connections = getModelTargetConnections();
		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			SyncMessageModel element = (SyncMessageModel) iter.next();
			element.updateCaller(getConstraint().y + getConstraint().height
					+ 20);
		}
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new TextPropertyDescriptor(P_NAME, UMLPlugin.getDefault()
						.getResourceString("property.name")),
				new TypePropertyDescriptor(P_TYPE, UMLPlugin.getDefault()
						.getResourceString("property.type")),
				new ColorPropertyDescriptor(P_BACKGROUND_COLOR, UMLPlugin.getDefault().getResourceString("property.background"))};
	}

	public Object getPropertyValue(Object id) {
		if (id.equals(P_NAME)) {
			return getName();
		} else if (id.equals(P_TYPE)) {
			return getType();
		}
		return super.getPropertyValue(id);
	}

	public boolean isPropertySet(Object id) {
		if (id.equals(P_NAME)) {
			return true;
		} else if (id.equals(P_TYPE)) {
			return true;
		}
		return super.isPropertySet(id);
	}

	public void setPropertyValue(Object id, Object value) {
		if (id.equals(P_NAME)) {
			setName((String) value);
		} else if (id.equals(P_TYPE)) {
			setType((TypeEntityModel) value);
		}
		super.setPropertyValue(id, value);
	}

	public void updateLocation(Rectangle delta) {
		Rectangle rectangle = getConstraint();
		if (rectangle != null) {
			rectangle = rectangle.getCopy();
			rectangle.translate(0, delta.y);
			super.setConstraint(rectangle);
			if (active != null) {
				Rectangle ac = active.getConstraint().getCopy();
				ac.translate(0, delta.y);
				active.setConstraint(ac);
			}
			lifeLine.adjustLocationWithNoEffect(delta);
		}
	}

	public void computeCaller(int size) {
		if (active != null) {
			active.computeCaller(size);
		}
	}

	public int getCalleeSize() {
		if (active != null) {
			int size = active.getCalleeSize();
			if (size > getConstraint().y + getConstraint().height + 40) {
				return size;
			}
		}
		return getConstraint().y + getConstraint().height + 40;
	}

	public ActivationModel getOwner() {
		return null;
	}

	public int computeChild() {
		return 0;
	}

	public void computeCaller() {
		if (getModelTargetConnections().isEmpty()) {
			oldRectangle = getConstraint().getCopy();
			Rectangle rectangle = getConstraint().getCopy();
			rectangle.y = InstanceModel.DEFAULT_LOCATION;
			setConstraint(rectangle);
		} else {
			setConstraint(oldRectangle);
			int size = getCalleeSize();
			SyncMessageModel element = (SyncMessageModel) getModelTargetConnections()
					.get(0);
			element.updateCaller(size);
		}
	}

}
