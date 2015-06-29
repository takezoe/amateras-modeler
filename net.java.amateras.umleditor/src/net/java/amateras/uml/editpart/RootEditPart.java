package net.java.amateras.uml.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.NoteModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.ConnectionRouter;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
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
import org.eclipse.swt.SWT;

public class RootEditPart extends AbstractUMLEditPart {
	
	private Set RESIZABLE = null;
	{
		RESIZABLE = new HashSet();
		RESIZABLE.add(NoteModel.class);
	}
	protected void addResizableClass(Class c) {
		RESIZABLE.add(c);
	}
	
	protected IFigure createFigure() {
		Layer figure = new Layer() {
			public void paint(Graphics graphics) {
				if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
					graphics.setAntialias(SWT.ON);
					graphics.setTextAntialias(SWT.ON);
				}
				super.paint(graphics);
			}
		};
		figure.setLayoutManager(new XYLayout());
		
		ConnectionLayer layer = (ConnectionLayer) getLayer(LayerConstants.CONNECTION_LAYER);
//		if ("manhattan".equals(UMLPlugin.getDefault().getConnectionRouter())) {
//			layer.setConnectionRouter(new ManhattanConnectionRouter());
//		} else if ("fan".equals(UMLPlugin.getDefault().getConnectionRouter())) {
//			FanRouter router = new FanRouter();
//			router.setSeparation(40);
//			layer.setConnectionRouter(router);
//		} else if ("shortest".equals(UMLPlugin.getDefault().getConnectionRouter())) {
//			ShortestPathConnectionRouter router = new ShortestPathConnectionRouter(figure);
//			layer.setConnectionRouter(router);
//		} else {
			layer.setConnectionRouter(ConnectionRouter.NULL);
//		}
		return figure;
	}
	
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new RootEditPolicy());
	}
	
	protected List getModelChildren() {
		return ((RootModel)getModel()).getChildren();
//		ArrayList models = new ArrayList();
//		for(int i=0;i<list.size();i++){
//			Object obj = list.get(i);
//			if(obj instanceof AbstractUMLModel){
//				models.add(obj);
//			}
//		}
//		return models;
	}
	
//	protected void refreshVisuals(){
//		List list = getChildren();
//		for(int i=0;i<list.size();i++){
//			Object obj = list.get(i);
//			if(obj instanceof PageEditPart){
//				((PageEditPart)obj).refreshVisuals();
//			}
//		}
//	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RootModel.P_CHILDREN)) {
			refreshChildren();
		}
	}
	
	/** エディットポリシー */
	private class RootEditPolicy extends XYLayoutEditPolicy {

		protected EditPolicy createChildEditPolicy(EditPart child) {
			if (RESIZABLE.contains(child.getModel().getClass())) {
				return new ResizableEditPolicy();
			} else {
				return new NonResizableEditPolicy();
			}
		}
		
		protected Command createAddCommand(EditPart child, Object constraint) {
			if (!(child.getModel() instanceof AbstractUMLEntityModel)) {
				return null;
			}
			CreateAddCommand command = new CreateAddCommand();
			command.setModel((AbstractUMLEntityModel) child.getModel());
			command.setTarget((RootModel) getHost().getModel());
			
			ChangeConstraintCommand nextCommand = new ChangeConstraintCommand();
			nextCommand.setModel((AbstractUMLEntityModel)child.getModel());
			nextCommand.setConstraint((Rectangle)constraint);
			
			return command.chain(nextCommand);
		}
		
		protected Command createChangeConstraintCommand(EditPart child,Object constraint) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			command.setModel((AbstractUMLEntityModel)child.getModel());
			command.setConstraint((Rectangle)constraint);
			return command;
		}
		
		protected Command getCreateCommand(CreateRequest request) {
			CreateCommand command = new CreateCommand();
			Rectangle constraint = (Rectangle) getConstraintFor(request);
			AbstractUMLEntityModel model = (AbstractUMLEntityModel) request.getNewObject();
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
	
	/** 制約の変更コマンド */
	private class ChangeConstraintCommand extends Command {
		
		private AbstractUMLEntityModel model;
		private Rectangle constraint;
		private Rectangle oldConstraint;
		
		public void execute(){
			model.setConstraint(constraint);
		}
		
		public void setConstraint(Rectangle constraint){
			this.constraint = constraint;
		}
		
		public void setModel(AbstractUMLEntityModel model){
			this.model = model;
			oldConstraint = model.getConstraint();
		}
		
		public void undo() {
			model.setConstraint(oldConstraint);
		}
	}
	
	/** モデルの新規作成コマンド */
	private class CreateCommand extends Command {
		
		private RootModel root;
		private AbstractUMLEntityModel model;
		
		public void execute() {
			root.copyFilter(model);
			root.copyPresentation(model);
			root.addChild(model);
		}
		
		public void setRootModel(Object root) {
			this.root = (RootModel)root;
		}
		
		public void setModel(Object model) {
			this.model = (AbstractUMLEntityModel) model;
		}
		
		public void undo() {
			root.removeChild(model);
		}
	}

	private class CreateAddCommand extends Command {
		private RootModel target;
		private AbstractUMLEntityModel model;
		private AbstractUMLEntityModel container;
		
		public void execute() {
			this.container = model.getParent();
			container.removeChild(model);
			target.addChild(model);
		}
		
		public void undo() {
			target.removeChild(model);
			container.addChild(model);
		}
		public void setTarget(RootModel target) {
			this.target = target;
		}
		
		public void setModel(AbstractUMLEntityModel model) {
			this.model = model;
		}
	}
	/**
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();
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
