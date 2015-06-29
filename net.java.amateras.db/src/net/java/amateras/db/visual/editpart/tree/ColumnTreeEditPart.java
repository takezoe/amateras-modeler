package net.java.amateras.db.visual.editpart.tree;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.ColumnModel;

public class ColumnTreeEditPart extends AbstractDBTreeEditPart {
	
	protected void refreshVisuals() {
		ColumnModel model = (ColumnModel) getModel();
		
		StringBuilder sb = new StringBuilder();
		sb.append(model.getColumnName()).append("(").append(model.getLogicalName()).append(")");
		sb.append(" - ");
		sb.append(model.getColumnType().getName());
		if(model.getColumnType().supportSize()){
			sb.append("(").append(model.getSize()).append(")");
		}
		
		setWidgetText(sb.toString());
		
		if(model.isPrimaryKey()){
			setWidgetImage(DBPlugin.getImage(DBPlugin.ICON_PK_COLUMN));
		} else {
			setWidgetImage(DBPlugin.getImage(DBPlugin.ICON_COLUMN));
		}
		// TODO changes an image for foreign key columns
	}
	
}
