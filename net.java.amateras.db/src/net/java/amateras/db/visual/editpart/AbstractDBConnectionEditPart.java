package net.java.amateras.db.visual.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.AbstractDBModel;

import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

public abstract class AbstractDBConnectionEditPart extends AbstractConnectionEditPart 
implements PropertyChangeListener, IDoubleClickSupport {

	protected void createEditPolicies() {
    	installEditPolicy(EditPolicy.COMPONENT_ROLE,new EntityComponentEditPolicy());
        installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,new ConnectionEndpointEditPolicy());
//		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new StereoTypeDirectEditPolicy());
	}
	
	public void activate() {
		super.activate();
		((AbstractDBModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate() {
		super.deactivate();
		((AbstractDBModel) getModel()).removePropertyChangeListener(this);
	}

	
	/** EditPolicy for Entity */
	private class EntityComponentEditPolicy extends ComponentEditPolicy {
		
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setModel((AbstractDBConnectionModel)getModel());
		    return command;
		}
		
	}
	
	/** Delete connection command  */
	private class DeleteCommand extends Command {
		
		private AbstractDBConnectionModel model;
		
		public void setModel(AbstractDBConnectionModel model){
			this.model = model;
		}
		
		public void execute() {
			model.detachSource();
			model.detachTarget();
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}
	
	public void doubleClicked(){
		
	}
	
}
