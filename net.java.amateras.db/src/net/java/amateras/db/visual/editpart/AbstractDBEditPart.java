package net.java.amateras.db.visual.editpart;

import java.beans.PropertyChangeListener;

import net.java.amateras.db.visual.model.AbstractDBModel;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;

public abstract class AbstractDBEditPart extends AbstractGraphicalEditPart 
implements PropertyChangeListener, IDoubleClickSupport {

	public void activate() {
		super.activate();
		((AbstractDBModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate() {
		super.deactivate();
		((AbstractDBModel) getModel()).removePropertyChangeListener(this);
	}
	
	public void doubleClicked(){
		
	}

}
