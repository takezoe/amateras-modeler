/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.sequencediagram.model.MessageModel;
import net.java.amateras.uml.sequencediagram.property.MessageTextCellEditor;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractConnectionEditPart;
import org.eclipse.gef.editpolicies.ConnectionEndpointEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.swt.widgets.Text;

/**
 * @author Takahiro Shida.
 * 
 */
public class MessageEditPart extends AbstractConnectionEditPart implements PropertyChangeListener {

	private EntityDirectEditManager directManager = null;

	public void activate() {
		super.activate();
		((AbstractUMLModel) getModel()).addPropertyChangeListener(this);
	}

	public void deactivate() {
		super.deactivate();
		((AbstractUMLModel) getModel()).removePropertyChangeListener(this);
	}

	/*
	 * (”ñ Javadoc)
	 * 
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ConnectionEndpointEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new EntityDirectEditPolicy());
	}

	/*
	 * (”ñ Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		refreshVisuals();
	}

	protected void refreshVisuals() {
		if (getFigure() instanceof PresentationFigure) {
			PresentationFigure figure = (PresentationFigure) getFigure();
			figure.updatePresentation((AbstractUMLModel) getModel());
		}
		super.refreshVisuals();
	}
	
	public void performRequest(Request req) {
		if (getModel() instanceof EntityModel) {
			if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT) || req.getType().equals(RequestConstants.REQ_OPEN)) {
				performDirectEdit();
				return;
			}
		}
		super.performRequest(req);
	}

	private void performDirectEdit() {
		if (directManager == null) {
			directManager = new EntityDirectEditManager();
		}
		directManager.show();
	}

	/**
	 * DirectEditManager
	 */
	private class EntityDirectEditManager extends DirectEditManager {

		public EntityDirectEditManager() {
			super(MessageEditPart.this, MessageTextCellEditor.class, new EntityCellEditorLocator());
		}

		protected void initCellEditor() {
			MessageTextCellEditor cellEditor = (MessageTextCellEditor) getCellEditor();
			MessageModel model = (MessageModel) getModel();
			cellEditor.setUMLModel(model.getProporsal());
			getCellEditor().setValue(model.getName());
			Text text = (Text) getCellEditor().getControl();
			text.selectAll();
		}
	}

	/**
	 * CellEditorLocator
	 */
	private class EntityCellEditorLocator implements CellEditorLocator {
		public void relocate(CellEditor celleditor) {
			EntityFigure figure = (EntityFigure) getFigure();
			Text text = (Text) celleditor.getControl();
			// Point pref = text.computeSize(-1, -1);
			Rectangle rect = figure.getCellEditorRectangle();
			figure.translateToAbsolute(rect);
			text.setBounds(rect.x,rect.y,rect.width,rect.height);
		}
	}

	/**
	 * DirectEditCommand
	 */
	private class DirectEditCommand extends Command {

		private String oldName;

		private String newName;

		public void execute() {
			EntityModel model = (EntityModel) getModel();
			oldName = model.getName();
			model.setName(newName);
		}

		public void setName(String name) {
			newName = name;
		}

		public void undo() {
			EntityModel model = (EntityModel) getModel();
			model.setName(oldName);
		}
	}

	/**
	 * DirectEditPolicy
	 */
	private class EntityDirectEditPolicy extends DirectEditPolicy {

		protected Command getDirectEditCommand(DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			command.setName((String) request.getCellEditor().getValue());
			return command;
		}

		protected void showCurrentEditValue(DirectEditRequest request) {
		}
	}
}
