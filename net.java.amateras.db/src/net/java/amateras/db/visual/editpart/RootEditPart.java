package net.java.amateras.db.visual.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.NoteModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;

public class RootEditPart extends AbstractDBEditPart {

	private Set<Class<? extends AbstractDBEntityModel>> RESIZABLE = null;
	{
		RESIZABLE = new HashSet<Class<? extends AbstractDBEntityModel>>();
		RESIZABLE.add(NoteModel.class);
	}

	protected IFigure createFigure() {
		Layer figure = new Layer();
		figure.setLayoutManager(new XYLayout());
		return figure;
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new RootEditPolicy());
	}

	protected List<AbstractDBEntityModel> getModelChildren() {
		return ((RootModel)getModel()).getChildren();
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RootModel.P_CHILDREN)) {
			refreshChildren();
		}
		if (evt.getPropertyName().equals(RootModel.P_FONT)) {
			@SuppressWarnings("unchecked")
			List<AbstractDBEntityEditPart> children = getChildren();
			for(AbstractDBEntityEditPart part: children){
				part.refresh();
				for(Object conn: part.getSourceConnections()){
					((AbstractDBConnectionEditPart) conn).refresh();
				}
			}
		}
		if (evt.getPropertyName().equals(RootModel.P_MODE)) {
			@SuppressWarnings("unchecked")
			List<AbstractDBEntityEditPart> children = getChildren();
			for(AbstractDBEntityEditPart part: children){
				part.refresh();
				@SuppressWarnings("unchecked")
				List<AbstractDBConnectionEditPart> conns = part.getSourceConnections();
				for(AbstractDBConnectionEditPart conn: conns){
					conn.refresh();
				}
			}
		}
	}

	/** EditPolicy for layout */
	private class RootEditPolicy extends XYLayoutEditPolicy {

		protected EditPolicy createChildEditPolicy(EditPart child) {
			if (RESIZABLE.contains(child.getModel().getClass())) {
				return new ResizableEditPolicy();
			} else {
				return new NonResizableEditPolicy();
			}
		}

		protected Command createAddCommand(EditPart child, Object constraint) {
			return null;
		}

		protected Command createChangeConstraintCommand(EditPart child,Object constraint) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			command.setModel((AbstractDBEntityModel)child.getModel());
			command.setConstraint((Rectangle)constraint);
			return command;
		}

		protected Command getCreateCommand(CreateRequest request) {
			CreateCommand command = new CreateCommand();
			Rectangle constraint = (Rectangle) getConstraintFor(request);
			AbstractDBEntityModel model = (AbstractDBEntityModel) request.getNewObject();
			if (!RESIZABLE.contains(model.getClass())) {
				constraint.width = -1;
				constraint.height = -1;
			}
			model.setConstraint(constraint);

			command.setRootModel(getHost().getModel());
			command.setModel(model);
			return command;
		}

		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}
	}

	/** Change constraint command */
	private class ChangeConstraintCommand extends Command {

		private AbstractDBEntityModel model;
		private Rectangle constraint;
		private Rectangle oldConstraint;

		public void execute(){
			model.setConstraint(constraint);
		}

		public void setConstraint(Rectangle constraint){
			this.constraint = constraint;
		}

		public void setModel(AbstractDBEntityModel model){
			this.model = model;
			oldConstraint = model.getConstraint();
		}

		public void undo() {
			model.setConstraint(oldConstraint);
		}
	}

	/** Creation model command */
	private class CreateCommand extends Command {

		private RootModel root;
		private AbstractDBEntityModel model;

		public void execute() {
			root.addChild(model);
		}

		public void setRootModel(Object root) {
			this.root = (RootModel)root;
		}

		public void setModel(Object model) {
			this.model = (AbstractDBEntityModel) model;
			if(this.model instanceof TableModel){
				((TableModel)this.model).setTableName("TABLE_" + (root.getChildren().size()+1));
				((TableModel)this.model).setLogicalName(DBPlugin.getResourceString("label.table") + (root.getChildren().size()+1));
			}
		}

		public void undo() {
			root.removeChild(model);
		}
	}

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List<SnapToHelper> snapStrategies = new ArrayList<SnapToHelper>();
			Boolean val = (Boolean)getViewer().getProperty(RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGuides(this));
			val = (Boolean)getViewer().getProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGeometry(this));
			val = (Boolean)getViewer().getProperty(SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGrid(this));

			if (snapStrategies.size() == 0)
				return null;
			if (snapStrategies.size() == 1)
				return (SnapToHelper)snapStrategies.get(0);

			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++)
				ss[i] = (SnapToHelper)snapStrategies.get(i);
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}
}

