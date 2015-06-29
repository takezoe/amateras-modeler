package net.java.amateras.db.visual.action;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.editor.VisualDBEditor;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.ICloneableModel;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * Paste copied entity models in the diagram editor.
 *
 * @author Naoki Takezoe
 * @since 1.0.4
 */
public class PasteAction extends SelectionAction {

	public PasteAction(VisualDBEditor editor) {
		super(editor);
		setId(ActionFactory.PASTE.getId());
		//setActionDefinitionId(ActionFactory.PASTE.getId());
		setText(DBPlugin.getResourceString("action.paste"));
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_PASTE_DISABLED));
	}

	@SuppressWarnings("unchecked")
	public void run() {
		List<ICloneableModel> model = (List<ICloneableModel>) Clipboard.getDefault().getContents();
		if (model == null) {
			return;
		}
		CommandStack stack = (CommandStack) ((VisualDBEditor) getWorkbenchPart())
				.getAdapter(CommandStack.class);
		stack.execute(new PasteCommand(model));
	}

	private RootModel getRootModel(){
		GraphicalViewer viewer
			= (GraphicalViewer) getWorkbenchPart().getAdapter(GraphicalViewer.class);
		RootModel root = (RootModel) viewer.getRootEditPart().getContents().getModel();
		return root;
	}

	private class PasteCommand extends Command {

		private List<ICloneableModel> target;

		public PasteCommand(List<ICloneableModel> target) {
			this.target = target;
		}

		public void execute() {
			List<ICloneableModel> copied = new ArrayList<ICloneableModel>();
			for (int i = 0; i < target.size(); i++) {
				ICloneableModel obj = target.get(i);
				AbstractDBEntityModel entity = (AbstractDBEntityModel) obj;
				entity.setConstraint(getNewRectangle(entity.getConstraint()));

				getRootModel().addChild((AbstractDBEntityModel) obj);
				copied.add(obj.clone());
			}
			Clipboard.getDefault().setContents(copied);
		}

		public void undo() {
			for (int i = 0; i < target.size(); i++) {
				ICloneableModel obj = target.get(i);
				getRootModel().removeChild((AbstractDBEntityModel) obj);
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
				if (ICloneableModel.class.isAssignableFrom(element.getClass())) {
					return true;
				}
			}
		}
		return true;
	}

}
