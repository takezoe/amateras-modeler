package net.java.amateras.uml.classdiagram.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.OperationLabel;
import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.editpolicies.DirectEditPolicy;
import org.eclipse.gef.requests.DirectEditRequest;
import org.eclipse.gef.requests.GroupRequest;
import org.eclipse.gef.tools.CellEditorLocator;
import org.eclipse.gef.tools.DirectEditManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;

public class OperationEditPart extends AbstractUMLEditPart {

	private OperationDirectEditManager directManager = null;

	private Font normal = null;

	private Font italic = null;

	protected IFigure createFigure() {
		OperationLabel label = new OperationLabel();
		OperationModel model = (OperationModel) getModel();

		Font font = ((AbstractGraphicalEditPart) getParent()).getFigure().getFont();
		FontData fontData = font.getFontData()[0];
		this.normal = new Font(null, fontData.getName(), fontData.getHeight(), SWT.NULL);
		this.italic = new Font(null, fontData.getName(), fontData.getHeight(), SWT.ITALIC);

		updateLabel(label, model);
		return label;
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new OperationComponentEditPolicy());
		installEditPolicy(EditPolicy.DIRECT_EDIT_ROLE, new OperationDirectEditPolicy());
	}

	public void propertyChange(PropertyChangeEvent arg0) {
		OperationLabel label = (OperationLabel) getFigure();
		OperationModel model = (OperationModel) getModel();
		updateLabel(label, model);
		super.propertyChange(arg0);
	}

	private void updateLabel(OperationLabel label, OperationModel model) {

		if (model.isShowIcon()) {
			Image iconImage = null;
			if (model.getVisibility().equals(Visibility.PUBLIC)) {
				if(model.isConstructor()){
					iconImage = UMLPlugin.getImageDescriptor("icons/const_public.gif").createImage();
				}
				else {
					iconImage = UMLPlugin.getImageDescriptor("icons/method_public.gif").createImage();
				}
			}
			else if (model.getVisibility().equals(Visibility.PRIVATE)) {
				if(model.isConstructor()){
					iconImage = UMLPlugin.getImageDescriptor("icons/const_private.gif").createImage();
				}
				else {
					iconImage = UMLPlugin.getImageDescriptor("icons/method_private.gif").createImage();
				}
			}
			else if (model.getVisibility().equals(Visibility.PROTECTED)) {
				if(model.isConstructor()){
					iconImage = UMLPlugin.getImageDescriptor("icons/const_protected.gif").createImage();
				}
				else {
					iconImage = UMLPlugin.getImageDescriptor("icons/method_protected.gif").createImage();
				}
			}
			else if (model.getVisibility().equals(Visibility.PACKAGE)) {
				if(model.isConstructor()){
					iconImage = UMLPlugin.getImageDescriptor("icons/const_default.gif").createImage();
				}
				else {
					iconImage = UMLPlugin.getImageDescriptor("icons/method_default.gif").createImage();
				}
			}
			Image iconDecorator;
			//An abstract method could not be static or final
			if (model.isAbstract()) {
				iconDecorator = UMLPlugin.getImageDescriptor("icons/abstract_co.gif").createImage();
				iconImage = addDecoratorTopRight(iconImage, iconDecorator);
			}
			else {
				if (model.isFinal()) {
					iconDecorator = UMLPlugin.getImageDescriptor("icons/final_co.gif").createImage();
					iconImage = addDecoratorTopRight(iconImage, iconDecorator);
					if (model.isStatic()) {
						iconDecorator = UMLPlugin.getImageDescriptor("icons/static_co.gif").createImage();
						iconImage = addDecoratorTopLeft(iconImage, iconDecorator);
					}
				}
				else {
					if (model.isStatic()) {
						iconDecorator = UMLPlugin.getImageDescriptor("icons/static_co.gif").createImage();
						iconImage = addDecoratorTopRight(iconImage, iconDecorator);
					}
				}
			}
			label.setIcon(iconImage);
			label.setText(getOperationText(model));
		} else {
			String visibility = " ";
			if (model.getVisibility().equals(Visibility.PUBLIC)) {
				visibility = "+";
			} else if (model.getVisibility().equals(Visibility.PRIVATE)) {
				visibility = "-";
			} else if (model.getVisibility().equals(Visibility.PROTECTED)) {
				visibility = "#";
			}
			label.setText(visibility + getOperationText(model));
		}

		if (model.isAbstract()) {
			label.setFont(italic);
		} else {
			label.setFont(normal);
		}

		label.setUnderline(model.isStatic());
	}

	private String getOperationText(OperationModel model) {
		if (showParameterName()) {
			return model.toString();
		} else {
			StringBuffer sb = new StringBuffer();
			sb.append(model.getName());
			sb.append("(");
			for (int i = 0; i < model.getParams().size(); i++) {
				if (i != 0) {
					sb.append(", ");
				}
				Argument arg = (Argument) model.getParams().get(i);
				sb.append(arg.getType());
			}
			sb.append(")");
			if(!model.isConstructor()){
				sb.append(": ");
				sb.append(model.getType());
			}
			return sb.toString();
		}

	}

	public void deactivate() {
		super.deactivate();
		normal.dispose();
		italic.dispose();
	}

	/**
	 * Operation component edit policy
	 */
	private class OperationComponentEditPolicy extends ComponentEditPolicy {
		protected Command createDeleteCommand(GroupRequest deleteRequest) {
			DeleteCommand command = new DeleteCommand();
			command.setParentModel(getHost().getParent().getModel());
			command.setTargetModel(getHost().getModel());
			return command;
		}
	}

	/**
	 * Delete command
	 *
	 */
	private class DeleteCommand extends Command {

		private AbstractUMLEntityModel parent;

		private OperationModel model;

		public void execute() {
			parent.removeChild(model);
		}

		public void setParentModel(Object root) {
			this.parent = (AbstractUMLEntityModel) root;
		}

		public void setTargetModel(Object model) {
			this.model = (OperationModel) model;
		}

		public void undo() {
			parent.addChild(model);
		}
	}

	public void performRequest(Request req) {
		if (req.getType().equals(RequestConstants.REQ_DIRECT_EDIT) || req.getType().equals(RequestConstants.REQ_OPEN)) {
			performDirectEdit();
			return;
		}
		super.performRequest(req);
	}

	private void performDirectEdit() {
		if (directManager == null) {
			directManager = new OperationDirectEditManager();
		}
		directManager.show();
	}

	/**
	 * DirectEditManager
	 */
	private class OperationDirectEditManager extends DirectEditManager {

		public OperationDirectEditManager() {
			super(OperationEditPart.this, TextCellEditor.class, new OperationCellEditorLocator());
		}

		protected void initCellEditor() {
			getCellEditor().setValue(getOperationText((OperationModel) getModel()));
			Text text = (Text) getCellEditor().getControl();
			text.selectAll();
		}
	}

	/**
	 * CellEditorLocator
	 */
	private class OperationCellEditorLocator implements CellEditorLocator {
		public void relocate(CellEditor celleditor) {
			Text text = (Text) celleditor.getControl();
			// Point pref = text.computeSize(-1, -1);
			Rectangle rect = getFigure().getBounds().getCopy();
			figure.translateToAbsolute(rect);
			if (((OperationModel) getModel()).isShowIcon()) {
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

		private List<Argument> oldParams;

		private String newName;

		private String newType;

		private List<Argument> newParams;

		public void execute() {
			OperationModel model = (OperationModel) getModel();

			oldName = model.getName();
			oldType = model.getType();
			oldParams = model.getParams();

			model.setName(newName);

			if (newType != null) {
				model.setType(newType);
			}
			if (newParams != null) {
				model.setParams(newParams);
			}
		}

		public void setName(String name) {
			newName = name;
		}

		public void setType(String type) {
			newType = type;
		}

		public void setParams(List<Argument> params) {
			newParams = params;
		}

		public void undo() {
			OperationModel model = (OperationModel) getModel();
			model.setName(oldName);
			model.setType(oldType);
			model.setParams(oldParams);
		}
	}

	/**
	 * DirectEditPolicy
	 */
	private class OperationDirectEditPolicy extends DirectEditPolicy {

		protected Command getDirectEditCommand(DirectEditRequest request) {
			DirectEditCommand command = new DirectEditCommand();
			String value = (String) request.getCellEditor().getValue();

			String values[] = value.split("\\(");
			command.setName(values[0].trim());
			if (values.length > 1) {
				String[] values2 = values[1].split("\\)");
				if(values2.length > 1){
					String[] params = values2[0].split(",");
					List<Argument> newParams = new ArrayList<Argument>();
					for (int i = 0; i < params.length; i++) {
						String param[] = params[i].split(":");
						if (!param[0].trim().equals("")) {
							Argument arg = new Argument();
							arg.setName(param[0].trim());
							if (param.length > 1) {
								arg.setType(param[1].trim());
							} else {
								arg.setType("int");
							}
							newParams.add(arg);
						}
					}
					command.setParams(newParams);

				} else {
					command.setParams(new ArrayList<Argument>());
				}

				if (values2.length > 1) {
					if (values2[1].trim().startsWith(":")) {
						command.setType(values2[1].trim().substring(1).trim());
					}
				}
			}
			return command;
		}

		protected void showCurrentEditValue(DirectEditRequest request) {
		}
	}

	private boolean showParameterName() {
		return UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME);
	}
}
