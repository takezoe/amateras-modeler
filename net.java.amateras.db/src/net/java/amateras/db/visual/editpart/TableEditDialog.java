package net.java.amateras.db.visual.editpart;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.dialect.IColumnType;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.dialect.IIndexType;
import net.java.amateras.db.dialect.IndexType;
import net.java.amateras.db.sqleditor.SQLConfiguration;
import net.java.amateras.db.sqleditor.SQLPartitionScanner;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.DommainModel;
import net.java.amateras.db.visual.model.IndexModel;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 *
 * @author Naoki Takezoe
 */
public class TableEditDialog extends Dialog {

	private String tableName;
	private String tableLogicalName;
	private String tableDescription;
	private List<ColumnModel> columnModels = new ArrayList<ColumnModel>();
	private int editColumnIndex = -1;
	private String sql;

	private Text txtTableName;
	private Text txtTableLogicalName;
	private Text txtTableDescription;
	private Table tblColumns;
	private Text txtColumnName;
	private Text txtColumnLogicalName;
	private Combo cmbColumnType;
	private Text txtColumnSize;
	private Button chkNotNull;
	private Button chkIsPK;
	private Button autoIncrement;
	private Text defaultValue;
	private Text txtColumnDescription;

	private org.eclipse.swt.widgets.List indexList;
	private Text indexName;
//	private TableViewer indexColumns;
	private Combo indexType;
	private org.eclipse.swt.widgets.List selectedColumns;

	private StyledText txtSql;

	private List<IndexModel> indexModels = new ArrayList<IndexModel>();
	private int editIndexIndex = -1;

	private RootModel rootModel;
	private IDialect dialect;

	private FocusListener updateColumnListener = new FocusAdapter(){
		@Override public void focusLost(FocusEvent e) {
			updateColumn();
		}
	};

	private SelectionListener updateColumnListener2 = new SelectionAdapter(){
		@Override public void widgetSelected(SelectionEvent e) {
			updateColumn();
		}
	};

	private Button btnDelColumn;
	private Button btnUpColumn;
	private Button btnDownColumn;
	private Button btnAddColumn;

	private Button addIndex;
	private Button delIndex;
	private Button indexUpButton;
	private Button indexDownButton;
	private Button indexAddButton;
	private Button indexRemoveButton;

	private boolean indexEditing = false;

	/**
	 * The constructor.
	 *
	 * @param parentShell the parent shell
	 * @param rootModel the root model
	 * @param tableName the table name
	 * @param tableLogicalName the logical table name
	 * @param tableDescription the table description
	 * @param columns the column models
	 * @param editColumn the editing target column model
	 * @param indices the index models
	 * @param sql SQL
	 */
	public TableEditDialog(Shell parentShell, RootModel rootModel, String tableName,
			String tableLogicalName, String tableDescription, ColumnModel[] columns,
			ColumnModel editColumn, IndexModel[] indices, boolean indexEditing, IndexModel editIndex,
			String sql) {
		super(parentShell);
		setShellStyle(getShellStyle()|SWT.RESIZE);
		this.tableName = tableName;
		this.tableLogicalName = tableLogicalName;
		this.tableDescription = tableDescription;
		this.rootModel = rootModel;
		this.dialect = DialectProvider.getDialect(rootModel.getDialectName());

		for(int i=0;i<columns.length;i++){
			columnModels.add(columns[i]);
			if(editColumn != null && editColumn == columns[i]){
				editColumnIndex = i;
			}
		}
		for(int i=0;i<indices.length;i++){
			indexModels.add(indices[i]);
			if(editIndex != null && indices[i] == editIndex){
				editIndexIndex = i;
			}
		}

		this.indexEditing = indexEditing;
		this.sql = sql;
	}

	@Override protected void constrainShellSize() {
		Shell shell = getShell();
		shell.pack();
		shell.setSize(shell.getSize().x, 450);
	}

	private void syncColumnModelsToTable(){
		tblColumns.removeAll();

		for(int j=0;j<columnModels.size();j++){
			ColumnModel model = (ColumnModel)columnModels.get(j);
			TableItem item = new TableItem(tblColumns, SWT.NULL);
			updateTableItem(item, model);
		}
	}

	@Override protected Control createDialogArea(Composite parent) {
		getShell().setText(DBPlugin.getResourceString("dialog.table.title"));

		final TabFolder tabFolder = new TabFolder(parent, SWT.NULL);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		////////////////////////////////////////////////////////////////
		// Table tab
		////////////////////////////////////////////////////////////////
		TabItem tab1 = new TabItem(tabFolder, SWT.NULL);
		tab1.setText(DBPlugin.getResourceString("label.table"));

		Composite composite1 = new Composite(tabFolder, SWT.NULL);
		composite1.setLayout(new GridLayout(2, false));
		composite1.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab1.setControl(composite1);

		UIUtils.createLabel(composite1, "dialog.table.tableLogicalName");
		txtTableLogicalName = new Text(composite1, SWT.BORDER);
		txtTableLogicalName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtTableLogicalName.setText(tableLogicalName);

		UIUtils.createLabel(composite1, "dialog.table.tableName");

		txtTableName = new Text(composite1, SWT.BORDER);
		txtTableName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtTableName.setText(tableName);

		UIUtils.createLabel(composite1, "dialog.table.description");

		txtTableDescription = new Text(composite1, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER);
		txtTableDescription.setLayoutData(new GridData(GridData.FILL_BOTH));
		txtTableDescription.setText(tableDescription);

		////////////////////////////////////////////////////////////////
		// Column tab
		////////////////////////////////////////////////////////////////
		TabItem tab2 = new TabItem(tabFolder, SWT.NULL);
		tab2.setText(DBPlugin.getResourceString("label.column"));

		Composite composite2 = new Composite(tabFolder, SWT.NULL);
		composite2.setLayout(new GridLayout());
		composite2.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab2.setControl(composite2);

		Composite tableArea = new Composite(composite2, SWT.NULL);
		GridLayout layout = new GridLayout(2, false);
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		tableArea.setLayout(layout);
		tableArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		tblColumns = new Table(tableArea, SWT.BORDER|SWT.SINGLE|SWT.FULL_SELECTION);
		tblColumns.setLayoutData(new GridData(GridData.FILL_BOTH));
		tblColumns.setHeaderVisible(true);

		UIUtils.createColumn(tblColumns, "dialog.table.columnLogicalName", 150);
		UIUtils.createColumn(tblColumns, "dialog.table.columnName", 150);
		UIUtils.createColumn(tblColumns, "dialog.table.columnType", 150);
		UIUtils.createColumn(tblColumns, "dialog.table.columnPK", 40);
		UIUtils.createColumn(tblColumns, "dialog.table.columnNotNull", 40);

		for(int i=0;i<columnModels.size();i++){
			ColumnModel model = (ColumnModel)columnModels.get(i);
			TableItem item = new TableItem(tblColumns, SWT.NULL);
			updateTableItem(item, model);
		}

		tblColumns.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				tableSelectionChanged();
			}
		});

		Composite buttons = new Composite(tableArea, SWT.NULL);

		GridLayout buttonsLayout = new GridLayout(1, false);
		buttonsLayout.horizontalSpacing = 0;
		buttonsLayout.verticalSpacing = 0;
		buttonsLayout.marginHeight = 0;
		buttonsLayout.marginWidth = 2;
		buttons.setLayout(buttonsLayout);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		btnAddColumn = new Button(buttons, SWT.PUSH);
		btnAddColumn.setText(DBPlugin.getResourceString("dialog.table.addColumn"));
		btnAddColumn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnAddColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				ColumnModel column = new ColumnModel();
				column.setColumnName("COLUMN_" + (columnModels.size() + 1));
				column.setLogicalName(DBPlugin.getResourceString("label.column") + (columnModels.size() + 1));
				column.setColumnType(dialect.getDefaultColumnType());
				column.setSize("10");

				int i = tblColumns.getSelectionIndex();
				if(i == -1){
					columnModels.add(column);
					TableItem item = new TableItem(tblColumns, SWT.NULL);
					updateTableItem(item, column);

					tblColumns.setSelection(columnModels.size() - 1);
					tableSelectionChanged();

				} else {
					columnModels.add(i + 1, column);
					syncColumnModelsToTable();

					tblColumns.setSelection(i + 1);
					tableSelectionChanged();
				}
			}
		});

		btnDelColumn = new Button(buttons, SWT.PUSH);
		btnDelColumn.setText(DBPlugin.getResourceString("dialog.table.removeColumn"));
		btnDelColumn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnDelColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				columnModels.remove(tblColumns.getSelectionIndex());
				tblColumns.remove(tblColumns.getSelectionIndex());
				disableColumnForm();
				updateButtons();
			}
		});

		btnUpColumn = new Button(buttons, SWT.PUSH);
		btnUpColumn.setText(DBPlugin.getResourceString("dialog.table.upColumn"));
		btnUpColumn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnUpColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				int i = tblColumns.getSelectionIndex();
				ColumnModel column = columnModels.get(i);
				columnModels.remove(i);
				columnModels.add(i - 1, column);
				syncColumnModelsToTable();

				tblColumns.setSelection(i - 1);
				tableSelectionChanged();
			}
		});

		btnDownColumn = new Button(buttons, SWT.PUSH);
		btnDownColumn.setText(DBPlugin.getResourceString("dialog.table.downColumn"));
		btnDownColumn.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		btnDownColumn.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				int i = tblColumns.getSelectionIndex();
				ColumnModel column = columnModels.get(i);
				columnModels.remove(i);
				columnModels.add(i + 1, column);
				syncColumnModelsToTable();

				tblColumns.setSelection(i + 1);
				tableSelectionChanged();
			}
		});


		Group group = new Group(composite2, SWT.NULL);
		group.setText(DBPlugin.getResourceString("dialog.table.editColumn"));
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(6, false));

		UIUtils.createLabel(group, "dialog.table.editColumn.logicalName");
		txtColumnLogicalName = new Text(group, SWT.BORDER);
		txtColumnLogicalName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtColumnLogicalName.addFocusListener(updateColumnListener);

		// fill margin
		new Label(group, SWT.NULL);
		new Label(group, SWT.NULL);
		new Label(group, SWT.NULL);
		new Label(group, SWT.NULL);

		UIUtils.createLabel(group, "dialog.table.editColumn.name");
		txtColumnName = new Text(group, SWT.BORDER);
		txtColumnName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		txtColumnName.addFocusListener(updateColumnListener);

		UIUtils.createLabel(group, "dialog.table.editColumn.type");
		cmbColumnType = new Combo(group, SWT.READ_ONLY);
		for(DommainModel domman: rootModel.getDommains()){
			cmbColumnType.add(domman.toString());
		}
		for(int i=0;i<dialect.getColumnTypes().length;i++){
			IColumnType type = dialect.getColumnTypes()[i];
			cmbColumnType.add(type.toString());
		}
		cmbColumnType.addSelectionListener(updateColumnListener2);

		UIUtils.createLabel(group, "dialog.table.editColumn.size");
		txtColumnSize = new Text(group, SWT.BORDER);
		txtColumnSize.setLayoutData(UIUtils.createGridDataWithWidth(60));
		txtColumnSize.addFocusListener(updateColumnListener);

		UIUtils.createLabel(group, "dialog.table.description");
		txtColumnDescription = new Text(group, SWT.MULTI|SWT.V_SCROLL|SWT.H_SCROLL|SWT.BORDER);
		txtColumnDescription.setLayoutData(UIUtils.createGridDataWithColspan(5, 40));
		txtColumnDescription.addFocusListener(updateColumnListener);

		Composite checks = new Composite(group, SWT.NULL);
		checks.setLayout(new GridLayout(5, false));
		checks.setLayoutData(UIUtils.createGridData(6));

		chkIsPK = new Button(checks, SWT.CHECK);
		chkIsPK.setText(DBPlugin.getResourceString("dialog.table.editColumn.PK"));
		chkIsPK.addSelectionListener(updateColumnListener2);

		chkNotNull = new Button(checks, SWT.CHECK);
		chkNotNull.setText(DBPlugin.getResourceString("dialog.table.editColumn.notNull"));
		chkNotNull.addSelectionListener(updateColumnListener2);

		autoIncrement = new Button(checks, SWT.CHECK);
		autoIncrement.setText(DBPlugin.getResourceString("dialog.table.editColumn.autoIncrement"));
		autoIncrement.addSelectionListener(updateColumnListener2);

		new Label(checks, SWT.NULL).setText(DBPlugin.getResourceString("dialog.table.editColumn.defaultValue"));
		defaultValue = new Text(checks, SWT.BORDER);
		defaultValue.setLayoutData(UIUtils.createGridDataWithWidth(60));
		defaultValue.addFocusListener(updateColumnListener);

		if(editColumnIndex >= 0){
			tabFolder.setSelection(tab2);
			tblColumns.select(editColumnIndex);
			tableSelectionChanged();
		} else {
			disableColumnForm();
		}
		updateButtons();

		////////////////////////////////////////////////////////////////
		// Index tab
		////////////////////////////////////////////////////////////////
		TabItem tab3 = new TabItem(tabFolder, SWT.NULL);
		tab3.setText(DBPlugin.getResourceString("label.index"));

		Composite composite3 = new Composite(tabFolder, SWT.NULL);
		composite3.setLayout(new GridLayout(2, false));
		composite3.setLayoutData(new GridData(GridData.FILL_BOTH));
		tab3.setControl(composite3);

		Composite indexArea = new Composite(composite3, SWT.NULL);
		indexArea.setLayout(layout);
		indexArea.setLayoutData(UIUtils.createGridData(2, GridData.FILL_BOTH));

		indexList = new org.eclipse.swt.widgets.List(indexArea, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		indexList.setLayoutData(new GridData(GridData.FILL_BOTH));
		for(IndexModel index: indexModels){
			indexList.add(index.toString());
		}
		indexList.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				indexSelectionChanged();
			}
		});

		Composite indexButtons = new Composite(indexArea, SWT.NULL);
		indexButtons.setLayout(buttonsLayout);
		indexButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		addIndex = new Button(indexButtons, SWT.PUSH);
		addIndex.setText(DBPlugin.getResourceString("dialog.table.addIndex"));
		addIndex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addIndex.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent selectionevent) {
				IndexModel indexModel = new IndexModel();
				indexModel.setIndexType(new IndexType("UNIQUE"));
				indexModel.setIndexName("IDX_" + txtTableName.getText() + "_" + (indexModels.size() + 1));
				indexModels.add(indexModel);
				indexList.add(indexModel.toString());
			}
		});

		delIndex = new Button(indexButtons, SWT.PUSH);
		delIndex.setText(DBPlugin.getResourceString("dialog.table.removeIndex"));
		delIndex.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		delIndex.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent selectionevent) {
				int index = indexList.getSelectionIndex();
				indexList.remove(index);
				indexModels.remove(index);
				disableIndexForm();
			}
		});

		new Label(composite3, SWT.NULL).setText(DBPlugin.getResourceString("dialog.table.editIndex.indexName"));
		indexName = new Text(composite3, SWT.BORDER);
		indexName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexName.addFocusListener(new FocusAdapter(){
			@Override public void focusLost(FocusEvent e) {
				IndexModel model = indexModels.get(editIndexIndex);
				model.setIndexName(indexName.getText());
				indexList.setItem(editIndexIndex, model.toString());
			}
		});

		new Label(composite3, SWT.NULL).setText(DBPlugin.getResourceString("dialog.table.editIndex.indexType"));
		indexType = new Combo(composite3, SWT.READ_ONLY);
		for (int i=0;i<dialect.getIndexTypes().length; i++){
			IIndexType type = dialect.getIndexTypes()[i];
			indexType.add(type.getName());
		}
		indexType.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				IndexModel model = indexModels.get(editIndexIndex);
				model.setIndexType(dialect.getIndexType(indexType.getText()));
				indexList.setItem(editIndexIndex, model.toString());
			}
		});

		Group indexColumn = new Group(composite3, SWT.NULL);
		indexColumn.setText(DBPlugin.getResourceString("dialog.table.editIndex.indexColumns"));
		indexColumn.setLayout(new GridLayout(2, false));
		indexColumn.setLayoutData(UIUtils.createGridData(3));

		selectedColumns = new org.eclipse.swt.widgets.List(indexColumn, SWT.BORDER|SWT.V_SCROLL|SWT.H_SCROLL);
		selectedColumns.setLayoutData(UIUtils.createGridDataWithColspan(1, 80));
		selectedColumns.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				updateIndexColumnButtons();
			}
		});

		Composite indexColumnButtons = new Composite(indexColumn, SWT.NULL);
		indexColumnButtons.setLayout(buttonsLayout);
		indexColumnButtons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

		indexAddButton = new Button(indexColumnButtons, SWT.PUSH);
		indexAddButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexAddButton.setText(DBPlugin.getResourceString("dialog.table.addColumn"));
		indexAddButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				ColumnSelectDialog dialog = new ColumnSelectDialog(getShell(), columnModels);
				if(dialog.open() == Dialog.OK){
					if(dialog.getSelectedColumn() != null){
						String columnName = dialog.getSelectedColumn().getColumnName();
						IndexModel model = indexModels.get(editIndexIndex);
						model.getColumns().add(columnName);
						selectedColumns.add(columnName);

						indexList.setItem(editIndexIndex, model.toString());

						updateIndexColumnButtons();
					}
				}
			}
		});

		indexRemoveButton = new Button(indexColumnButtons, SWT.PUSH);
		indexRemoveButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexRemoveButton.setText(DBPlugin.getResourceString("dialog.table.removeColumn"));
		indexRemoveButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				int index = selectedColumns.getSelectionIndex();
				if(index >= 0){
					IndexModel model = indexModels.get(editIndexIndex);
					model.getColumns().remove(index);
					selectedColumns.remove(index);

					updateIndexColumnButtons();
				}
			}
		});

		indexUpButton = new Button(indexColumnButtons, SWT.PUSH);
		indexUpButton.setText(DBPlugin.getResourceString("dialog.table.upColumn"));
		indexUpButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexUpButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				int index = selectedColumns.getSelectionIndex();
				if(index > 0){
					IndexModel model = indexModels.get(editIndexIndex);
					String columnName = model.getColumns().get(index);

					model.getColumns().remove(index);
					model.getColumns().add(index - 1, columnName);

					selectedColumns.remove(index);
					selectedColumns.add(columnName, index - 1);
					selectedColumns.select(index - 1);

					indexList.setItem(editIndexIndex, model.toString());
					updateIndexColumnButtons();
				}
			}
		});

		indexDownButton = new Button(indexColumnButtons, SWT.PUSH);
		indexDownButton.setText(DBPlugin.getResourceString("dialog.table.downColumn"));
		indexDownButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		indexDownButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {
				int index = selectedColumns.getSelectionIndex();
				if(index < indexModels.get(editIndexIndex).getColumns().size() - 1){
					IndexModel model = indexModels.get(editIndexIndex);
					String columnName = model.getColumns().get(index);

					model.getColumns().remove(index);
					model.getColumns().add(index + 1, columnName);

					selectedColumns.remove(index);
					selectedColumns.add(columnName, index + 1);
					selectedColumns.select(index + 1);

					indexList.setItem(editIndexIndex, model.toString());
					updateIndexColumnButtons();
				}
			}
		});

		if(indexEditing){
			tabFolder.setSelection(tab3);
			if(editIndexIndex >= 0){
				indexList.select(editIndexIndex);
				indexSelectionChanged();
			} else {
				disableIndexForm();
			}
		} else {
			disableIndexForm();
		}

		////////////////////////////////////////////////////////////////
		// SQL tab
		////////////////////////////////////////////////////////////////
		TabItem tab4 = new TabItem(tabFolder, SWT.NULL);
		tab4.setText(DBPlugin.getResourceString("label.sql"));

		Composite sqlComposite = new Composite(tabFolder, SWT.NULL);
		sqlComposite.setLayout(new GridLayout(2, false));

		Composite editorArea = new Composite(sqlComposite, SWT.BORDER);
		editorArea.setLayout(new FillLayout());
		editorArea.setLayoutData(UIUtils.createGridData(2, GridData.FILL_BOTH));

		SourceViewer sqlEditor = new SourceViewer(editorArea, new VerticalRuler(0), SWT.V_SCROLL | SWT.H_SCROLL);
		sqlEditor.configure(new SQLConfiguration());
		sqlEditor.getTextWidget().setFont(JFaceResources.getTextFont());

		Document document = new Document();
		IDocumentPartitioner partitioner = new FastPartitioner(
		        new SQLPartitionScanner(),
		        new String[] {
		        	SQLPartitionScanner.SQL_COMMENT,
		        	SQLPartitionScanner.SQL_STRING
		        });
		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
		sqlEditor.setDocument(document);

		txtSql = sqlEditor.getTextWidget();
		txtSql.addFocusListener(new FocusAdapter(){
			@Override public void focusLost(FocusEvent e) {
				sql = txtSql.getText();
			}
		});

		Button insertButton = new Button(sqlComposite, SWT.PUSH);
		insertButton.setText(DBPlugin.getResourceString("dialog.table.insertSql"));
		insertButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {

				StringBuilder columns = new StringBuilder();
				for(int i=0;i<columnModels.size();i++){
					if(columns.length() != 0){
						columns.append(", ");
					}
					columns.append(columnModels.get(i).getColumnName());
				}

				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ");
				sb.append(txtTableName.getText());
				sb.append(" (");
				sb.append(columns.toString());
				sb.append(") VALUES (");
				sb.append(columns.toString());
				sb.append(");");

				txtSql.insert(sb.toString());
				txtSql.setFocus();
			}
		});
		Button insertNotNullButton = new Button(sqlComposite, SWT.PUSH);
		insertNotNullButton.setText(DBPlugin.getResourceString("dialog.table.insertNotNullSql"));
		insertNotNullButton.addSelectionListener(new SelectionAdapter(){
			@Override public void widgetSelected(SelectionEvent e) {

				StringBuilder columns = new StringBuilder();
				for(int i=0;i<columnModels.size();i++){
					if(columnModels.get(i).isNotNull()){
						if(columns.length() != 0){
							columns.append(", ");
						}
						columns.append(columnModels.get(i).getColumnName());
					}
				}

				StringBuilder sb = new StringBuilder();
				sb.append("INSERT INTO ");
				sb.append(txtTableName.getText());
				sb.append(" (");
				sb.append(columns.toString());
				sb.append(") VALUES (");
				sb.append(columns.toString());
				sb.append(");");

				txtSql.insert(sb.toString());
				txtSql.setFocus();
			}
		});

		tab4.setControl(sqlComposite);
		txtSql.setText(sql);

		return tabFolder;
	}

	private void indexSelectionChanged() {
		editIndexIndex = indexList.getSelectionIndex();
		if(editIndexIndex >= 0){
			IndexModel model = indexModels.get(editIndexIndex);
			indexName.setEnabled(true);
			indexType.setEnabled(true);
			selectedColumns.setEnabled(true);
			selectedColumns.removeAll();

			indexName.setText(model.getIndexName());
			indexType.setText(model.getIndexType().getName());

			indexAddButton.setEnabled(true);
			delIndex.setEnabled(true);

			for(String columnName: model.getColumns()){
				selectedColumns.add(columnName);
			}
		} else {
			disableIndexForm();
		}
	}


	/**
	 * Updates status of index column up / down buttons.
	 */
	private void updateIndexColumnButtons(){
		indexRemoveButton.setEnabled(false);
		indexUpButton.setEnabled(false);
		indexDownButton.setEnabled(false);

		int index = selectedColumns.getSelectionIndex();
		if(index >= 0){
			indexRemoveButton.setEnabled(true);
			if(index > 0){
				indexUpButton.setEnabled(true);
			} else if(index < selectedColumns.getItemCount() - 1){
				indexDownButton.setEnabled(true);
			}
		}
	}

	/**
	 * Updates status of column control buttons.
	 */
	private void updateButtons(){
		btnDelColumn.setEnabled(false);
		btnUpColumn.setEnabled(false);
		btnDownColumn.setEnabled(false);
		int index = tblColumns.getSelectionIndex();
		if(index >= 0){
			btnDelColumn.setEnabled(true);
			if(index > 0){
				btnUpColumn.setEnabled(true);
			}
			if(index < columnModels.size() - 1){
				btnDownColumn.setEnabled(true);
			}
		}
	}

	/**
	 * Clears and disables the column editing form.
	 */
	private void disableColumnForm(){
		editColumnIndex = -1;

		txtColumnName.setText("");
		txtColumnLogicalName.setText("");
		cmbColumnType.setText("");
		txtColumnSize.setText("");
		chkIsPK.setSelection(false);
		chkNotNull.setSelection(false);
		autoIncrement.setSelection(false);
		defaultValue.setText("");

		txtColumnName.setEnabled(false);
		txtColumnLogicalName.setEnabled(false);
		cmbColumnType.setEnabled(false);
		txtColumnSize.setEnabled(false);
		txtColumnDescription.setEnabled(false);
		chkIsPK.setEnabled(false);
		chkNotNull.setEnabled(false);
		autoIncrement.setEnabled(false);
		defaultValue.setEnabled(false);
	}

	/**
	 * Clears and disables the index editing form.
	 */
	private void disableIndexForm(){
		editIndexIndex = -1;
		indexName.setText("");
		indexType.setText("");
		selectedColumns.removeAll();

		indexName.setEnabled(false);
		indexType.setEnabled(false);
		selectedColumns.setEnabled(false);

		delIndex.setEnabled(false);
		indexAddButton.setEnabled(false);
		indexRemoveButton.setEnabled(false);
		indexUpButton.setEnabled(false);
		indexDownButton.setEnabled(false);
	}

	private void updateColumn(){
		if(editColumnIndex != -1 && cmbColumnType.getSelectionIndex() != -1){
			ColumnModel model = (ColumnModel)columnModels.get(editColumnIndex);
			model.setColumnName(txtColumnName.getText());
			model.setLogicalName(txtColumnLogicalName.getText());

			int columnIndex = cmbColumnType.getSelectionIndex();
			if(columnIndex < rootModel.getDommains().size()){
				model.setDommain(rootModel.getDommains().get(columnIndex));
			} else {
				model.setDommain(null);
				model.setColumnType(dialect.getColumnTypes()
						[cmbColumnType.getSelectionIndex() - rootModel.getDommains().size()]);
			}

			model.setSize(txtColumnSize.getText());
			model.setPrimaryKey(chkIsPK.getSelection());
			model.setNotNull(chkNotNull.getSelection());
			model.setDescription(txtColumnDescription.getText());
			model.setAutoIncrement(autoIncrement.getSelection());
			model.setDefaultValue(defaultValue.getText());

			TableItem item = tblColumns.getItem(editColumnIndex);
			updateTableItem(item, model);

			if(model.getDommain() == null){
				txtColumnSize.setEnabled(model.getColumnType().supportSize());
			} else {
				txtColumnSize.setEnabled(false);
			}
		}
	}

	private void tableSelectionChanged(){
		int index = tblColumns.getSelectionIndex();
		if(index >= 0){
			ColumnModel model = columnModels.get(index);
			txtColumnName.setText(model.getColumnName());
			txtColumnLogicalName.setText(model.getLogicalName());
			if(model.getDommain() == null){
				cmbColumnType.setText(model.getColumnType().toString());
			} else {
				cmbColumnType.setText(model.getDommain().toString());
			}
			txtColumnSize.setText(String.valueOf(model.getSize()));
			txtColumnDescription.setText(model.getDescription());
			chkIsPK.setSelection(model.isPrimaryKey());
			chkNotNull.setSelection(model.isNotNull());
			autoIncrement.setSelection(model.isAutoIncrement());
			defaultValue.setText(model.getDefaultValue());
			editColumnIndex = index;

			txtColumnName.setEnabled(true);
			txtColumnLogicalName.setEnabled(true);
			cmbColumnType.setEnabled(true);
			if(model.getDommain()==null){
				txtColumnSize.setEnabled(model.getColumnType().supportSize());
			} else {
				txtColumnSize.setEnabled(false);
			}
			txtColumnDescription.setEnabled(true);
			chkIsPK.setEnabled(true);
			chkNotNull.setEnabled(true);
			autoIncrement.setEnabled(true);
			defaultValue.setEnabled(true);
		} else {
			disableColumnForm();
		}
		updateButtons();
	}

	protected void okPressed() {
		this.tableName = txtTableName.getText();
		this.tableLogicalName = txtTableLogicalName.getText();
		this.tableDescription = txtTableDescription.getText();
		super.okPressed();
	}

	/**
	 * Returns the edited table name.
	 *
	 * @return the edited table name
	 */
	public String getTableName(){
		return this.tableName;
	}

	/**
	 * Returns the edited table logical name.
	 *
	 * @return the edited table logical name
	 */
	public String getTableLogicalName(){
		return this.tableLogicalName;
	}

	/**
	 * Returns the edited table description.
	 *
	 * @return the edited table description
	 */
	public String getTableDescription(){
		return this.tableDescription;
	}

	/**
	 * Returns the list of edited column models.
	 *
	 * @return edited column models
	 */
	public List<ColumnModel> getResultColumns(){
		return this.columnModels;
	}

	/**
	 * Returns the list of edited index models.
	 *
	 * @return edited index models
	 */
	public List<IndexModel> getResultIncices(){
		return this.indexModels;
	}

	public String getSql(){
		return this.sql;
	}

	private void updateTableItem(TableItem item, ColumnModel model){
		StringBuilder sb = new StringBuilder();
		sb.append(model.getColumnType().getName());
		if(model.getColumnType().supportSize() && model.getSize().length() > 0){
			sb.append("(").append(model.getSize()).append(")");
		}

		item.setText(0, model.getLogicalName());
		item.setText(1, model.getColumnName());
		item.setText(2, sb.toString());
		item.setText(3, String.valueOf(model.isPrimaryKey()));
		item.setText(4, String.valueOf(model.isNotNull()));
	}

}
