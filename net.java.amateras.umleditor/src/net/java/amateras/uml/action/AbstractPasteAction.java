package net.java.amateras.uml.action;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.DiagramEditor;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.RootEditPart;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * The abstract class for paste actions in the diagram.
 *
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public abstract class AbstractPasteAction extends SelectionAction {

	private List<Class<?>> allowModelTypes = new ArrayList<Class<?>>();

	public AbstractPasteAction(IWorkbenchPart part) {
		super(part);
		setId(ActionFactory.PASTE.getId());
		setActionDefinitionId(ActionFactory.PASTE.getId());
		setText(UMLPlugin.getDefault().getResourceString("menu.paste"));
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	}

	protected void registerAllowType(Class<?> type) {
		allowModelTypes.add(type);
	}

	public void run() {
		@SuppressWarnings("unchecked")
		List<Object> model = (List<Object>) Clipboard.getDefault().getContents();
		if (model == null) {
			return;
		}
		CommandStack stack = (CommandStack) ((DiagramEditor) getWorkbenchPart()).getAdapter(CommandStack.class);
		stack.execute(new PasteCommand(model));
	}

	private class PasteCommand extends Command {

		private List<Object> target;

		public PasteCommand(List<Object> target) {
			this.target = target;
		}

		public void execute() {
			List<Object> copied = new ArrayList<Object>();
			for (int i = 0; i < target.size(); i++) {
				ICloneableModel obj = (ICloneableModel) target.get(i);
				if (obj instanceof AbstractUMLEntityModel) {
					AbstractUMLEntityModel entity = (AbstractUMLEntityModel) obj;
					entity.setConstraint(getNewRectangle(entity.getConstraint()));
				}
				EditPart rootEditPart = getRootEditPart();
				((AbstractUMLEntityModel) rootEditPart.getModel()).addChild((AbstractUMLModel) obj);
				copied.add(obj.clone());
			}
			Clipboard.getDefault().setContents(copied);
		}

		/**
		 * Returns the parent edit part of the selected object.
		 */
		private EditPart getRootEditPart() {
			@SuppressWarnings("unchecked")
			List<Object> selected = getSelectedObjects();

			EditPart rootEditPart = null;
			if (selected.size() > 0) {
				Object item = selected.get(0);
				if (item instanceof EditPart) {
					EditPart selectedEditPart = (EditPart) item;
					while (!(selectedEditPart instanceof RootEditPart)) {
						selectedEditPart = selectedEditPart.getParent();
					}
					rootEditPart = selectedEditPart;
				}
			}
			return rootEditPart;
		}

		public void undo() {
			for (int i = 0; i < target.size(); i++) {
				ICloneableModel obj = (ICloneableModel) target.get(i);
				obj.getParent().removeChild((AbstractUMLModel) obj);
			}
			Clipboard.getDefault().setContents(target);
		}
	}

	private Rectangle getNewRectangle(Rectangle rect) {
		Rectangle newRect = new Rectangle();
		newRect.x = rect.x + 5;
		newRect.y = rect.y + 5;
		newRect.width = rect.width;
		newRect.height = rect.height;
		return newRect;
	}

	@SuppressWarnings("unchecked")
	protected boolean calculateEnabled() {
		Object obj = Clipboard.getDefault().getContents();
		if (obj == null) {
			return false;
		}
		if (obj instanceof List) {
			List<Object> list = (List<Object>) obj;
			for (int i = 0; i < list.size(); i++) {
				Object element = list.get(i);
				for (int j = 0; j < allowModelTypes.size(); j++) {
					Class<?> type = allowModelTypes.get(j);
					if (type.isAssignableFrom(element.getClass())) {
						return true;
					}
				}
			}
		}
		return true;
	}
}
