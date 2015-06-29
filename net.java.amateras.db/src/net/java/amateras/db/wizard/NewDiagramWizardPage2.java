package net.java.amateras.db.wizard;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ResourceBundle;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.dialect.ISchemaLoader;
import net.java.amateras.db.util.DatabaseInfo;
import net.java.amateras.db.util.JarClassLoader;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

public class NewDiagramWizardPage2 extends WizardPage {

	private JarClassLoader classLoader;
	private DatabaseInfo dbinfo;
	private Button view;
	private Text jarFile;
	private Combo driver;
	private List list;
	private Text catalog;
	private Text schema;
	private Text password;
	private Text user;
	private Text databaseURI;
	private URL[] classpathes = new URL[0];
	private ResourceBundle url = ResourceBundle.getBundle("net.java.amateras.db.wizard.databaseURI");
	private Text filter;
	private Button autoConvert;

	private ArrayList<String> tableNames = new ArrayList<String>();

	private RootModel model;

	public NewDiagramWizardPage2(){
		this(null);
	}

	public NewDiagramWizardPage2(RootModel model){
		super(DBPlugin.getResourceString("wizard.new.import.title"));
		setTitle(DBPlugin.getResourceString("wizard.new.import.title"));
		setMessage(DBPlugin.getResourceString("wizard.new.import.message"));
		this.model = model;
	}


	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));

		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.jarFile"));

		jarFile = new Text(container, SWT.BORDER | SWT.SINGLE);
		jarFile.setEditable(false);
		jarFile.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Button button = new Button(container, SWT.PUSH);
		button.setText(DBPlugin.getResourceString("button.browseFileSystem"));
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleFileSystemBrowse();
			}
		});

		Button button2 = new Button(container, SWT.PUSH);
		button2.setText(DBPlugin.getResourceString("button.browseWorkspace"));
		button2.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleWorkspaceBrowse();
			}
		});

		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.driver"));

		driver = new Combo(container, SWT.READ_ONLY);
		driver.setLayoutData(UIUtils.createGridData(3));
		driver.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if(Collections.list(url.getKeys()).contains(driver.getText())){
					String template = url.getString(driver.getText());
					databaseURI.setText(template);
				}
			}
		});
		driver.add("sun.jdbc.odbc.JdbcOdbc");
		driver.select(0);

		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.uri"));

		databaseURI = new Text(container, SWT.BORDER | SWT.SINGLE);
		databaseURI.setLayoutData(UIUtils.createGridData(3));
		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.user"));

		user = new Text(container, SWT.BORDER | SWT.SINGLE);
		user.setLayoutData(UIUtils.createGridData(3));
		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.pass"));

		password = new Text(container, SWT.BORDER | SWT.PASSWORD);
		password.setLayoutData(UIUtils.createGridData(3));
		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.schema"));

		schema = new Text(container, SWT.BORDER | SWT.SINGLE);
		schema.setLayoutData(UIUtils.createGridData(3));
		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.catalog"));

		catalog = new Text(container, SWT.BORDER | SWT.SINGLE);
		catalog.setLayoutData(UIUtils.createGridData(3));
		//-------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.view"));

		view = new Button(container, SWT.CHECK);
		view.setLayoutData(UIUtils.createGridData(3));
		//-------------
		new Label(container, SWT.NULL);
		Button load = new Button(container, SWT.PUSH);
		load.setText(DBPlugin.getResourceString("wizard.new.import.loadTables"));
		load.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					loadTables();
				} catch (Exception ex) {
					ex.printStackTrace();
					MessageBox msg = new MessageBox(getShell());
					msg.setMessage(ex.getMessage());
					msg.open();
				}
			}
		});

		new Label(container, SWT.NULL);
		new Label(container, SWT.NULL);

		//----------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.filter"));
		filter = new Text(container, SWT.BORDER);
		filter.setLayoutData(UIUtils.createGridData(3));
		filter.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String filterText = filter.getText();
				list.removeAll();
				for(String tableName: tableNames){
					if(tableName.startsWith(filterText)){
						list.add(tableName);
					}
				}
			}
		});

		//----------------
		UIUtils.createLabel(container, DBPlugin.getResourceString("wizard.new.import.tables"));
		list = new List(container, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		list.setLayoutData(UIUtils.createGridData(3));
		
		//----------------
		autoConvert = new Button(container, SWT.CHECK);
		autoConvert.setText(DBPlugin.getResourceString("wizard.new.import.autoConvert"));
		autoConvert.setLayoutData(UIUtils.createGridData(4));
		

		if(model != null){
			jarFile.setText(model.getJarFile());
			loadJdbcDriver();
			driver.setText(model.getJdbcDriver());
			databaseURI.setText(model.getJdbcUrl());
			user.setText(model.getJdbcUser());
			password.setText(model.getJdbcPassword());
			catalog.setText(model.getJdbcCatalog());
			schema.setText(model.getJdbcSchema());
			view.setSelection(model.isIncludeView());
		}

		setControl(container);
	}

	private void loadTables() throws Exception {
		if(classLoader!=null){
			Class<?> driverClass = classLoader.loadClass(driver.getText());
			dbinfo = new DatabaseInfo(driverClass);
			dbinfo.setURI(databaseURI.getText());
			dbinfo.setUser(user.getText());
			dbinfo.setPassword(password.getText());
			dbinfo.setCatalog(catalog.getText());
			dbinfo.setSchema(schema.getText());
			dbinfo.setEnableView(view.getSelection());
			dbinfo.setAutoConvert(autoConvert.getSelection());

			list.removeAll();
			tableNames.clear();
			filter.setText("");

			for(String tableName: dbinfo.loadTables()){
				list.add(tableName);
				tableNames.add(tableName);
			}
		}
	}

	private void loadJdbcDriver(){
		try {
			URL jarURL = null;
			String jarFilePath = jarFile.getText();

			if(jarFilePath.startsWith("workspace:")){
				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
				jarFilePath = jarFilePath.replaceFirst("^workspace:", "");

				IFile file = wsroot.getFile(new Path(jarFilePath));
				jarFilePath = file.getLocation().makeAbsolute().toString();

				jarURL = new URL("file:///" + jarFilePath);

			} else {
				jarURL = new URL("file:///" + jarFilePath);
			}

			URL[] clspath = new URL[classpathes.length + 1];
			clspath[0] = jarURL;
			for (int i = 0; i < classpathes.length; i++) {
				clspath[i + 1] = classpathes[i];
			}
			classLoader = new JarClassLoader(clspath);
			java.util.List<Class<?>> list = classLoader.getJDBCDriverClass(jarFilePath);
			driver.removeAll();
			for(Class<?> item: list){
//				if(!ArrayUtils.contains(driver.getItems(),item.getName())){
				if(Arrays.binarySearch(driver.getItems(),item.getName())<0){
					driver.add(item.getName());
				}
			}
			driver.add("sun.jdbc.odbc.JdbcOdbc");
			driver.select(0);
		} catch (Exception e1) {
			DBPlugin.logException(e1);
		}
	}

	/**
	 * Choose a jar file which contains the JDBC driver from local file system.
	 */
	private void handleFileSystemBrowse(){
		FileDialog dialog = new FileDialog(getShell());
		if(dialog.open()==null){
			return;
		}
		jarFile.setText(dialog.getFilterPath() + System.getProperty("file.separator") + dialog.getFileName());
		loadJdbcDriver();
	}

	/**
	 * Choose a jar file which contains the JDBC driver from workspace.
	 */
	private void handleWorkspaceBrowse(){
		try {
			IResource selectedResource = null;
			if(jarFile.getText().startsWith("workspace:")){
				String jarFilePath = jarFile.getText().replaceFirst("^workspace:", "");
				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = wsroot.getFile(new Path(jarFilePath));
				if(file != null && file.exists()){
					selectedResource = file;
				}
			}

			Class<?>[] acceptedClasses = new Class<?>[] { IFile.class };
			ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);

			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			FolderSelectionDialog dialog = new FolderSelectionDialog(
					getShell(),
					new WorkbenchLabelProvider(),
					new WorkbenchContentProvider());

			ViewerFilter filter = new ViewerFilter(){
				@Override
				public boolean select(Viewer viewer, Object parentElement,
						Object element) {
					if(element instanceof IContainer){
						return true;
					}
					return element instanceof IFile && //!element.equals(self) &&
						((IFile) element).getName().endsWith(".jar");
				}
			};

			dialog.setTitle(DBPlugin.getResourceString("wizard.generate.browse.title"));
			dialog.setMessage(DBPlugin.getResourceString("wizard.generate.browse.message"));
			dialog.addFilter(filter);
			dialog.setInput(wsroot);
			dialog.setValidator(validator);
			dialog.setInitialSelection(selectedResource);

			if (dialog.open() == Window.OK) {
				IFile selectedFile = (IFile) dialog.getFirstResult();
				jarFile.setText("workspace:"+selectedFile.getFullPath().toString());
				loadJdbcDriver();
			}

		} catch (Exception ex) {
			DBPlugin.logException(ex);
		}
	}

	public void importTables(RootModel root) throws SQLException {
		root.setJarFile(jarFile.getText());
		root.setJdbcDriver(driver.getText());
		root.setJdbcUrl(databaseURI.getText());
		root.setJdbcUser(user.getText());
		root.setJdbcPassword(password.getText());
		root.setJdbcCatalog(catalog.getText());
		root.setJdbcSchema(schema.getText());
		root.setIncludeView(view.getSelection());

		if(list.getSelection().length == 0){
			return;
		}

		IDialect dialect = DialectProvider.getDialect(root.getDialectName());
		ISchemaLoader loader = dialect.getSchemaLoader();
		Connection conn = null;
		
		try {
			conn = dbinfo.connect();
			loader.loadSchema(root, DialectProvider.getDialect(root.getDialectName()), conn, 
					list.getSelection(), dbinfo.getCatalog(), dbinfo.getSchema(), dbinfo.isAutoConvert());
		
		} catch(Exception ex){
			DBPlugin.logException(ex);
		
		} finally {
			if(conn != null){
				conn.close();
			}
		}
	}
}
