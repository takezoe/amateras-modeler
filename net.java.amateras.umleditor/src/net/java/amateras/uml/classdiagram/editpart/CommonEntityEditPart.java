package net.java.amateras.uml.classdiagram.editpart;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Common edit part for CommonEntityModel
 * @author <a HREF="mailto:xuhuanze@qq.com">Xu Huanze(Wayne)</a>
 *
 */
public abstract class CommonEntityEditPart extends AbstractUMLEntityEditPart {

	private EntityDirectEditManager directManager;

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new ClassNameDirectEditPolicy());
	}

	protected List<AbstractUMLModel> getModelChildren() {
		CommonEntityModel model = (CommonEntityModel) getModel();
		return FilterUtil.getFilteredChildren(model);
	}

	public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		if (evt.getPropertyName().equals(CommonEntityModel.P_ENTITY_NAME)) {
			RenameUtil.rename((String) evt.getOldValue(), (String) evt.getNewValue(), (RootModel) getParent().getModel());
		} else if (evt.getPropertyName().equals(CommonEntityModel.P_CHILDREN)) {
			refreshChildren();
		} else if (evt.getPropertyName().equals(CommonEntityModel.P_FILTER)) {
			@SuppressWarnings("unchecked")
			List<EditPart> list = new ArrayList<EditPart>(getChildren());
			for (Iterator<EditPart> iter = list.iterator(); iter.hasNext();) {
				EditPart element = iter.next();
				removeChild(element);
			}
			refreshChildren();
		}
	}

	protected IFigure createFigure() {
		CommonEntityModel model = (CommonEntityModel) getModel();
		UMLClassFigure figure = getClassFigure();

		figure.setClassName(showSimpleName() ? model.getSimpleName() : model.getName());
		figure.setStereoType(model.getStereoType());

		figure.setBackgroundColor(model.getBackgroundColor());
		figure.setForegroundColor(model.getForegroundColor());

		return figure;
	}

	abstract protected UMLClassFigure getClassFigure();

	protected void refreshVisuals() {
		super.refreshVisuals();

		UMLClassFigure figure = (UMLClassFigure) getFigure();
		CommonEntityModel model = (CommonEntityModel) getModel();
		figure.setClassName(showSimpleName() ? model.getSimpleName() : model.getName());
		figure.setStereoType(model.getStereoType());
		figure.setBackgroundColor(model.getBackgroundColor());
		figure.setForegroundColor(model.getForegroundColor());
	}

	/**
	 * DirectEditPolicy
	 */
	private class ClassNameDirectEditPolicy extends DirectEditPolicy {

		protected Command getDirectEditCommand(DirectEditRequest directeditrequest) {
			ClassNameDirectEditCommand command = new ClassNameDirectEditCommand();
			command.setName((String) directeditrequest.getCellEditor().getValue());
			return command;
		}

		protected void showCurrentEditValue(DirectEditRequest directeditrequest) {

		}
	}

	private class ClassNameDirectEditCommand extends Command {

		private String oldName;

		private String newName;

		public void execute() {
			CommonEntityModel model = (CommonEntityModel) getModel();
			oldName = showSimpleName() ? model.getSimpleName() : model.getName();
			if (showSimpleName()) {
				model.setSimpleName(newName);
			} else {
				model.setName(newName);
			}
		}

		public void setName(String name) {
			newName = name;
		}

		public void undo() {
			CommonEntityModel model = (CommonEntityModel) getModel();
			if (showSimpleName()) {
				model.setSimpleName(oldName);
			} else {
				model.setName(oldName);
			}
		}
	}

	private boolean showSimpleName() {
		return UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME);
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
			directManager = new EntityDirectEditManager(this);
		}
		directManager.show();
	}

	/**
	 * DirectEditManager
	 */
	private class EntityDirectEditManager extends DirectEditManager {

		public EntityDirectEditManager(GraphicalEditPart editPart) {
			super(editPart, TextCellEditor.class, new EntityCellEditorLocator());
		}

		protected void initCellEditor() {
			CommonEntityModel model = (CommonEntityModel) getModel();
			getCellEditor().setValue(showSimpleName() ? model.getSimpleName() : model.getName());
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

			Rectangle rect = figure.getCellEditorRectangle();
			figure.translateToAbsolute(rect);
			text.setBounds(rect.x, rect.y, rect.width, rect.height);
		}
	}

}
