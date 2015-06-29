/**
 * 
 */
package net.java.amateras.uml.activitydiagram.model;

import java.util.List;

import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

/**
 * @author Takahiro Shida.
 *
 */
public class ActivityModel extends RootModel {

	public void addChild(AbstractUMLModel model) {
		List children = getChildren();
		model.setParent(this);
		if(model instanceof VerticalPartitionModel){
			children.add(0, model);
		} else {
			children.add(model);
		}
		firePropertyChange(P_CHILDREN,null,model);
	}

}
