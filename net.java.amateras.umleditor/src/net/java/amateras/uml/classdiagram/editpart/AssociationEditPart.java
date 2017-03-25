package net.java.amateras.uml.classdiagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.classdiagram.figure.AssociationConnectionFigure;
import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class AssociationEditPart extends AbstractUMLConnectionEditPart {
	
	@Override
	protected IFigure createFigure() {
		AssociationModel model = (AssociationModel)getModel();
		return new AssociationConnectionFigure(model);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		AssociationModel model = (AssociationModel)getModel();
		((AssociationConnectionFigure) getFigure()).update(model);
		super.propertyChange(evt);
	}

	@Override
	protected Label getStereoTypeLabel() {
		return ((AssociationConnectionFigure) getFigure()).getStereoTypeLabel();
	}

}
