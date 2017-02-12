package net.java.amateras.uml.classdiagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.classdiagram.figure.AggregationConnectionFigure;
import net.java.amateras.uml.classdiagram.model.AggregationModel;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class AggregationEditPart extends AbstractUMLConnectionEditPart {
	
	@Override
	protected IFigure createFigure() {
		AggregationModel model = (AggregationModel)getModel();
		return new AggregationConnectionFigure(model);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		AggregationModel model = (AggregationModel)getModel();
		((AggregationConnectionFigure) getFigure()).update(model);
		super.propertyChange(evt);
	}

	@Override
	protected Label getStereoTypeLabel() {
		return ((AggregationConnectionFigure) getFigure()).getStereoTypeLabel();
	}

}
