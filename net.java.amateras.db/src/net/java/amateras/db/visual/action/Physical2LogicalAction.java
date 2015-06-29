package net.java.amateras.db.visual.action;

import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.NameConverter;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.editor.VisualDBEditor;
import net.java.amateras.db.visual.editpart.TableEditPart;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;

public class Physical2LogicalAction extends SelectionAction {

	public Physical2LogicalAction(VisualDBEditor editor) {
		super(editor);

		setId(Physical2LogicalAction.class.getName());
		//setActionDefinitionId(Physical2LogicalAction.class.getName());
		setText(DBPlugin.getResourceString("action.physical2logical"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		if(!MessageDialog.openConfirm(getWorkbenchPart().getSite().getShell(),
				DBPlugin.getResourceString("dialog.confirm.title"),
				DBPlugin.getResourceString("action.physical2logical.confirm"))){
			return;
		}

		List<EditPart> selection = getSelectedObjects();

		for (int i = 0; i < selection.size(); i++) {
			EditPart editPart = (EditPart) selection.get(i);
			if(editPart instanceof TableEditPart){
				TableModel table = (TableModel) editPart.getModel();

				for(ColumnModel column: table.getColumns()){
					String physical = column.getColumnName();
					if(physical != null && physical.length() != 0){
						column.setLogicalName(NameConverter.physical2logical(physical));
					}
				}

				String physical = table.getTableName();
				if(physical != null && physical.length() != 0){
					table.setLogicalName(NameConverter.physical2logical(physical));
				}
			}
		}

		UIUtils.getActiveEditor().doSave(new NullProgressMonitor());
	}

	@Override
	@SuppressWarnings("unchecked")
	protected boolean calculateEnabled() {
		List<Object> selection = getSelectedObjects();
		for (int i = 0; i < selection.size(); i++) {
			Object obj = selection.get(i);
			if(obj instanceof TableEditPart){
				return true;
			}
		}
		return false;
	}
}
