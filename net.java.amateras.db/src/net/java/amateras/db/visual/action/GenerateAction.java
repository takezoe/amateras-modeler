package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.editor.VisualDBEditor;
import net.java.amateras.db.visual.generate.IGenerator;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IFileEditorInput;

/**
 *
 * @author Naoki Takezoe
 */
public class GenerateAction extends Action {

	private IGenerator generater;
	private VisualDBEditor editor;
	private GraphicalViewer viewer;

	public GenerateAction(IGenerator generater, GraphicalViewer viewer, VisualDBEditor editor){
		super(generater.getGeneratorName());
		this.generater = generater;
		this.editor = editor;
		this.viewer = viewer;
	}

	public void run() {
		// force save
		if(editor.isDirty()){
			if(MessageDialog.openConfirm(editor.getSite().getShell(),
					DBPlugin.getResourceString("dialog.confirm.title"),
					DBPlugin.getResourceString("message.saveBeforeExecute"))){
				editor.doSave(new NullProgressMonitor());
			} else {
				return;
			}
		}

		RootModel root = (RootModel) viewer.getContents().getModel();
		IFileEditorInput input = (IFileEditorInput) editor.getEditorInput();
		generater.execute(input.getFile(), root, viewer);
	}
}
