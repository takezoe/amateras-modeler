package net.java.amateras.db.visual.editpart.tree;

import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.validator.DiagramErrors;
import net.java.amateras.db.visual.editpart.TableEditPart;
import net.java.amateras.db.visual.editpart.tree.FolderTreeEditPart.FolderModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class TableTreeEditPart extends AbstractDBTreeEditPart {
	
    private static final String IMAGE_TABLE_ERROR = "image_table_error";
    private static final String IMAGE_TABLE_WARNING = "image_table_warning";
    
    /**
     * A <code>CompositeImageDescriptor</code> implementation to create overlay icon.
     */
    private static class OverlayImageDescriptor extends CompositeImageDescriptor {
        
        private String baseImageKey;
        private String overlayImageKey;
        
        public OverlayImageDescriptor(String baseImageKey, String overlayImageKey){
            this.baseImageKey = baseImageKey;
            this.overlayImageKey = overlayImageKey;
        }
        
        @Override protected void drawCompositeImage(int arg0, int arg1) {
            Image baseImage = DBPlugin.getImage(baseImageKey);
            drawImage(baseImage.getImageData(), 0, 0);
            
            Image overlayImage = DBPlugin.getImage(overlayImageKey);
            drawImage(overlayImage.getImageData(), 0, 8);
        }

        @Override protected Point getSize() {
            return new Point(16, 16);
        }
    }
    
    // Register overlay icons to ImageRegistery of DBPlugin.
    static {
        DBPlugin.getDefault().getImageRegistry().put(IMAGE_TABLE_ERROR, 
                new OverlayImageDescriptor(DBPlugin.ICON_TABLE, DBPlugin.ICON_OVERLAY_ERROR));
        
        DBPlugin.getDefault().getImageRegistry().put(IMAGE_TABLE_WARNING, 
                new OverlayImageDescriptor(DBPlugin.ICON_TABLE, DBPlugin.ICON_OVERLAY_WARNING));
    }
    
	@Override protected List<Object> getModelChildren() {
		List<Object> list = new ArrayList<Object>();
		ColumnModel[] columns = ((TableModel)getModel()).getColumns();
		for(int i=0;i<columns.length;i++){
			list.add(columns[i]);
		}
		list.add(new FolderModel(DBPlugin.getResourceString("label.index"), null){
			@Override public void doEdit() {
				TableModel table = (TableModel) getModel();
				if(table.isLinkedTable()){
					UIUtils.openAlertDialog(DBPlugin.getResourceString("error.edit.linkedTable"));
					return;
				}
				TableEditPart.openTableEditDialog(
						getViewer(), table, (RootModel) getRoot().getContents().getModel(), 
						(IndexModel) null);
				
			}
			@Override public List<?> getChildren() {
				IndexModel[] indices = ((TableModel)getModel()).getIndices();
				List<IndexModel> list = new ArrayList<IndexModel>();
				for(IndexModel indexModel: indices){
					list.add(indexModel);
				}
				return list;
			}
		});
		return list;
	}
	
	@Override protected void refreshVisuals() {
		TableModel model = (TableModel) getModel();
		setWidgetText(model.getTableName() + "(" + model.getLogicalName() + ")");
		
		if(model.getError().length()==0){
		    setWidgetImage(DBPlugin.getImage(DBPlugin.ICON_TABLE));
		} else if(model.getError().indexOf(DiagramErrors.ERROR_PREFIX)>=0){
            setWidgetImage(DBPlugin.getImage(IMAGE_TABLE_ERROR));
		} else {
            setWidgetImage(DBPlugin.getImage(IMAGE_TABLE_WARNING));
		}
		
		@SuppressWarnings("unchecked")
		List<AbstractEditPart> children = getChildren();
		for(AbstractEditPart child: children){
			child.refresh();
		}
	}

	@Override public void propertyChange(PropertyChangeEvent evt) {
		super.propertyChange(evt);
		String propName = evt.getPropertyName();
		TableModel model = (TableModel) getModel();
    	
        if (TableModel.P_LOGICAL_NAME.equals(propName) || 
        		TableModel.P_TABLE_NAME.equals(propName)) {
    		setWidgetText(model.getTableName() + "(" + model.getLogicalName() + ")");
    		
        } else if (TableModel.P_ERROR.equals(propName)) {
            refreshVisuals();
            
        } if(TableModel.P_COLUMNS.equals(propName) || TableModel.P_INDICES.equals(propName)){
        	refreshChildren();
    		@SuppressWarnings("unchecked")
    		List<AbstractEditPart> children = getChildren();
    		for(AbstractEditPart child: children){
    			child.refresh();
    		}
        }
	}
	
}
