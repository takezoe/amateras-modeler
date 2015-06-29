package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

/**
 * 
 * @author Naoki Takezoe
 */
public class DeleteMarkerAction extends Action {

    private GraphicalViewer viewer;

    public DeleteMarkerAction(GraphicalViewer viewer) {
        super(DBPlugin.getResourceString("action.validation.deleteMarkers"));
        this.viewer = viewer;
    }

    @Override public void run() {
        CommandStack stack = viewer.getEditDomain().getCommandStack();
        stack.execute(new Command("Delete markers") {
            @Override public void execute() {
                RootModel model = (RootModel) viewer.getRootEditPart().getContents().getModel();
                for (AbstractDBEntityModel entity : model.getChildren()) {
                    if (entity instanceof TableModel) {
                        ((TableModel) entity).setError("");
                    }
                }
            }

            @Override public boolean canUndo() {
                return false;
            }
        });

        IEditorInput input = UIUtils.getActiveEditor().getEditorInput();
        if (input instanceof IFileEditorInput) {
            IFile file = ((IFileEditorInput) input).getFile();
            try {
                file.deleteMarkers(IMarker.PROBLEM, false, 0);
            } catch (CoreException ex) {
                DBPlugin.logException(ex);
            }
        }
    }

}
