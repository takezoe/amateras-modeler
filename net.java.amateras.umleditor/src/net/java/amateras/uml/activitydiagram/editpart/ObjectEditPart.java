package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.activitydiagram.figure.ActivityFigureFactory;
import net.java.amateras.uml.activitydiagram.figure.ObjectFigure;
import net.java.amateras.uml.activitydiagram.model.ObjectModel;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;

import org.eclipse.draw2d.IFigure;

/**
 * 
 * @author Naoki Takezoe
 */
public class ObjectEditPart extends AbstractUMLEntityEditPart {
	
	protected IFigure createFigure() {
		ObjectModel model = (ObjectModel) getModel();
		
		ObjectFigure figure = ActivityFigureFactory.getObjectFigure();
		figure.updatePresentation(model);
		
		return figure;
	}
	
	protected void refreshVisuals() {
		super.refreshVisuals();
		ObjectModel model = (ObjectModel) getModel();
		ObjectFigure figure = (ObjectFigure)getFigure();
		figure.updatePresentation(model);
	}

}
