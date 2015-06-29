package net.java.amateras.db.visual.action;

import java.util.ArrayList;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.ModelUtils;
import net.java.amateras.db.util.UIUtils;
import net.java.amateras.db.visual.editor.VisualDBSerializer;
import net.java.amateras.db.visual.model.AbstractDBEntityModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jdt.internal.ui.wizards.TypedElementSelectionValidator;
import org.eclipse.jdt.internal.ui.wizards.buildpaths.FolderSelectionDialog;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * Imports table models from other diagram.
 *
 * @author Naoki Takezoe
 * @since 1.0.4
 */
public class ImportFromDiagramAction extends Action {

    private GraphicalViewer viewer;

    public ImportFromDiagramAction(GraphicalViewer viewer){
        super(DBPlugin.getResourceString("action.importFromDiagram"));
        this.viewer = viewer;
    }

    @Override public void run() {
		IFileEditorInput input = (IFileEditorInput) UIUtils.getActiveEditor().getEditorInput();
    	IFile file = input.getFile();
		RootModel root = (RootModel) viewer.getContents().getModel();

    	ImportFromDiagramWizard wizard = new ImportFromDiagramWizard(
    			root, file, viewer.getEditDomain().getCommandStack());
		WizardDialog dialog = new WizardDialog(
				viewer.getControl().getShell(), wizard);
		dialog.open();
    }

	private static class ImportFromDiagramWizard extends Wizard {

		private ImportFromDiagramWizardPage page;
		private RootModel model;
		private IFile file;
		private CommandStack stack;

		public ImportFromDiagramWizard(RootModel model, IFile file, CommandStack stack) {
			super();
			setNeedsProgressMonitor(true);
			setWindowTitle(DBPlugin.getResourceString("wizard.new.import.title"));
			this.model = model;
			this.file = file;
			this.stack = stack;
		}

		public void addPages() {
			page = new ImportFromDiagramWizardPage(file, model);
			addPage(page);
		}

		public boolean performFinish() {
			final TableModel[] selectedTables = page.getSelectedTableModel();
			final IFile file = page.getSelectedFile();

			stack.execute(new Command(){
				@Override public void execute() {
					for(TableModel newTable: selectedTables){
						TableModel oldTable = model.getTable(newTable.getTableName());
                    	ModelUtils.importOrReplaceTable(model, oldTable, newTable);
						newTable.setLinkedPath(file.getFullPath().toString());
					}
				}

				@Override public boolean canUndo(){
					return false;
				}
			});

			return true;
		}
	}

	private static class ImportFromDiagramWizardPage extends WizardPage {

		private IFile self;

		private RootModel root;
		private RootModel selectedRootModel;
		private Text file;
		private List list;

		public ImportFromDiagramWizardPage(IFile self, RootModel root){
			super(DBPlugin.getResourceString("wizard.importFromDiagram.title"));
			setTitle(DBPlugin.getResourceString("wizard.importFromDiagram.title"));
			setMessage(DBPlugin.getResourceString("wizard.importFromDiagram.message"));
			this.self = self;
			this.root = root;
		}

		public void createControl(Composite parent) {
			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayout(new GridLayout(3, false));
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));

			UIUtils.createLabel(composite, DBPlugin.getResourceString("wizard.importFromDiagram.erdFile"));
			file = new Text(composite, SWT.BORDER);
			file.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			file.setEditable(false);

			Button browse = new Button(composite, SWT.PUSH);
			browse.setText(DBPlugin.getResourceString("button.browse"));
			browse.addSelectionListener(new SelectionAdapter(){
				@Override public void widgetSelected(SelectionEvent e) {
					selectFile();
				}
			});

			UIUtils.createLabel(composite, DBPlugin.getResourceString("wizard.new.import.tables"));
			list = new List(composite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
			list.setLayoutData(new GridData(GridData.FILL_BOTH));

			list.addSelectionListener(new SelectionAdapter(){
                @Override public void widgetSelected(SelectionEvent e) {
                    String[] selectedTableNames = list.getSelection();
                    for(String tableName: selectedTableNames){
                        TableModel table = root.getTable(tableName);
                        if(table != null && !table.isLinkedTable()){
                            setErrorMessage(DBPlugin.getResourceString("wizard.importFromDiagram.error.existTable"));
                            setPageComplete(false);
                            return;
                        }
                        setErrorMessage(null);
                        setPageComplete(true);
                    }
                }
			});

			setControl(composite);
		}

		private void selectFile() {
			try {
				IResource init = null;
				if(!file.getText().equals("")){
					init = getSelectedFile();
				}
				Class<?>[] acceptedClasses = new Class<?>[] { IFile.class };
				ISelectionStatusValidator validator = new TypedElementSelectionValidator(acceptedClasses, false);

				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
				FolderSelectionDialog dialog = new FolderSelectionDialog(
						getShell(),
						new WorkbenchLabelProvider(),
						new WorkbenchContentProvider());

				ViewerFilter filter = new ViewerFilter(){
					@Override public boolean select(Viewer viewer, Object parentElement,
							Object element) {
						if(element instanceof IContainer){
							return true;
						}
						return element instanceof IFile && !element.equals(self) &&
							((IFile) element).getName().endsWith(".erd");
					}
				};

				dialog.setTitle(DBPlugin.getResourceString("wizard.generate.browse.title"));
				dialog.setMessage(DBPlugin.getResourceString("wizard.generate.browse.message"));
				dialog.addFilter(filter);
				dialog.setInput(wsroot);
				dialog.setValidator(validator);
				dialog.setInitialSelection(init);
				if (dialog.open() == Window.OK) {
					IFile selectedFile = (IFile) dialog.getFirstResult();
					file.setText(selectedFile.getFullPath().toString());

					selectedRootModel = VisualDBSerializer.deserialize(selectedFile.getContents());
					list.removeAll();
					for(AbstractDBEntityModel entity: selectedRootModel.getChildren()){
						if(entity instanceof TableModel){
							list.add(((TableModel) entity).getTableName());
						}
					}
				}

			} catch (Exception ex) {
				DBPlugin.logException(ex);
			}
		}

		public IFile getSelectedFile(){
			String outputDir = file.getText();
			IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
			return (IFile) wsroot.findMember(outputDir);
		}

		public TableModel[] getSelectedTableModel(){
			if(selectedRootModel==null){
				return new TableModel[0];
			}
			java.util.List<TableModel> result = new ArrayList<TableModel>();
			for(String tableName: list.getSelection()){
				TableModel table = selectedRootModel.getTable(tableName);
				table.setSchema(selectedRootModel.getJdbcSchema());
				result.add(selectedRootModel.getTable(tableName));
			}
			return result.toArray(new TableModel[result.size()]);
		}
	}

}
