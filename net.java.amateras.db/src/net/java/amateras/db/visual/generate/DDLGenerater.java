package net.java.amateras.db.visual.generate;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class DDLGenerater implements IGenerator {

	public String getGeneratorName() {
		return "DDL";
	}

	public void execute(IFile erdFile, RootModel root, GraphicalViewer viewer) {
		WizardDialog dialog = new WizardDialog(null,new DDLWizard(erdFile, root));
		dialog.open();
	}

	private class DDLWizard extends Wizard {

		private IFile erdFile;
		private RootModel root;
		private DDLWizardPage page;

		public DDLWizard(IFile erdFile, RootModel root){
			this.erdFile = erdFile;
			this.root = root;
			setWindowTitle(getGeneratorName());

			IDialogSettings settings = DBPlugin.getDefault().getDialogSettings();
			IDialogSettings section = settings.getSection("DDLWizard");
			if (section==null){
			    section = settings.addNewSection("DDLWizard");
				section.put("schema", false);
				section.put("drop", false);
				section.put("alterTable", true);
				section.put("comment", true);
				section.put("encoding", System.getProperty("file.encoding"));
			}
			this.setDialogSettings(section);
		}

		public void addPages() {
			this.page = new DDLWizardPage(erdFile);
			addPage(page);
		}

		public boolean performFinish() {
			try {
				IPath path = page.getOutputFolderResource().getFullPath();
				path = path.append(page.filename.getText());

				String ddl = DialectProvider.getDialect(root.getDialectName()).createDDL(
						root, page.schema.getSelection(), page.drop.getSelection(),
						page.alterTable.getSelection(), page.comment.getSelection());

				IDialogSettings section = getDialogSettings();
				section.put("schema", page.schema.getSelection());
				section.put("drop", page.drop.getSelection());
				section.put("alterTable", page.alterTable.getSelection());
				section.put("comment", page.comment.getSelection());
				section.put("encoding", page.encoding.getText());

				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
				IFile file = wsroot.getFile(path);

				if(file.exists()){
					if(!MessageDialog.openConfirm(null, DBPlugin.getResourceString("wizard.generate.ddl.confirm.title"),
							DBPlugin.getDefault().createMessage("wizard.generate.ddl.confirm.message",
									new String[]{page.filename.getText()}))){
						return false;
					}
				}

				if(file.exists()){
					file.setContents(new ByteArrayInputStream(ddl.getBytes(page.encoding.getText())),
							true, true, new NullProgressMonitor());
					file.setCharset(page.encoding.getText(), null);
				} else {
					file.create(new ByteArrayInputStream(ddl.getBytes(page.encoding.getText())),
							true,new NullProgressMonitor());
					file.setCharset(page.encoding.getText(), null);
				}

				file.getParent().refreshLocal(1, new NullProgressMonitor());

				return true;

			} catch(Exception ex){
				DBPlugin.logException(ex);
				return false;
			}
		}
	}

	private class DDLWizardPage extends FolderSelectWizardPage {

		private Text filename;
		private Button comment;
		private Button drop;
		private Button alterTable;
		private Button schema;
		private Text encoding;

		public DDLWizardPage(IFile erdFile) {
			super(erdFile, DBPlugin.getResourceString("wizard.generate.ddl.title"));
			setDescription(DBPlugin.getResourceString("wizard.generate.ddl.description"));
		}

		public void createControl(Composite parent) {
			super.createControl(parent);

			IDialogSettings section = getDialogSettings();
			Composite composite = (Composite)getControl();

			Label label = new Label(composite, SWT.NULL);
			label.setText(DBPlugin.getResourceString("wizard.generate.ddl.filename"));

			filename = new Text(composite, SWT.BORDER);
			filename.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			filename.setText(erdFile.getName().replaceFirst("\\.erd$", ".ddl"));
			filename.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					doValidate();
				}
			});

			new Label(composite, SWT.NULL);

			new Label(composite, SWT.NULL).setText(DBPlugin.getResourceString("wizard.generate.ddl.encoding"));
			encoding = new Text(composite, SWT.BORDER);
			encoding.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			encoding.setText(section.get("encoding"));
			encoding.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent e) {
					doValidate();
				}
			});

			schema = new Button(composite, SWT.CHECK);
			schema.setText(DBPlugin.getResourceString("wizard.generate.ddl.schema"));
			schema.setLayoutData(UIUtils.createGridData(2));
			schema.setSelection(section.getBoolean("schema"));

			drop = new Button(composite, SWT.CHECK);
			drop.setText(DBPlugin.getResourceString("wizard.generate.ddl.dropTable"));
			drop.setLayoutData(UIUtils.createGridData(2));
			drop.setSelection(section.getBoolean("drop"));

			alterTable = new Button(composite, SWT.CHECK);
			alterTable.setText(DBPlugin.getResourceString("wizard.generate.ddl.alterTable"));
			alterTable.setLayoutData(UIUtils.createGridData(2));
			alterTable.setSelection(section.getBoolean("alterTable"));

			comment = new Button(composite, SWT.CHECK);
			comment.setText(DBPlugin.getResourceString("wizard.generate.ddl.comment"));
			comment.setLayoutData(UIUtils.createGridData(2));
			comment.setSelection(section.getBoolean("comment"));
		}

		@Override protected void doValidate(){
			super.doValidate();
			if(filename.getText().length() == 0){
				setErrorMessage(DBPlugin.getResourceString("wizard.generate.ddl.error.filename"));
				setPageComplete(false);
				return;
			}
			if(!isSupportedEncoding(encoding.getText())){
				setErrorMessage(DBPlugin.getResourceString("wizard.generate.ddl.error.encoding"));
				setPageComplete(false);
				return;
			}
		}


	}

	private static boolean isSupportedEncoding(String encoding) {
		try {
			new String(new byte[0], encoding);
		} catch (UnsupportedEncodingException ex) {
			return false;
		}
		return true;
	}

}
