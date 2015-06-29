package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.validator.DiagramErrors;
import net.java.amateras.db.validator.DiagramValidator;
import net.java.amateras.db.validator.DiagramErrors.DiagramError;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IFileEditorInput;

/**
 * 
 * @author Naoki Takezoe
 */
public class ValidateAction extends Action {

    private GraphicalViewer viewer;

    public ValidateAction(GraphicalViewer viewer) {
        super(DBPlugin.getResourceString("action.validation.executeValidation"));
        this.viewer = viewer;
    }
    
    @Override 
    public void run() {
        
        // Validate models
        CommandStack stack = viewer.getEditDomain().getCommandStack();
        stack.execute(new Command("Validation"){
            @Override 
            public boolean canUndo() {
                return false;
            }

            @Override 
            public void execute() {
                try {
                    RootModel model = (RootModel) viewer.getContents().getModel();
                    IFile file = ((IFileEditorInput) UIUtils.getActiveEditor().getEditorInput()).getFile();
                    
                    file.deleteMarkers(IMarker.PROBLEM, false, 0);
                    DiagramErrors errors = new DiagramValidator(model).doValidate();
                    for(DiagramError error: errors.getErrors()){
                        error.addMarker(file);
                    }
                } catch(CoreException ex){
                    DBPlugin.logException(ex);
                }
            }
        });
        
//        // Save editing models using XStream
//        try {
//            file.setContents(VisualDBSerializer.serialize(model), true, true, null);
//        } catch(Exception ex){
//            DBPlugin.logException(ex);
//        }
    }

}
