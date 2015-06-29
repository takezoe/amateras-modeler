package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.activitydiagram.figure.ActionFigure;
import net.java.amateras.uml.activitydiagram.figure.ActivityFigureFactory;
import net.java.amateras.uml.activitydiagram.model.ActionModel;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;

import org.eclipse.draw2d.IFigure;

/**
 * 
 * @author Naoki Takezoe
 */
public class ActionEditPart extends AbstractUMLEntityEditPart {
	
	protected IFigure createFigure() {
		ActionModel model = (ActionModel) getModel();
		
		ActionFigure figure = ActivityFigureFactory.getActionFigure();
		figure.updatePresentation(model);
		
		return figure;
	}
	
	protected void refreshVisuals() {
		super.refreshVisuals();
		ActionModel model = (ActionModel) getModel();
		ActionFigure figure = (ActionFigure)getFigure();
		figure.updatePresentation(model);
	}

}
