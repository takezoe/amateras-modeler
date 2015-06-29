package net.java.amateras.db.visual.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.ModelUtils;
import net.java.amateras.db.visual.editor.VisualDBSerializer;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;

public class RefreshLinkedTablesAction extends Action {

	private GraphicalViewer viewer;
	private Map<String, RootModel> models = new HashMap<String, RootModel>();

	public RefreshLinkedTablesAction(GraphicalViewer viewer) {
		super(DBPlugin.getResourceString("action.refreshLinkedTables"));
		this.viewer = viewer;
		setImageDescriptor(DBPlugin.getImageDescriptor(DBPlugin.ICON_REFRESH));
	}

	private TableModel getNewTable(TableModel oldTable) throws Exception {
		String path = oldTable.getLinkedPath();
		if(models.containsKey(path)){
			return models.get(path).getTable(oldTable.getTableName());
		}
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(path));
		RootModel root = VisualDBSerializer.deserialize(file.getContents());
		models.put(path, root);

		return  root.getTable(oldTable.getTableName());
	}

    @Override public void run() {
		final RootModel root = (RootModel) viewer.getContents().getModel();
		final List<TableModel> oldTables = new ArrayList<TableModel>();

		for(AbstractDBEntityModel child: root.getChildren()){
			if(child instanceof TableModel){
				TableModel table = (TableModel) child;
				if(table.isLinkedTable()){
					oldTables.add(table);
				}
			}
		}

		if(oldTables.isEmpty()){
			MessageDialog.openInformation(viewer.getControl().getShell(),
					DBPlugin.getResourceString("dialog.info.title"),
					DBPlugin.getResourceString("action.refreshLinkedTables.noLinkedTable"));
			return;
		}

		CommandStack stack = viewer.getEditDomain().getCommandStack();
		stack.execute(new Command(){
			@Override public void execute() {
				for(TableModel oldTable: oldTables){
					try {
						TableModel newTable = getNewTable(oldTable);
						if(newTable != null){
							ModelUtils.importOrReplaceTable(root, oldTable, newTable);
							newTable.setLinkedPath(oldTable.getLinkedPath());
						}
					} catch(Exception ex){
						ex.printStackTrace();
					}
				}
				models.clear();
			}

			@Override public boolean canUndo(){
				return false;
			}
		});


    }


}
