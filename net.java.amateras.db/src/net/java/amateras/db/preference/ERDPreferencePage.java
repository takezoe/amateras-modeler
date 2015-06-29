package net.java.amateras.db.preference;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.SpinnerFieldEditor;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ERDPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private BooleanFieldEditor showGrid;

	private SpinnerFieldEditor gridSize;

	private BooleanFieldEditor snapToGeometry;
	
	private BooleanFieldEditor showNotNull;

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// for Layout (Grid)
		Group layoutGroup = new Group(composite, SWT.NULL);
		layoutGroup.setText(DBPlugin.getResourceString("preference.layout"));
		layoutGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showGrid = new BooleanFieldEditor(DBPlugin.PREF_SHOW_GRID, DBPlugin.getResourceString("preference.layout.showGrid"),
				layoutGroup);
		gridSize = new SpinnerFieldEditor(DBPlugin.PREF_GRID_SIZE, DBPlugin.getResourceString("preference.layout.gridSize"), 1, 100,
				layoutGroup);
		snapToGeometry = new BooleanFieldEditor(DBPlugin.PREF_SNAP_GEOMETRY, DBPlugin.getResourceString(
				"preference.layout.snapToGeometry"), layoutGroup);
		layoutGroup.setLayout(new GridLayout(3, false));
		
		// for Diagram (Grid)
		Group diagramGroup = new Group(composite, SWT.NULL);
		diagramGroup.setText(DBPlugin.getResourceString("preference.diagram"));
		diagramGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showNotNull = new BooleanFieldEditor(DBPlugin.PREF_SHOW_NOT_NULL, DBPlugin.getResourceString(
			"preference.diagram.showNotNull"), diagramGroup);
		diagramGroup.setLayout(new GridLayout(1, false));

		// Initializes values
		fillInitialValues();

		return composite;
	}
	
	private void fillInitialValues() {
		IPreferenceStore store = DBPlugin.getDefault().getPreferenceStore();

		showGrid.setPreferenceStore(store);
		showGrid.load();

		gridSize.setPreferenceStore(store);
		gridSize.load();

		snapToGeometry.setPreferenceStore(store);
		snapToGeometry.load();
		
		showNotNull.setPreferenceStore(store);
		showNotNull.load();
	}
	
	public boolean performOk() {
		showGrid.store();
		gridSize.store();
		snapToGeometry.store();
		showNotNull.store();
		return true;
	}

}
