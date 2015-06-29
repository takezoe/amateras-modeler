package net.java.amateras.uml.classdiagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.classdiagram.figure.CompositeConnectionFigure;
import net.java.amateras.uml.classdiagram.model.CompositeModel;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class CompositeEditPart extends AbstractUMLConnectionEditPart {
	
	protected IFigure createFigure() {
		CompositeModel model = (CompositeModel)getModel();
		return new CompositeConnectionFigure(model);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		CompositeModel model = (CompositeModel)getModel();
		((CompositeConnectionFigure) getFigure()).update(model);
		super.propertyChange(evt);
	}

	protected Label getStereoTypeLabel() {
		return ((CompositeConnectionFigure)getFigure()).getStereoTypeLabel();
	}
	
}
