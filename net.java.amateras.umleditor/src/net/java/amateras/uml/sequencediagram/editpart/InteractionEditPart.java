/**
 * 
 */
package net.java.amateras.uml.sequencediagram.editpart;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.AbstractUMLEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.NoteModel;
import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.ActorModel;
import net.java.amateras.uml.sequencediagram.model.FragmentModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.InteractionModel;
import net.java.amateras.uml.sequencediagram.model.LifeLineModel;
import net.java.amateras.uml.sequencediagram.model.MessageOrderUtil;

import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.draw2d.geometry.Point;
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
import org.eclipse.gef.commands.CompoundCommand;
import org.eclipse.gef.editpolicies.NonResizableEditPolicy;
import org.eclipse.gef.editpolicies.ResizableEditPolicy;
import org.eclipse.gef.editpolicies.XYLayoutEditPolicy;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.swt.SWT;

/**
 * @author Takahiro Shida.
 *
 */
public class InteractionEditPart extends AbstractUMLEditPart {

	private static Layer layer;
	
	public static Layer getLayer() {
		return layer;
	}
	
	/* (非 Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractGraphicalEditPart#createFigure()
	 */
	protected IFigure createFigure() {
		layer = new Layer() {
			public void paint(Graphics graphics) {
				if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
					graphics.setAntialias(SWT.ON);
					graphics.setTextAntialias(SWT.ON);
				}
				super.paint(graphics);
			}
		};
		layer.setLayoutManager(new FreeformLayout());
		return layer;
	}

	/* (非 Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#createEditPolicies()
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new DelegateLayoutEditPolicy());
	}

	protected List getModelChildren() {
		//順番大事.
		List rv = new ArrayList();
		rv.addAll(getInteraction().getFragments());
		rv.addAll(getInteraction().getLifeLines());
		rv.addAll(getInteraction().getActivations());
		rv.addAll(getInteraction().getInstances());
		rv.addAll(getInteraction().getNotes());
		return rv;
	}
	
	private InteractionModel getInteraction() {
		return (InteractionModel) getModel();
	}
	/* (非 Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		refreshChildren();
	}

	/**
	 * レイアウトポリシー微妙実装.
	 * @author Takahiro Shida.
	 *
	 */
	private class DelegateLayoutEditPolicy extends XYLayoutEditPolicy {

		private Map delegates = new HashMap();

		private DelegateLayoutEditPolicy owner;

		DelegateLayoutEditPolicy() {
			delegates.put(InstanceModel.class, new InstanceEditPolicy(this));
			delegates.put(ActorModel.class, new InstanceEditPolicy(this));
			delegates.put(LifeLineModel.class, new LifeLineEditPolicy(this));
			delegates.put(FragmentModel.class, new FragmentEditPolicy(this));
			delegates.put(ActivationModel.class, new ActivationEditPolicy(this));
			delegates.put(NoteModel.class, new NoteEditPolicy(this));
		}
		
		DelegateLayoutEditPolicy(DelegateLayoutEditPolicy owner) {
			this.owner = owner;
		}
		
		protected DelegateLayoutEditPolicy getOwner() {
			return owner;
		}
		
		protected Command createAddCommand(EditPart child, Object constraint) {
			return null;
		}

		protected EditPolicy createChildEditPolicy(EditPart child) {
			DelegateLayoutEditPolicy delegate = (DelegateLayoutEditPolicy) delegates.get(child.getModel().getClass());
			return delegate != null ? delegate.createChildEditPolicyDelegate(child) : null;
		}
		
		protected EditPolicy createChildEditPolicyDelegate(EditPart child) {
			return null;
		}

		protected Command createChangeConstraintCommand(EditPart child, Object constraint) {
			DelegateLayoutEditPolicy delegate = (DelegateLayoutEditPolicy) delegates.get(child.getModel().getClass());
			return delegate != null ? delegate.createChangeConstraintCommandDelegate(child, constraint) : null;
		}

		protected Command createChangeConstraintCommandDelegate(EditPart child, Object constraint) {
			return null;
		}

		protected Command getCreateCommand(CreateRequest request) {
			DelegateLayoutEditPolicy delegate = (DelegateLayoutEditPolicy) delegates.get(request.getNewObjectType());
			return delegate != null ? delegate.getCreateCommandDelegate(request, getConstraintFor(request)) : null;
		}
		
		protected Command getCreateCommandDelegate(CreateRequest request, Object constraint) {
			return null;
		}
		
		protected Command getDeleteDependantCommand(Request request) {
			return null;
		}
	}
	
	private class ChangeConstraintCommand extends Command {
		
		private AbstractUMLEntityModel model;
		private Rectangle constraint;
		private Rectangle oldConstraint;
		
		public void execute(){
			model.setConstraint(constraint);
			MessageOrderUtil.computeMessageOrders(getInteraction());
			getInteraction().adjustLifeLine();
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
	
	private class CreateCommand extends Command {
		
		private InteractionModel root;
		private AbstractUMLModel model;
		
		public void execute() {
			root.addUMLModel(model);
			root.copyPresentation(model);
			MessageOrderUtil.computeMessageOrders(getInteraction());
		}
		
		public void setRoot(InteractionModel root) {
			this.root = (InteractionModel)root;
		}
		
		public void setModel(AbstractUMLModel model) {
			this.model = (AbstractUMLModel) model;
		}
		
		public void undo() {
			root.removeUMLModel(model);
		}
	}
	
	
	/**
	 * 横移動可能,横サイズ変更可.
	 * @author Takahiro Shida.
	 *
	 */
	private class InstanceEditPolicy extends DelegateLayoutEditPolicy {
		
		public InstanceEditPolicy(DelegateLayoutEditPolicy owner) {
			super(owner);
		}

		protected EditPolicy createChildEditPolicyDelegate(EditPart arg0) {
			return new ResizableEditPolicy();
		}
		
		protected Command createChangeConstraintCommandDelegate(EditPart arg0, Object arg1) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			InstanceModel model = (InstanceModel) arg0.getModel();
			Rectangle rectangle = (Rectangle) arg1;
			if (model.getModelTargetConnections().isEmpty()) {
				rectangle.setLocation(rectangle.x, model.getConstraint().y);
			} 
			if (rectangle.getSize() != null) {
				rectangle.setSize(rectangle.getSize().width, model.getConstraint().height);
			}
			command.setConstraint(rectangle);
			command.setModel(model);
			return command;
		}
		
		/**
		 * InstanceとLifelineを同時に作る.
		 */
		protected Command getCreateCommandDelegate(CreateRequest request, Object constraint) {
			CompoundCommand rv = new CompoundCommand("create Instance And Lifeline");
			CreateCommand command = new CreateCommand();
			Rectangle rectangle = (Rectangle) constraint;
			rectangle.width = 100;
			rectangle.height = -1;
			rectangle.y = InstanceModel.DEFAULT_LOCATION;
			InstanceModel model = (InstanceModel) request.getNewObject();
			model.setConstraint(rectangle);
			command.setRoot((InteractionModel) getOwner().getHost().getModel());
			command.setModel(model);
			rv.add(command);
			command = new CreateCommand();
			command.setRoot((InteractionModel) getOwner().getHost().getModel());
			command.setModel(model.getModel());
			
			Rectangle lineRect = rectangle.getCopy();
			lineRect.translate(new Point(50,0));
			lineRect.width = 5;
			lineRect.height = LifeLineModel.DEFAULT_HEIGHT;
			model.getModel().setConstraint(lineRect);			
			return rv;
		}
	}


	/**
	 * 移動不可,縦サイズ変更可能.
	 * @author Takahiro Shida.
	 *
	 */
	private class LifeLineEditPolicy extends DelegateLayoutEditPolicy {
		
		public LifeLineEditPolicy(DelegateLayoutEditPolicy owner) {
			super(owner);
		}

		protected EditPolicy createChildEditPolicy(EditPart arg0) {
			return super.createChildEditPolicy(arg0);
		}
		
		protected Command createChangeConstraintCommand(EditPart arg0, Object arg1) {
			return super.createChangeConstraintCommand(arg0, arg1);
		}
		
	}

	/**
	 * 移動可能,サイズ変更可能.
	 * @author Takahiro Shida.
	 *
	 */
	private class FragmentEditPolicy extends DelegateLayoutEditPolicy {
		
		public FragmentEditPolicy(DelegateLayoutEditPolicy owner) {
			super(owner);
		}
		
		protected EditPolicy createChildEditPolicyDelegate(EditPart arg0) {
			return new ResizableEditPolicy();
		}
		protected Command getCreateCommandDelegate(CreateRequest arg0, Object arg1) {
			FragmentModel object = (FragmentModel) arg0.getNewObject();
			CreateCommand command = new CreateCommand();
			object.setConstraint((Rectangle) arg1);
			command.setModel(object);
			command.setRoot((InteractionModel) getOwner().getHost().getModel());
			return command;
		}
		
		protected Command createChangeConstraintCommandDelegate(EditPart arg0, Object arg1) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			FragmentModel model = (FragmentModel) arg0.getModel();
			Rectangle rectangle = (Rectangle) arg1;
			command.setModel(model);
			command.setConstraint(rectangle);
			return command;
		}
		
	}

	/**
	 * 移動可能,サイズ変更可能.
	 * @author Takahiro Shida.
	 *
	 */
	private class NoteEditPolicy extends DelegateLayoutEditPolicy {
		
		public NoteEditPolicy(DelegateLayoutEditPolicy owner) {
			super(owner);
		}
		
		protected EditPolicy createChildEditPolicyDelegate(EditPart arg0) {
			return new ResizableEditPolicy();
		}
		protected Command getCreateCommandDelegate(CreateRequest arg0, Object arg1) {
			NoteModel object = (NoteModel) arg0.getNewObject();
			CreateCommand command = new CreateCommand();
			object.setConstraint((Rectangle) arg1);
			command.setModel(object);
			command.setRoot((InteractionModel) getOwner().getHost().getModel());
			return command;
		}
		
		protected Command createChangeConstraintCommandDelegate(EditPart arg0, Object arg1) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			NoteModel model = (NoteModel) arg0.getModel();
			Rectangle rectangle = (Rectangle) arg1;
			command.setModel(model);
			command.setConstraint(rectangle);
			return command;
		}
		
	}
	
	/**
	 * 縦移動可能,サイズ変更不可.
	 * @author Takahiro Shida.
	 *
	 */
	private class ActivationEditPolicy extends DelegateLayoutEditPolicy {
		
		public ActivationEditPolicy(DelegateLayoutEditPolicy owner) {
			super(owner);
		}

		protected EditPolicy createChildEditPolicyDelegate(EditPart arg0) {
			return new NonResizableEditPolicy();
		}
		
		protected Command createChangeConstraintCommandDelegate(EditPart arg0, Object arg1) {
			ChangeConstraintCommand command = new ChangeConstraintCommand();
			ActivationModel model = (ActivationModel) arg0.getModel();
			Rectangle rectangle = (Rectangle) arg1;
			ActivationModel owner = model.getOwner();
			if (owner != null) {
				Rectangle constraint = owner.getConstraint();
				if (rectangle.y < constraint.y) {
					rectangle.y = constraint.y;
				}
			}
			rectangle.setLocation(model.getConstraint().x, rectangle.y);				
			command.setConstraint(rectangle);
			command.setModel(model);
			if (model.isMovable()) {
				return command;
			} else {
				return null;
			}
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
