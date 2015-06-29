/**
 * 
 */
package net.java.amateras.uml.editpart;

import java.beans.PropertyChangeEvent;

import org.eclipse.draw2d.IFigure;

import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.model.EntityModel;

/**
 * @author shida
 *
 */
public abstract class NamedEntityEditPart extends AbstractUMLEntityEditPart {

	protected IFigure createFigure() {
		EntityModel model = (EntityModel) getModel();
		EntityFigure figure = (EntityFigure) createEntityFigure();
		figure.getLabel().setText(model.getName());
		return figure;
	}
	
	protected abstract EntityFigure createEntityFigure();
	/*
	 * (”ñ Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(EntityModel.P_ENTITY_NAME)) {
			refreshName();
		}
		super.propertyChange(evt);
	}

	private void refreshName() {
		EntityModel model = (EntityModel) getModel();
		EntityFigure figure = (EntityFigure) getFigure();
		figure.getLabel().setText(model.getName());
	}
}
