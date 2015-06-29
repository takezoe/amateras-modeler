package net.java.amateras.db.visual.editpart.tree;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.IndexModel;

public class IndexTreeEditPart extends AbstractDBTreeEditPart {
	
	protected void refreshVisuals() {
		IndexModel model = (IndexModel) getModel();
		setWidgetText(model.toString());
		setWidgetImage(DBPlugin.getImage(DBPlugin.ICON_INDEX));
	}
	
}
