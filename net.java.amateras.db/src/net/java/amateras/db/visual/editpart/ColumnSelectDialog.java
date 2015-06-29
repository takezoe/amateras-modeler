package net.java.amateras.db.visual.editpart;

import java.util.List;

import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.ColumnModel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;

public class ColumnSelectDialog extends Dialog {

	private List<ColumnModel> columns;
	private ColumnModel selectedColumn;
	private TableViewer viewer;


	protected ColumnSelectDialog(Shell parentShell, List<ColumnModel> columns) {
		super(parentShell);
		this.columns = columns;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 300);
	}

	@Override protected Control createDialogArea(Composite parent) {
		viewer = new TableViewer(parent, SWT.FULL_SELECTION|SWT.SINGLE);
		Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		UIUtils.createColumn(table, "dialog.table.columnName", 150);
		UIUtils.createColumn(table, "dialog.table.columnType", 150);

		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ITableLabelProvider(){

			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				ColumnModel column = (ColumnModel) element;
				if(columnIndex == 0){
					return column.getColumnName();
				} else if(columnIndex == 1){
					StringBuilder sb = new StringBuilder();
					sb.append(column.getColumnType().getName());
					if(column.getColumnType().supportSize()){
						sb.append("(").append(column).append(")");
					}
					return sb.toString();
				}
				return null;
			}

			public void addListener(ILabelProviderListener listener) {
			}

			public void dispose() {
			}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {
			}
		});
		viewer.setInput(columns);

		return viewer.getControl();
	}

	protected void okPressed() {
		selectedColumn = (ColumnModel) ((IStructuredSelection) viewer.getSelection()).getFirstElement();
		super.okPressed();
	}

	public ColumnModel getSelectedColumn(){
		return selectedColumn;
	}


}
