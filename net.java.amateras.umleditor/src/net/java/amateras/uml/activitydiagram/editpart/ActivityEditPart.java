package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.activitydiagram.model.VerticalPartitionModel;
import net.java.amateras.uml.editpart.RootEditPart;

/**
 * 
 * @author Naoki Takezoe
 */
public class ActivityEditPart extends RootEditPart {
	
	public ActivityEditPart() {
		addResizableClass(VerticalPartitionModel.class);
	}
	
}
