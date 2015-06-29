package net.java.amateras.db.visual.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.dialect.IColumnType;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.DommainModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

public class DommainEditDialog extends Dialog {

	private TableViewer viewer;
	private RootModel rootModel;
	private IDialect dialect;
	private List<DommainModel> result = new ArrayList<DommainModel>();
	private DommainModel editingModel = null;
	
	private Text dommainName;
	private Combo columnType;
	private Text columnSize;
	private Button removeButton;
	private Button addButton;
	
	protected DommainEditDialog(Shell parentShell, RootModel rootModel, DommainModel editDommain) {
		super(parentShell);
		setShellStyle(getShellStyle()|SWT.RESIZE);
		for(DommainModel model: rootModel.getDommains()){
			DommainModel clonedModel = model.clone();
			if(editDommain != null && model == editDommain){
				editingModel = clonedModel;
			}
			result.add(clonedModel);
		}
		this.rootModel = rootModel;
		this.dialect = DialectProvider.getDialect(rootModel.getDialectName());
	}
	
	@Override protected Point getInitialSize() {
		Point point = super.getInitialSize();
		point.y = 350;
		return point;
	}
	
	protected Control createDialogArea(Composite parent) {
		getShell().setText(DBPlugin.getResourceString("dialog.dommain.title"));
		
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(2,false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		viewer = new TableViewer(composite, 
				SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.FULL_SELECTION);
		Table table = viewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		UIUtils.createColumn(table, "dialog.dommain.name", 150);
		UIUtils.createColumn(table, "dialog.dommain.type", 150);
		
		viewer.setContentProvider(new ArrayContentProvider());
		viewer.setLabelProvider(new ITableLabelProvider(){
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				DommainModel model = (DommainModel) element;
				if(columnIndex == 0){
					return model.getName();
				} else if(columnIndex == 1){
					String type = model.getType().getName();
					if(model.getType().supportSize()){
						type = type + "(" + model.getSize() + ")";
					}
					return type;
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
		
		viewer.setInput(result);
		
		Composite buttons = new Composite(composite, SWT.NULL);
		GridLayout buttonsLayout = new GridLayout(1, false);
		buttonsLayout.horizontalSpacing = 0;
		buttonsLayout.verticalSpacing = 0;
		buttonsLayout.marginHeight = 0;
		buttonsLayout.marginWidth = 2;
		buttons.setLayout(buttonsLayout);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		addButton = new Button(buttons, SWT.PUSH);
		addButton.setText(DBPlugin.getResourceString("dialog.dommain.addDommain"));
		addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				DommainModel dommain = new DommainModel();
				dommain.setName(DBPlugin.getResourceString("dialog.dommain.name"));
				IColumnType defaultType = dialect.getDefaultColumnType();
				dommain.setType(defaultType);
				dommain.setSize(defaultType.supportSize() ? "0" : "");
				dommain.setId(getDommainId());
				result.add(dommain);
				viewer.refresh();
			}
		});
		
		removeButton = new Button(buttons, SWT.PUSH);
		removeButton.setText(DBPlugin.getResourceString("dialog.dommain.removeDommain"));
		removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
				
				@SuppressWarnings("unchecked")
				List<DommainModel> dommains = (List<DommainModel>) sel.toList();
				for(DommainModel dommain: dommains){
					for(AbstractDBEntityModel entity: rootModel.getChildren()){
						if(entity instanceof TableModel){
							TableModel table = (TableModel) entity;
							for(ColumnModel column: table.getColumns()){
								if(column.getDommain() != null && dommain.getId().equals(column.getDommain().getId())){
									UIUtils.openAlertDialog("dialog.alert.dommain.delete.error");
									return;
								}
							}
						}
					}
				}
				
				result.removeAll(dommains);
				viewer.refresh();
			}
		});
		
		Composite editArea = new Composite(composite, SWT.NULL);
		editArea.setLayout(new GridLayout(4, false));
		editArea.setLayoutData(UIUtils.createGridData(2));
		
		new Label(editArea, SWT.NULL).setText(DBPlugin.getResourceString("dialog.dommain.editDommain.name"));
		dommainName = new Text(editArea, SWT.BORDER);
		dommainName.setLayoutData(UIUtils.createGridData(3));
		dommainName.addFocusListener(new FocusAdapter(){
			@Override public void focusLost(FocusEvent e) {
				applyToDommainModel();
			}
		});
		
		new Label(editArea, SWT.NULL).setText(DBPlugin.getResourceString("dialog.dommain.editDommain.type"));
		columnType = new Combo(editArea, SWT.READ_ONLY);
		for(int i=0;i<dialect.getColumnTypes().length;i++){
			IColumnType type = dialect.getColumnTypes()[i];
			columnType.add(type.toString());
		}
		columnType.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				applyToDommainModel();
			}
		});
		columnType.setLayoutData(UIUtils.createGridData(1));
		
		new Label(editArea, SWT.NULL).setText(DBPlugin.getResourceString("dialog.dommain.editDommain.size"));
		columnSize = new Text(editArea, SWT.BORDER);
		columnSize.addFocusListener(new FocusAdapter(){
			@Override public void focusLost(FocusEvent e) {
				applyToDommainModel();
			}
		});
		GridData gd = new GridData();
		gd.widthHint = 80;
		columnSize.setLayoutData(gd);
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(SelectionChangedEvent event) {
				updateComponents();
			}
		});
		if(editingModel != null){
			viewer.setSelection(new StructuredSelection(editingModel));
		}
		
		updateComponents();
		return composite;
	}
	
	private void updateComponents(){
		IStructuredSelection sel = (IStructuredSelection) viewer.getSelection();
		removeButton.setEnabled(!sel.isEmpty());
		if(sel.isEmpty()){
			editingModel = null;
			dommainName.setEnabled(false);
			dommainName.setText("");
			columnType.setEnabled(false);
			columnType.setText("");
			columnSize.setEnabled(false);
			columnSize.setText("");
		} else {
			editingModel = (DommainModel) sel.getFirstElement();
			dommainName.setEnabled(true);
			dommainName.setText(editingModel.getName());
			columnType.setEnabled(true);
			columnType.setText(editingModel.getType().toString());
			columnSize.setEnabled(editingModel.getType().supportSize());
			columnSize.setText(editingModel.getSize());
		}
	}
	
	private void applyToDommainModel(){
		editingModel.setName(dommainName.getText());
		editingModel.setType(dialect.getColumnTypes()[columnType.getSelectionIndex()]);
		editingModel.setSize(columnSize.getText());
		columnSize.setEnabled(editingModel.getType().supportSize());
		viewer.refresh();
	}
	
	private String getDommainId(){
		return String.valueOf(new Date().getTime()) + result.size();
	}
	
	public List<DommainModel> getResult(){
		return this.result;
	}

}
