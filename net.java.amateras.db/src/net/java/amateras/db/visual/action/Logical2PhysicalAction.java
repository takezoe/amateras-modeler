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

public class Logical2PhysicalAction extends SelectionAction {

	public Logical2PhysicalAction(VisualDBEditor editor) {
		super(editor);

		setId(Logical2PhysicalAction.class.getName());
		//setActionDefinitionId(Logical2PhysicalAction.class.getName());
		setText(DBPlugin.getResourceString("action.logical2physical"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		if(!MessageDialog.openConfirm(getWorkbenchPart().getSite().getShell(),
				DBPlugin.getResourceString("dialog.confirm.title"),
				DBPlugin.getResourceString("action.logical2physical.confirm"))){
			return;
		}

		List<EditPart> selection = getSelectedObjects();

		for (int i = 0; i < selection.size(); i++) {
			EditPart editPart = (EditPart) selection.get(i);
			if(editPart instanceof TableEditPart){
				TableModel table = (TableModel) editPart.getModel();

				for(ColumnModel column: table.getColumns()){
					String logical = column.getLogicalName();
					if(logical != null && logical.length() != 0){
						column.setColumnName(NameConverter.logical2physical(logical));
					}
				}

				String logical = table.getTableName();
				if(logical != null && logical.length() != 0){
					table.setTableName(NameConverter.logical2physical(logical));
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
