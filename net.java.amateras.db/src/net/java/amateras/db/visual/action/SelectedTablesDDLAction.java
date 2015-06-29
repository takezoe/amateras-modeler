package net.java.amateras.db.visual.action;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.sqleditor.SQLConfiguration;
import net.java.amateras.db.sqleditor.SQLPartitionScanner;
import net.java.amateras.db.visual.editpart.TableEditPart;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.text.source.VerticalRuler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class SelectedTablesDDLAction extends Action {

	private GraphicalViewer viewer;

	public SelectedTablesDDLAction(GraphicalViewer viewer) {
		super(DBPlugin.getResourceString("action.selectedTablesDDL"));
		this.viewer = viewer;
	}

	@Override public void run(){
		ISelection sel = viewer.getSelection();
		if(sel instanceof IStructuredSelection){
			List<TableModel> tableModels = new ArrayList<TableModel>();
			Object[] selected = ((IStructuredSelection) sel).toArray();
			for(Object obj: selected){
				if(obj instanceof TableEditPart){
					tableModels.add((TableModel) ((TableEditPart) obj).getModel());
				}
			}

			RootModel root = ((RootModel) viewer.getContents().getModel());
			IDialect dialect = DialectProvider.getDialect(root.getDialectName());

			StringBuilder sb = new StringBuilder();

			if(tableModels.isEmpty()){
				sb.append(dialect.createDDL(root, true, false, true, true));

			} else {
				StringBuilder additions = new StringBuilder();
				for(TableModel tableModel: tableModels){
					sb.append(dialect.createTableDDL(root, tableModel,
							false, false, true, true, additions));
				}
				if(additions.length() > 0){
					sb.append(System.getProperty("line.separator"));
					sb.append(additions.toString());
				}
			}

			DDLDisplayDialog dialog = new DDLDisplayDialog(
					Display.getDefault().getActiveShell(), sb.toString());
			dialog.open();
		}
	}

	private class DDLDisplayDialog extends Dialog {

		private String ddl;

		protected DDLDisplayDialog(Shell parentShell, String ddl) {
			super(parentShell);
			this.ddl = ddl;
			setShellStyle(getShellStyle()|SWT.RESIZE);
		}

		@Override protected Point getInitialSize() {
			return new Point(600, 450);
		}

		@Override protected Control createDialogArea(Composite parent) {
			getShell().setText("DDL");

			SourceViewer sqlEditor = new SourceViewer(parent, new VerticalRuler(0), SWT.V_SCROLL | SWT.H_SCROLL);
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
			sqlEditor.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));

			StyledText text = sqlEditor.getTextWidget();
			text.setText(ddl);
			text.setEditable(false);

			return text;
		}

		@Override protected void createButtonsForButtonBar(Composite parent) {
			createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		}
	}

}
