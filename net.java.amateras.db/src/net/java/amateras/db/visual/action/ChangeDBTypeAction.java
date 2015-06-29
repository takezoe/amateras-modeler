package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.dialect.IColumnType;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.ColumnModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * Changes a database dialect and convert column types 
 * from the old dialect to the new dialect.
 * 
 * @author Naoki Takezoe
 */
public class ChangeDBTypeAction extends Action {
	
	private GraphicalViewer viewer;
	
	public ChangeDBTypeAction(GraphicalViewer viewer){
		super(DBPlugin.getResourceString("action.changeDatabaseType"));
		this.viewer = viewer;
	}
	
	public void run() {
		RootModel root = (RootModel) viewer.getContents().getModel();
		
		WizardDialog dialog = new WizardDialog(
				viewer.getControl().getShell(), new ChangeDBTypeWizard(root));
		
		dialog.open();
	}
	
	private static IColumnType getColumnType(IDialect dialect, TableModel table, ColumnModel column){
		IColumnType type = dialect.getColumnType(column.getColumnType().getType());
		if(type == null){
			type = dialect.getDefaultColumnType();
		}
		return type;
	}
	
	private class ChangeDBTypeWizard extends Wizard {
		
		private RootModel root;
		private ChangeDBTypeWizardPage page;
		
		public ChangeDBTypeWizard(RootModel rootModel){
			setWindowTitle(DBPlugin.getResourceString("wizard.changedb.title"));
			this.root = rootModel;
		}
		
		@Override
		public void addPages() {
			page = new ChangeDBTypeWizardPage(root.getDialectName());
			addPage(page);
		}

		@Override
		public boolean performFinish() {
			String result = page.dbType.getText();
			
			if(!result.equals(root.getDialectName())){
				IDialect dialect = DialectProvider.getDialect(result);
				for(AbstractDBEntityModel entity: root.getChildren()){
					if(entity instanceof TableModel){
						TableModel table = (TableModel) entity;
						for(ColumnModel column: table.getColumns()){
							IColumnType type = getColumnType(dialect, table, column);
							column.setColumnType(type);
//							if(!type.supportSize()){
//								column.setSize("");
//							}
						}
						table.setColumns(table.getColumns());
					}
				}
				root.setDialectName(result);
			}
			
			return true;
		}
		
	}
	
	private class ChangeDBTypeWizardPage extends WizardPage {

		private Combo dbType;
		private String dbName;
		
		public ChangeDBTypeWizardPage(String dbName) {
			super("ChangeDBTypeWizardPage");
			setTitle(DBPlugin.getResourceString("wizard.changedb.title"));
			setDescription(DBPlugin.getResourceString("wizard.changedb.description"));
			this.dbName = dbName;
		}
		
		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(2, false));
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			
			UIUtils.createLabel(composite, DBPlugin.getResourceString("wizard.changedb.databaseType"));
			
			dbType = new Combo(composite, SWT.READ_ONLY);
			for(String dbName: DialectProvider.getDialectNames()){
				dbType.add(dbName);
			}
			
			dbType.setText(dbName);
			
			setControl(composite);
		}
		
	}
	
}
