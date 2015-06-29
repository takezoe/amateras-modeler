package net.java.amateras.db.util;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Spinner;

/**
 * The FieldEditor implementation for int values.
 * This FieldEditor edits int value using {@link org.eclipse.swt.widgets.Spinner}.
 * 
 * @author Naoki Takezoe
 */
public class SpinnerFieldEditor extends FieldEditor {
	
	private int min;
	private int max;
	
	private Spinner spinner;
	
	public SpinnerFieldEditor() {
		super();
	}

	public SpinnerFieldEditor(String name, String labelText, int min, int max, Composite parent) {
		this.min = min;
		this.max = max;
        init(name, labelText);
        createControl(parent);
	}

	protected void adjustForNumColumns(int numColumns) {
	}

	protected void doFillIntoGrid(Composite parent, int numColumns) {
        getLabelControl(parent);
        getSpinnerControl(parent);
	}

	protected void doLoad() {
		spinner.setSelection(
				getPreferenceStore().getInt(getPreferenceName()));
	}

	protected void doLoadDefault() {
		spinner.setSelection(
				getPreferenceStore().getDefaultInt(getPreferenceName()));
	}

	protected void doStore() {
		getPreferenceStore().setValue(getPreferenceName(), spinner.getSelection());
	}

	public int getNumberOfControls() {
		return 2;
	}
	
	protected Spinner getSpinnerControl(Composite parent){
		if(spinner==null){
			spinner = new Spinner(parent, SWT.BORDER);
			spinner.setMinimum(min);
			spinner.setMaximum(max);
		}
		return spinner;
	}

}