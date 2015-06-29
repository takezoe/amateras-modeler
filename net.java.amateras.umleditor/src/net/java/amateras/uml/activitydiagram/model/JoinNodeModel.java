package net.java.amateras.uml.activitydiagram.model;

import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.draw2d.geometry.Rectangle;

public class JoinNodeModel extends AbstractUMLEntityModel implements ICloneableModel {
	public Object clone(){
		JoinNodeModel model = new JoinNodeModel();
		model.setParent(getParent());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
