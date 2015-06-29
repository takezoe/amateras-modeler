package net.java.amateras.db.visual.editpart.tree;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.DommainModel;

public class DommainTreeEditPart extends AbstractDBTreeEditPart {

	protected void refreshVisuals() {
		DommainModel model = (DommainModel) getModel();
		
		StringBuilder sb = new StringBuilder();
		sb.append(model.getName()).append(" - ");
		sb.append(model.getType().getName());
		if(model.getType().supportSize()){
			sb.append("(").append(model.getSize()).append(")");
		}
		
		setWidgetText(sb.toString());
		setWidgetImage(DBPlugin.getImage(DBPlugin.ICON_DOMMAIN));
	}
	
	
}
