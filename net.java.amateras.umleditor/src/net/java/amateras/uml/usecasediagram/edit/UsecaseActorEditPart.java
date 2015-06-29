/**
 * 
 */
package net.java.amateras.uml.usecasediagram.edit;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.editpart.NamedEntityEditPart;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.usecasediagram.figure.UsecaseActorFigure;
import net.java.amateras.uml.usecasediagram.model.UsecaseActorModel;

/**
 * @author Takahiro Shida.
 *
 */
public class UsecaseActorEditPart extends NamedEntityEditPart {

	protected EntityFigure createEntityFigure() {
		UsecaseActorModel model = (UsecaseActorModel) getModel();
		return new UsecaseActorFigure(model);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(UsecaseActorModel.P_IMAGE)) {
			refleshImage();
		}
	}

	private void refleshImage() {
		UsecaseActorModel model = (UsecaseActorModel) getModel();
		UsecaseActorFigure figure = (UsecaseActorFigure) getFigure();
		figure.setImage(model);
	}
}
