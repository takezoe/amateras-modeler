package net.java.amateras.db.visual.action;

import java.sql.SQLException;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.wizard.NewDiagramWizardPage2;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.MessageBox;

/**
 * 
 * @author Naoki Takezoe
 */
public class ImportFromJDBCAction extends Action {
	
	private GraphicalViewer viewer;
	
	public ImportFromJDBCAction(GraphicalViewer viewer){
		super(DBPlugin.getResourceString("action.importFromDB"));
		this.viewer = viewer;
	}
	
	public void run() {
		ImportFromJDBCWizard wizard = new ImportFromJDBCWizard();
		WizardDialog dialog = new WizardDialog(viewer.getControl().getShell(), wizard);
		dialog.open();
	}
	
	private class ImportFromJDBCWizard extends Wizard {
		
		private NewDiagramWizardPage2 page;
		
		public ImportFromJDBCWizard() {
			super();
			setNeedsProgressMonitor(true);
			setWindowTitle(DBPlugin.getResourceString("wizard.new.import.title"));
		}
		
		public void addPages() {
			RootModel root = (RootModel) viewer.getContents().getModel();
			page = new NewDiagramWizardPage2(root);
			addPage(page);
		}

		public boolean performFinish() {
			RootModel root = (RootModel) viewer.getContents().getModel();
			try {
				page.importTables(root);
				UIUtils.getActiveEditor().doSave(new NullProgressMonitor());
				return true;
			} catch(SQLException ex){
				DBPlugin.logException(ex);
				MessageBox msg = new MessageBox(getShell());
				msg.setMessage(ex.getMessage());
				msg.open();
				return false;
			}
		}
	}

}
