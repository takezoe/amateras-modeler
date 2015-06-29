package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.activitydiagram.figure.FlowFigure;
import net.java.amateras.uml.activitydiagram.model.FlowModel;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

/**
 * 
 * @author Naoki Takezoe
 */
public class FlowEditpart extends AbstractUMLConnectionEditPart {
	
	private FlowFigure figure;
	
	protected IFigure createFigure() {
		figure = new FlowFigure();
		updateCondition();
		return figure;
	}

	protected void refreshVisuals() {
		super.refreshVisuals();
		updateCondition();
	}
	
	private void updateCondition(){
		FlowModel model = (FlowModel)getModel();
		String condition = model.getCondition();
		if(condition.length()==0){
			this.figure.getLabel().setText("");
		} else {
			this.figure.getLabel().setText("[" + condition + "]");
		}
	}

	protected Label getStereoTypeLabel() {
		return this.figure.getLabel();
	}
	
}
