package net.java.amateras.uml;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

public class UMLPreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = UMLPlugin.getDefault().getPreferenceStore();
		store.setDefault(UMLPlugin.PREF_SEQUENCE_DIAGRAM_SHOW_SIMPLE_NAME, false);
		store.setDefault(UMLPlugin.PREF_SEQUENCE_DIAGRAM_CREATE_RETURN, true);
		store.setDefault(UMLPlugin.PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME, false);
		store.setDefault(UMLPlugin.PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME, true);
		store.setDefault(UMLPlugin.PREF_CLASS_DIAGRAM_CREATE_AGGREGATION_ON_IMPORT, true);
		store.setDefault(UMLPlugin.PREF_ANTI_ALIAS, false);
		store.setDefault(UMLPlugin.PREF_SHOW_GRID, false);
		store.setDefault(UMLPlugin.PREF_GRID_SIZE, 10);
		store.setDefault(UMLPlugin.PREF_SNAP_GEOMETRY, false);
	}

}
