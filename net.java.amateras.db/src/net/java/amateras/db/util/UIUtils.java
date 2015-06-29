package net.java.amateras.db.util;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.visual.editor.VisualDBEditor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author Naoki Takezoe
 */
public class UIUtils {
	
	/**
	 * Creates the <code>GridData</code> with a given colspan.
	 * 
	 * @param colspan the colspan
	 * @return the created GridData
	 */
	public static GridData createGridData(int colspan){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colspan;
		return gd;
	}
	
	public static GridData createGridData(int colspan, int option){
		GridData gd = new GridData(option);
		gd.horizontalSpan = colspan;
		return gd;
	}
	
	public static GridData createGridDataWithWidth(int width){
		GridData gd = new GridData();
		gd.widthHint = width;
		return gd;
	}
	
	public static GridData createGridDataWithColspan(int colspan, int height){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colspan;
		gd.heightHint = height;
		return gd;
	}
	
	public static GridData createGridDataWithRowspan(int rowspan, int width){
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.verticalSpan = rowspan;
		gd.widthHint = width;
		return gd;
	}
	
	/**
	 * Creates a <code>ColumnInfo</code> with a given width.
	 * 
	 * @param table the parent table
	 * @param key the resource key of the column label
	 * @param width the column width
	 */
	public static void createColumn(Table table, String key, int width){
		TableColumn column = new TableColumn(table, SWT.NULL);
		column.setText(DBPlugin.getResourceString(key));
		column.setWidth(width);
	}
	
	/**
	 * Creates a <code>Label</code> with a given text.
	 * 
	 * @param parent the parent
	 * @param key the resource key of the label text
	 * @return the created label
	 */
	public static Label createLabel(Composite parent, String key){
		Label label = new Label(parent, SWT.NULL);
		label.setText(DBPlugin.getResourceString(key));
		return label;
	}
	
	/**
	 * Open the alert dialog.
	 * 
	 * @param message message
	 */
	public static void openAlertDialog(String messageKey){
		MessageBox box = new MessageBox(Display.getCurrent().getActiveShell(),SWT.NULL|SWT.ICON_ERROR);
		box.setMessage(DBPlugin.getResourceString(messageKey));
		box.setText(DBPlugin.getResourceString("dialog.alert.title"));
		box.open();
	}
	
	/**
	 * Returns an active ERD editor in the workbench.
	 * 
	 * @return an instance of an active ERD editor
	 */
	public static VisualDBEditor getActiveEditor(){
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IEditorPart editorPart = page.getActiveEditor();
		if(editorPart instanceof VisualDBEditor){
			return (VisualDBEditor) editorPart;
		}
		return null;
	}

	
}
