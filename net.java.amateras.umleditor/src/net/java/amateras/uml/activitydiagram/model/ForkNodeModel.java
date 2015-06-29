package net.java.amateras.uml.activitydiagram.model;

import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author Naoki Takezoe
 */
public class ForkNodeModel extends AbstractUMLEntityModel implements ICloneableModel {
	public Object clone(){
		ForkNodeModel model = new ForkNodeModel();
		model.setParent(getParent());
		model.setConstraint(new Rectangle(getConstraint()));
		return model;
	}
}
