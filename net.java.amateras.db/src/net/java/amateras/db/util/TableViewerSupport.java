package net.java.amateras.db.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.java.amateras.db.DBPlugin;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public abstract class TableViewerSupport<T> {

	private Composite control;
	private TableViewer viewer;
	private Button buttonAdd;
	private Button buttonEdit;
	private Button buttonRemove;
	private List<T> model;

	public TableViewerSupport(List<T> model, Composite parent){
		this.model = model;
		initComponents(parent);
	}

	public Control getControl(){
		return this.control;
	}

	private void initComponents(Composite parent){
		this.control = new Composite(parent, SWT.NONE);
		this.control.setLayout(new GridLayout(2, false));

		viewer = new TableViewer(control, SWT.MULTI|SWT.FULL_SELECTION|SWT.BORDER|SWT.H_SCROLL|SWT.V_SCROLL);
		Table table = viewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		table.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent evt){
				TableItem[] items = viewer.getTable().getSelection();
				boolean enable = false;
				if(items.length > 0){
					String path = items[0].getText(1);
					if(!path.equals("[Default]")){
						enable = true;
					}
				}
				buttonEdit.setEnabled(enable);
				buttonRemove.setEnabled(enable);
			}
		});

		initTableViewer(viewer);

		// create buttons
		Composite buttons = new Composite(control,SWT.NONE);
		buttons.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		GridLayout layout = new GridLayout();
		layout.marginBottom = 0;
		layout.marginHeight = 0;
		layout.marginLeft = 0;
		layout.marginRight = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttonAdd = new Button(buttons,SWT.PUSH);
		buttonAdd.setText(DBPlugin.getResourceString("button.add"));
		buttonAdd.setLayoutData(createButtonGridData());
		buttonAdd.addSelectionListener(
				new SelectionAdapter(){
					@Override public void widgetSelected(SelectionEvent evt){
						T obj = doAdd();
						if(obj!=null){
							model.add(obj);
							viewer.refresh();
						}
					}
				});
		buttonEdit = new Button(buttons,SWT.PUSH);
		buttonEdit.setText(DBPlugin.getResourceString("button.edit"));
		buttonEdit.setLayoutData(createButtonGridData());
		buttonEdit.setEnabled(false);
		buttonEdit.addSelectionListener(
				new SelectionAdapter(){
					@Override public void widgetSelected(SelectionEvent evt){
						IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
						@SuppressWarnings("unchecked")
						T obj = (T) sel.getFirstElement();
						doEdit(obj);
						viewer.refresh();
					}
				});
		buttonRemove = new Button(buttons,SWT.PUSH);
		buttonRemove.setText(DBPlugin.getResourceString("button.delete"));
		buttonRemove.setLayoutData(createButtonGridData());
		buttonRemove.setEnabled(false);
		buttonRemove.addSelectionListener(
				new SelectionAdapter(){
					@Override public void widgetSelected(SelectionEvent evt){
						IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
						@SuppressWarnings("unchecked")
						List<T> list = (List<T>) sel.toList();
						doRemove(list);
						viewer.refresh();
					}
				});

		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event){
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				if(sel==null || sel.getFirstElement()==null){
					buttonEdit.setEnabled(false);
					buttonRemove.setEnabled(false);
				} else {
					buttonEdit.setEnabled(true);
					buttonRemove.setEnabled(true);
				}
			}
		});

		viewer.setContentProvider(new ListContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setInput(model);
	}

	protected void initTableViewer(TableViewer viewer){
		Type[] types = GenericsUtils.getGenericType(this, TableViewerSupport.class);
		Table table = viewer.getTable();

		for(ColumnInfo column: getColumns((Class<?>) types[0])){
			TableColumn col = new TableColumn(table, SWT.NULL);
			col.setText(DBPlugin.getResourceString(column.label()));
			col.setWidth(column.width());
		}
	}

	protected abstract T doAdd();
	protected abstract void doEdit(T obj);
	protected void doRemove(List<T> objList){
		model.removeAll(objList);
	}
	protected ITableLabelProvider createLabelProvider(){
		return new DefaultTableLabelProvider();
	}

	public List<T> getModel(){
		return this.model;
	}

	public TableViewer getTableViewer(){
		return this.viewer;
	}

	/**
	 * Create LayoutData for &quot;add&quot;, &quot;edit&quot; and &quot;remove&quot; buttons.
	 * @return GridData
	 */
	private static GridData createButtonGridData(){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		return gd;
	}

	@SuppressWarnings("rawtypes")
	public static class ListContentProvider implements IStructuredContentProvider {

		List fContents;

		public ListContentProvider() {
		}

		public Object[] getElements(Object input) {
			if (fContents != null && fContents == input)
				return fContents.toArray();
			return new Object[0];
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			if (newInput instanceof List)
				fContents= (List)newInput;
			else
				fContents= null;
			// we use a fixed set.
		}

		public void dispose() {
		}

		public boolean isDeleted(Object o) {
			return fContents != null && !fContents.contains(o);
		}
	}

	/**
	 * The base class of LabelProvider for TableViewers.
	 */
	public static abstract class TableLabelProviderAdapter implements ITableLabelProvider {

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
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
	}

	/**
	 * The default implementation of the {@link TableLabelProviderAdapter}.
	 * <p>
	 * Retrieves column values from the given element using {@link ColumnInfo} annotation.
	 *
	 * @see ColumnInfo
	 */
	public static class DefaultTableLabelProvider extends TableLabelProviderAdapter {
		@Override
		public String getColumnText(Object element, int columnIndex) {
			return getColumnValue(element, columnIndex);
		}
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Documented
	public @interface ColumnInfo {
		int index();
		String label();
		int width();
	}

	/**
	 * A utility method for retrieving column information.
	 */
	public static List<ColumnInfo> getColumns(Class<?> clazz){
		List<ColumnInfo> columns = new ArrayList<ColumnInfo>();

		for(Field field: clazz.getFields()){
			ColumnInfo annotation = field.getAnnotation(ColumnInfo.class);
			if(annotation != null){
				columns.add(annotation);
			}
		}

		Collections.sort(columns, new Comparator<ColumnInfo>() {
			public int compare(ColumnInfo o1, ColumnInfo o2) {
				return o1.index() > o2.index() ? 1 : o1.index() < o2.index() ? -1 : 0;
			}
		});

		return columns;
	}

	/**
	 * A utility method for retrieving the column value.
	 */
	public static String getColumnValue(Object element, int columnIndex){
		for(Field field: element.getClass().getFields()){
			ColumnInfo annotation = field.getAnnotation(ColumnInfo.class);
			if(annotation != null){
				int index = annotation.index();
				if(index == columnIndex){
					try {
						Object value = field.get(element);
						if(value == null){
							return "";
						} else {
							return value.toString();
						}
					} catch(Exception ex){
						// TODO log??
					}
				}
			}
		}
		return "";
	}

}
