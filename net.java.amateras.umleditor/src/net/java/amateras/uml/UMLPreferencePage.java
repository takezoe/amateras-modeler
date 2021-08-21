package net.java.amateras.uml;

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

public class UMLPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {


	private BooleanFieldEditor antiAlias;

	private BooleanFieldEditor showGrid;

	private SpinnerFieldEditor gridSize;

	private BooleanFieldEditor snapToGeometry;

	private BooleanFieldEditor newThema;

	private BooleanFieldEditor showSimpleNameInClassDiagram;

	/**
	 * Show parameter name or not, methods will be shorter if parameter name isn't shown but only parameter type
	 */
	private BooleanFieldEditor showParameterName;

	private BooleanFieldEditor createAggregationOnImport;

	private BooleanFieldEditor createReturn;

	private BooleanFieldEditor showSimpleNameInSequenceDiagram;

	private BooleanFieldEditor zoomableWithCtrlAndScroll;


	public UMLPreferencePage() {
		super("AmaterasUML");
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// for Diagram Layout (Grid)
		Group layoutGroup = new Group(composite, SWT.NULL);
		layoutGroup.setText(UMLPlugin.getDefault().getResourceString("preference.layout"));
		layoutGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showGrid = new BooleanFieldEditor(UMLPlugin.PREF_SHOW_GRID, UMLPlugin.getDefault().getResourceString("preference.layout.showGrid"),
				layoutGroup);
		gridSize = new SpinnerFieldEditor(UMLPlugin.PREF_GRID_SIZE, UMLPlugin.getDefault().getResourceString("preference.layout.gridSize"), 1, 100,
				layoutGroup);
		snapToGeometry = new BooleanFieldEditor(UMLPlugin.PREF_SNAP_GEOMETRY, UMLPlugin.getDefault().getResourceString(
				"preference.layout.snapToGeometry"), layoutGroup);

		zoomableWithCtrlAndScroll = new BooleanFieldEditor(UMLPlugin.PREF_ZOOMABLE_WITH_CTRL_AND_SCROLL, UMLPlugin.getDefault().getResourceString(
				"preference.layout.zoomableWithCtrlAndScroll"), layoutGroup);
		zoomableWithCtrlAndScroll.fillIntoGrid(layoutGroup, 2);

		layoutGroup.setLayout(new GridLayout(3, false));

		// for Class Diagram
		Group classGroup = new Group(composite, SWT.NULL);
		classGroup.setText(UMLPlugin.getDefault().getResourceString("preference.classdiagram"));
		classGroup.setLayout(new GridLayout(1, false));
		classGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		showSimpleNameInClassDiagram = new BooleanFieldEditor(UMLPlugin.PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME, UMLPlugin.getDefault().getResourceString(
				"preference.classdiagram.simpleName"), classGroup);
		showParameterName = new BooleanFieldEditor(UMLPlugin.PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME, UMLPlugin.getDefault().getResourceString(
				"preference.classdiagram.showParameterName"), classGroup);
		createAggregationOnImport = new BooleanFieldEditor(UMLPlugin.PREF_CLASS_DIAGRAM_CREATE_AGGREGATION_ON_IMPORT, UMLPlugin.getDefault().getResourceString(
				"preference.classdiagram.createAggregationOnImport"), classGroup);

		// for Sequence Diagram
		Group sequenceGroup = new Group(composite, SWT.NULL);
		sequenceGroup.setText(UMLPlugin.getDefault().getResourceString("preference.sequence"));
		sequenceGroup.setLayout(new GridLayout(1, false));
		sequenceGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		showSimpleNameInSequenceDiagram = new BooleanFieldEditor(UMLPlugin.PREF_SEQUENCE_DIAGRAM_SHOW_SIMPLE_NAME, UMLPlugin.getDefault().getResourceString(
				"preference.classdiagram.simpleName"), sequenceGroup);
		createReturn = new BooleanFieldEditor(UMLPlugin.PREF_SEQUENCE_DIAGRAM_CREATE_RETURN, UMLPlugin.getDefault().getResourceString(
				"preference.sequence.returnmessage"), sequenceGroup);

		// Graphics style.
		Group appearanceGoup = new Group(composite, SWT.NULL);
		appearanceGoup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		appearanceGoup.setText(UMLPlugin.getDefault().getResourceString("preference.appearance"));

		antiAlias = new BooleanFieldEditor(UMLPlugin.PREF_ANTI_ALIAS, UMLPlugin.getDefault().getResourceString("preference.antialias"),
				appearanceGoup);

		newThema = new BooleanFieldEditor(UMLPlugin.PREF_NEWSTYLE, UMLPlugin.getDefault().getResourceString("preference.appearance.new"),
				appearanceGoup);

		// Initializes values
		fillInitialValues();

		return composite;
	}

	private void fillInitialValues() {
		IPreferenceStore store = UMLPlugin.getDefault().getPreferenceStore();

		antiAlias.setPreferenceStore(store);
		antiAlias.load();

		showGrid.setPreferenceStore(store);
		showGrid.load();

		gridSize.setPreferenceStore(store);
		gridSize.load();

		snapToGeometry.setPreferenceStore(store);
		snapToGeometry.load();

		newThema.setPreferenceStore(store);
		newThema.load();

		showSimpleNameInClassDiagram.setPreferenceStore(store);
		showSimpleNameInClassDiagram.load();

		showParameterName.setPreferenceStore(store);
		showParameterName.load();

		createAggregationOnImport.setPreferenceStore(store);
		createAggregationOnImport.load();

		createReturn.setPreferenceStore(store);
		createReturn.load();

		showSimpleNameInSequenceDiagram.setPreferenceStore(store);
		showSimpleNameInSequenceDiagram.load();

		zoomableWithCtrlAndScroll.setPreferenceStore(store);
		zoomableWithCtrlAndScroll.load();
	}

	public boolean performOk() {
		createReturn.store();
		antiAlias.store();
		showGrid.store();
		gridSize.store();
		snapToGeometry.store();
		newThema.store();
		showSimpleNameInClassDiagram.store();
		showParameterName.store();
		createAggregationOnImport.store();
		showSimpleNameInSequenceDiagram.store();
		zoomableWithCtrlAndScroll.store();
		return true;
	}

	public void init(IWorkbench workbench) {
	}

}