package net.java.amateras.uml.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class AbstractUMLEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {
	
	public void activate() {
		super.activate();
		((AbstractUMLModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate() {
		super.deactivate();
		((AbstractUMLModel) getModel()).removePropertyChangeListener(this);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	protected void refreshVisuals() {
		if (getFigure() instanceof PresentationFigure) {
			PresentationFigure figure = (PresentationFigure) getFigure();
			figure.updatePresentation((AbstractUMLModel) getModel());
		}
		super.refreshVisuals();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AbstractUMLModel.P_BACKGROUND_COLOR)) {
			refreshVisuals();
		} else if (evt.getPropertyName().equals(AbstractUMLModel.P_FOREGROUND_COLOR)) {
			refreshVisuals();
		} else if (evt.getPropertyName().equals(AbstractUMLModel.P_SHOW_ICON)) {
			refreshVisuals();
		}
	}
	
}
