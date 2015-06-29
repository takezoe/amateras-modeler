package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.activitydiagram.model.ActionModel;
import net.java.amateras.uml.activitydiagram.model.ActivityModel;
import net.java.amateras.uml.activitydiagram.model.DecisionModel;
import net.java.amateras.uml.activitydiagram.model.FinalStateModel;
import net.java.amateras.uml.activitydiagram.model.FlowModel;
import net.java.amateras.uml.activitydiagram.model.ForkNodeModel;
import net.java.amateras.uml.activitydiagram.model.InitialStateModel;
import net.java.amateras.uml.activitydiagram.model.JoinNodeModel;
import net.java.amateras.uml.activitydiagram.model.ObjectModel;
import net.java.amateras.uml.activitydiagram.model.VerticalPartitionModel;
import net.java.amateras.uml.editpart.BaseUMLEditPartFactory;

import org.eclipse.gef.EditPart;

/**
 * 
 * @author Naoki Takezoe
 */
public class ActivityEditPartFactory extends BaseUMLEditPartFactory {

	protected EditPart createUMLEditPart(EditPart context, Object model) {
		EditPart part = null;
		if(model instanceof ActivityModel){
			part = new ActivityEditPart();
		} else if(model instanceof ActionModel){
			part = new ActionEditPart();
		} else if(model instanceof FlowModel){
			part = new FlowEditpart();
		} else if(model instanceof InitialStateModel){
			part = new InitialStateEditPart();
		} else if(model instanceof FinalStateModel){
			part = new FinalStateEditPart();
		} else if(model instanceof DecisionModel){
			part = new DecisionEditPart();
		} else if(model instanceof ForkNodeModel){
			part = new ForkNodeEditPart();
		} else if(model instanceof JoinNodeModel){
			part = new JoinNodeEditPart();
		} else if(model instanceof ObjectModel){
			part = new ObjectEditPart();
		} else if(model instanceof VerticalPartitionModel){
			part = new VerticalPartitionEditPart();
		}
		return part;
	}

}
