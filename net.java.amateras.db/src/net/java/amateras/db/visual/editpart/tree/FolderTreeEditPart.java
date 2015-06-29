package net.java.amateras.db.visual.editpart.tree;

import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.model.RootModel;

public class FolderTreeEditPart extends AbstractDBTreeEditPart {
	
	@Override protected List<?> getModelChildren() {
		FolderModel model = (FolderModel) getModel();
		return model.getChildren();
	}
	
	@Override protected void refreshVisuals() {
		FolderModel model = (FolderModel) getModel();
		setWidgetText(model.name);
		setWidgetImage(DBPlugin.getImage(DBPlugin.ICON_FOLDER));
		
//		@SuppressWarnings("unchecked")
//		List<AbstractEditPart> children = getChildren();
//		for(AbstractEditPart child: children){
//			child.refresh();
//		}
	}
	
	public static abstract class FolderModel {
		public String name;
		public RootModel root;
		
		public FolderModel(String name, RootModel root){
			this.name = name;
			this.root = root;
		}
		
		public abstract List<?> getChildren();
		
		public abstract void doEdit();
	}
	
}
