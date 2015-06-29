package net.java.amateras.db.visual.editpart.tree;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.AbstractDBModel;

import org.eclipse.gef.editparts.AbstractTreeEditPart;

public class AbstractDBTreeEditPart extends AbstractTreeEditPart implements
		PropertyChangeListener {

	public void activate() {
		super.activate();
		if(getModel() instanceof AbstractDBModel){
			((AbstractDBModel) getModel()).addPropertyChangeListener(this);
		}
	}

    public void deactivate() {
        super.deactivate();
		if(getModel() instanceof AbstractDBModel){
			((AbstractDBModel) getModel()).removePropertyChangeListener(this);
		}
    }

	public void propertyChange(PropertyChangeEvent evt) {
		String propName = evt.getPropertyName();
	    if (AbstractDBEntityModel.P_SOURCE_CONNECTION.equals(propName)) { 
	        refreshChildren();
	    }
	}

}
