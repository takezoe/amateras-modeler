package net.java.amateras.db.visual.action;

import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.editor.VisualDBEditor;
import net.java.amateras.db.visual.editpart.TableEditPart;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.ui.actions.SelectionAction;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * Converts selected table name and column name to lowercase.
 *
 * @author Naoki Takezoe
 */
public class LowercaseAction extends SelectionAction {

	public LowercaseAction(VisualDBEditor editor) {
		super(editor);

		setId(LowercaseAction.class.getName());
		//setActionDefinitionId(LowercaseAction.class.getName());
		setText(DBPlugin.getResourceString("action.toLowercase"));
	}

	@Override
	@SuppressWarnings("unchecked")
	public void run() {
		if(!MessageDialog.openConfirm(getWorkbenchPart().getSite().getShell(),
				DBPlugin.getResourceString("dialog.confirm.title"),
				DBPlugin.getResourceString("action.toLowercase.confirm"))){
			return;
		}

		List<EditPart> selection = getSelectedObjects();

		for (int i = 0; i < selection.size(); i++) {
			EditPart editPart = (EditPart) selection.get(i);
			if(editPart instanceof TableEditPart){
				TableModel table = (TableModel) editPart.getModel();

				for(ColumnModel column: table.getColumns()){
					column.setColumnName(column.getColumnName().toLowerCase());
				}

				for(IndexModel index: table.getIndices()){
					index.setIndexName(index.getIndexName().toLowerCase());
				}

				table.setTableName(table.getTableName().toLowerCase());
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
