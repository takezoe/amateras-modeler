package net.java.amateras.db.visual.editpart.tree;

import net.java.amateras.db.visual.editpart.tree.FolderTreeEditPart.FolderModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.DommainModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class VisualDBTreeEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
        EditPart part = null;
        if(model instanceof RootModel){
        	part = new RootTreeEditPart();
        } else if(model instanceof TableModel){
			part = new TableTreeEditPart();
		} else if(model instanceof ColumnModel){
			part = new ColumnTreeEditPart();
		} else if(model instanceof FolderModel){
			part = new FolderTreeEditPart();
		} else if(model instanceof DommainModel){
			part = new DommainTreeEditPart();
		} else if(model instanceof IndexModel){
			part = new IndexTreeEditPart();
		}
		if(part != null){
			part.setModel(model);
		}
		return part;
	}

}
