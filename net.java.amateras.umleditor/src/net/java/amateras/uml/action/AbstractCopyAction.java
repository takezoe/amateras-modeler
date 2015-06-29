package net.java.amateras.uml.action;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.ICloneableModel;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.Clipboard;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;

/**
 * The abstract class for copy actions in the diagram.
 *
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public abstract class AbstractCopyAction extends SelectionAction {

	private AbstractPasteAction pasteAction;

	private List<Class<?>> allowModelTypes = new ArrayList<Class<?>>();

	public AbstractCopyAction(IWorkbenchPart part, AbstractPasteAction pasteAction) {
		super(part);
		setId(ActionFactory.COPY.getId());
		setActionDefinitionId(ActionFactory.COPY.getId());
		setText(UMLPlugin.getDefault().getResourceString("menu.copy"));
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_COPY_DISABLED));

		this.pasteAction = pasteAction;
	}

	protected void registerAllowType(Class<?> type) {
		allowModelTypes.add(type);
	}

	public void run() {
		@SuppressWarnings("unchecked")
		List<Object> selection = getSelectedObjects();
		List<Object> copied = new ArrayList<Object>();
		for (int i = 0; i < selection.size(); i++) {
			EditPart editPart = (EditPart) selection.get(i);
			if (isAllowType(editPart.getModel().getClass())) {
				ICloneableModel cloneable = (ICloneableModel) editPart.getModel();
				copied.add(cloneable.clone());
			}
		}
		Clipboard.getDefault().setContents(copied);

		pasteAction.update();
	}

	protected boolean calculateEnabled() {
		@SuppressWarnings("unchecked")
		List<Object> selected = getSelectedObjects();
		if (selected.isEmpty()) {
			return true;
		}
		for (int i = 0; i < selected.size(); i++) {
			if (!(selected.get(i) instanceof EditPart)) {
				return false;
			}
			EditPart editPart = (EditPart) selected.get(i);
			if (isAllowType(editPart.getModel().getClass())) {
				return true;
			}
		}
		return false;
	}

	private boolean isAllowType(Class<?> type) {
		for (int j = 0; j < allowModelTypes.size(); j++) {
			Class<?> allowModelType = allowModelTypes.get(j);
			if (allowModelType.isAssignableFrom(type)) {
				return true;
			}
		}
		return false;
	}

}
