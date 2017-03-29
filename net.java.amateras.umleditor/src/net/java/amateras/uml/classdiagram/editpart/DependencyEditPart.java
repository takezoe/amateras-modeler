package net.java.amateras.uml.classdiagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.classdiagram.figure.DependencyConnectionFigure;
import net.java.amateras.uml.classdiagram.model.DependencyModel;
import net.java.amateras.uml.editpart.AbstractUMLConnectionEditPart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;

public class DependencyEditPart extends AbstractUMLConnectionEditPart {

	@Override
	protected IFigure createFigure() {
		DependencyModel model = (DependencyModel)getModel();
		return new DependencyConnectionFigure(model);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		DependencyModel model = (DependencyModel)getModel();
		((DependencyConnectionFigure) getFigure()).update(model);
		refreshVisuals();
	}
	
	@Override
	protected Label getStereoTypeLabel() {
		return ((DependencyConnectionFigure) getFigure()).getStereoTypeLabel();
	}

}
