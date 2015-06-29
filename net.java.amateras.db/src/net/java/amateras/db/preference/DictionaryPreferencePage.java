package net.java.amateras.db.preference;

import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.NameConverter;
import net.java.amateras.db.util.NameConverter.DictionaryEntry;
import net.java.amateras.db.util.TableViewerSupport;
import net.java.amateras.db.util.UIUtils;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class DictionaryPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private TableViewer viewer;
	private List<DictionaryEntry> models;

	public DictionaryPreferencePage(){
		super("DictionaryPreferencePage");
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	public Point computeSize() {
		Point point = super.computeSize();
		return new Point(point.x, 0);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		models = NameConverter.loadFromPreferenceStore(DBPlugin.getDefault().getPreferenceStore());

		TableViewerSupport<DictionaryEntry> support = new TableViewerSupport<DictionaryEntry>(models, composite){
			protected DictionaryEntry doAdd() {
				EntryEditDialog dialog = new EntryEditDialog(getShell());
				if(dialog.open()==Dialog.OK){
					return dialog.getEntry();
				}
				return null;
			}

			protected void doEdit(DictionaryEntry entry) {
				EntryEditDialog dialog = new EntryEditDialog(getShell(), entry);
				if(dialog.open()==Dialog.OK){
					DictionaryEntry newEntry = dialog.getEntry();
					entry.logicalName  = newEntry.logicalName;
					entry.physicalName = newEntry.physicalName;
					entry.partialMatch = newEntry.partialMatch;
				}
			}
		};

		support.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer = support.getTableViewer();

		return composite;
	}

	@Override
	public boolean performCancel() {
		models.clear();
		models.addAll(NameConverter.loadDefaultDictionary());
		viewer.refresh();
		return true;
	}

	@Override
	public boolean performOk() {
		NameConverter.saveToPreferenceStore(DBPlugin.getDefault().getPreferenceStore(), models);
		return true;
	}

	@Override
	protected void performDefaults() {
		List<DictionaryEntry> defaultEntries = NameConverter.loadDefaultDictionary();
		models.clear();
		models.addAll(defaultEntries);
		this.viewer.refresh();
	}

	private class EntryEditDialog extends Dialog {

		private Text logicalName;
		private Text physicalName;
		private Button partMatch;
		private DictionaryEntry element;

		public EntryEditDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle()|SWT.RESIZE);
		}

		public EntryEditDialog(Shell parentShell, DictionaryEntry element) {
			super(parentShell);
			this.element = element;
		}

		protected Control createDialogArea(Composite parent) {
			getShell().setText(DBPlugin.getResourceString("dialog.dictionary.title"));

			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2,false));

			UIUtils.createLabel(composite, "label.physicalName");
			physicalName = new Text(composite, SWT.BORDER);
			physicalName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			if(element!=null){
				physicalName.setText(element.physicalName);
			}

			UIUtils.createLabel(composite, "label.logicalName");
			logicalName = new Text(composite, SWT.BORDER);
			if(element!=null){
				logicalName.setText(element.logicalName);
			}
			logicalName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			UIUtils.createLabel(composite, "label.partialMatch");
			partMatch = new Button(composite, SWT.CHECK);
			if(element != null){
				partMatch.setSelection(element.partialMatch);
			}

			return composite;
		}

		protected void okPressed() {
			if(physicalName.getText().length()==0){
				UIUtils.openAlertDialog(DBPlugin.getDefault().createMessage(
						DBPlugin.getResourceString("error.required"),
						new String[]{ DBPlugin.getResourceString("label.physicalName") }));
				return;
			}
			if(logicalName.getText().length()==0){
				UIUtils.openAlertDialog(DBPlugin.getDefault().createMessage(
						DBPlugin.getResourceString("error.required"),
						new String[]{ DBPlugin.getResourceString("label.logicalName") }));
				return;
			}
			element = new DictionaryEntry(
					physicalName.getText(), logicalName.getText(), partMatch.getSelection());
			super.okPressed();
		}

		public DictionaryEntry getEntry(){
			return element;
		}
	}

}
