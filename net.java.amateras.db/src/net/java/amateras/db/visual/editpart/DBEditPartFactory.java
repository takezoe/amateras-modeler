package net.java.amateras.db.visual.editpart;

import net.java.amateras.db.visual.model.AnchorModel;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.NoteModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

public class DBEditPartFactory implements EditPartFactory {

	public EditPart createEditPart(EditPart context, Object model) {
		EditPart part = null;
		
		if(model instanceof RootModel){
			part = new RootEditPart();
		} else if(model instanceof TableModel){
			part = new TableEditPart();
		} else if(model instanceof NoteModel){
			part = new NoteEditPart();
		} else if(model instanceof ForeignKeyModel){
			part = new ForeignKeyEditPart();
		} else if(model instanceof AnchorModel){
			part = new AnchorEditPart();
		}
		
		if(part!=null){
			part.setModel(model);
		}
		
		return part;
	}

}
