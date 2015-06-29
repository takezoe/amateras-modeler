package net.java.amateras.uml.classdiagram.editpart;

import java.beans.PropertyChangeEvent;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.AttributeLabel;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.widgets.Text;

public class AttributeEditPart extends AbstractUMLEditPart {

	private AttributeDirectEditManager directManager = null;

	protected IFigure createFigure() {
		AttributeLabel label = new AttributeLabel();
		AttributeModel model = (AttributeModel) getModel();
		updateLabel(label, model);
		return label;
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new AttributeComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE,
				new AttributeDirectEditPolicy());
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		AttributeLabel label = (AttributeLabel) getFigure();
		AttributeModel model = (AttributeModel) getModel();
		updateLabel(label, model);
		super.propertyChange(arg0);
	}

	private void updateLabel(AttributeLabel label, AttributeModel model) {
		
		if (model.isShowIcon()) {
			if (model.getVisibility().equals(Visibility.PUBLIC)) {
				label.setIcon(UMLPlugin.getImageDescriptor(
						"icons/field_public.gif").createImage());
			} else if (model.getVisibility().equals(Visibility.PRIVATE)) {
				label.setIcon(UMLPlugin.getImageDescriptor(
						"icons/field_private.gif").createImage());
			} else if (model.getVisibility().equals(Visibility.PROTECTED)) {
				label.setIcon(UMLPlugin.getImageDescriptor(
						"icons/field_protected.gif").createImage());
			} else if (model.getVisibility().equals(Visibility.PACKAGE)) {
				label.setIcon(UMLPlugin.getImageDescriptor(
						"icons/field_default.gif").createImage());
			}
			label.setText(model.toString());
		} else {
			String visibility = " ";
			if (model.getVisibility().equals(Visibility.PUBLIC)) {
				visibility = "+";
			} else if (model.getVisibility().equals(Visibility.PRIVATE)) {
				visibility = "-";
			} else if (model.getVisibility().equals(Visibility.PROTECTED)) {
				visibility = "#";
			}
			label.setText(visibility + model.toString());
		}
		label.setUnderline(model.isStatic());
	}

	/** アトリビュートのエディットポリシー */
	private class AttributeComponentEditPolicy extends ComponentEditPolicy {
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setParentModel(getHost().getParent().getModel());
			command.setTargetModel(getHost().getModel());
			return command;
		}
	}

	/** 削除コマンド */
	private class DeleteCommand extends Command {

		private AbstractUMLEntityModel parent;

		private AttributeModel model;

		public void execute() {
			parent.removeChild(model);
		}

		public void setParentModel(Object root) {
			this.parent = (AbstractUMLEntityModel) root;
		}

		public void setTargetModel(Object model) {
			this.model = (AttributeModel) model;
		}

		public void undo() {
			parent.addChild(model);
		}
	}

	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT)
				|| req.getType().equals(RequestConstants.REQ_OPEN)) {
			performDirectEdit();
			return;
		}
		super.performRequest(req);
	}

	private void performDirectEdit() {
		if (directManager == null) {
			directManager = new AttributeDirectEditManager();
		}
		directManager.show();
	}

	/**
	 * DirectEditManager
	 */
	private class AttributeDirectEditManager extends DirectEditManager {

		public AttributeDirectEditManager() {
			super(AttributeEditPart.this, TextCellEditor.class,
					new AttributeCellEditorLocator());
		}

		protected void initCellEditor() {
			getCellEditor().setValue(((AttributeModel) getModel()).toString());
			Text text = (Text) getCellEditor().getControl();
			text.selectAll();
		}
	}

	/**
	 * CellEditorLocator
	 */
	private class AttributeCellEditorLocator implements CellEditorLocator {
		public void relocate(CellEditor celleditor) {
			Text text = (Text) celleditor.getControl();
			// Point pref = text.computeSize(-1, -1);
			Rectangle rect = getFigure().getBounds().getCopy();
			figure.translateToAbsolute(rect);
			if (((AttributeModel) getModel()).isShowIcon()) {
				text.setBounds(rect.x + 16, rect.y, rect.width - 16, rect.height);
			} else {
				text.setBounds(rect.x, rect.y, rect.width, rect.height);
			}
		}
	}

	/**
	 * DirectEditCommand
	 */
	private class DirectEditCommand extends Command {

		private String oldName;

		private String oldType;

		private String newName;

		private String newType;

		public void execute() {
			AttributeModel model = (AttributeModel) getModel();
			oldName = model.getName();
			oldType = model.getType();

			model.setName(newName);
			if (newType != null) {
				model.setType(newType);
			}
		}

		public void setName(String name) {
			newName = name;
		}

		public void setType(String type) {
			newType = type;
		}

		public void undo() {
			AttributeModel model = (AttributeModel) getModel();
			model.setName(oldName);
			model.setName(oldType);
		}
	}

	/**
	 * DirectEditPolicy
	 */
	private class AttributeDirectEditPolicy extends DirectEditPolicy {

		protected Command getDirectEditCommand(DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			String value = (String) request.getCellEditor().getValue();
			String[] values = value.split(":");
			command.setName(values[0].trim());
			if (values.length > 1) {
				command.setType(values[1].trim());
			}
			return command;
		}

		protected void showCurrentEditValue(DirectEditRequest request) {
		}
	}
}
