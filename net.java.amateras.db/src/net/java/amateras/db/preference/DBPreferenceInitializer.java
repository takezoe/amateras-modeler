package net.java.amateras.db.preference;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.util.NameConverter;
import net.java.amateras.db.util.NameConverter.DictionaryEntry;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

/**
 *
 * @author Naoki Takezoe
 * @since 1.0.3
 */
public class DBPreferenceInitializer extends AbstractPreferenceInitializer {

	@Override
	public void initializeDefaultPreferences() {
		IPreferenceStore store = DBPlugin.getDefault().getPreferenceStore();
		store.setDefault(DBPlugin.PREF_VALIDATE_ON_SAVE, false);
		store.setDefault(DBPlugin.PREF_VALIDATE_PHYSICAL_TABLE_NAME_REQUIRED, DBPlugin.LEVEL_ERROR);
        store.setDefault(DBPlugin.PREF_VALIDATE_PHYSICAL_TABLE_NAME_DUPLICATED, DBPlugin.LEVEL_ERROR);
        store.setDefault(DBPlugin.PREF_VALIDATE_LOGICAL_TABLE_NAME_REQUIRED, DBPlugin.LEVEL_WARNING);
        store.setDefault(DBPlugin.PREF_VALIDATE_LOGICAL_TABLE_NAME_DUPLICATED, DBPlugin.LEVEL_WARNING);
        store.setDefault(DBPlugin.PREF_VALIDATE_PHYSICAL_COLUMN_NAME_REQUIRED, DBPlugin.LEVEL_ERROR);
        store.setDefault(DBPlugin.PREF_VALIDATE_PHYSICAL_COLUMN_NAME_DUPLICATED, DBPlugin.LEVEL_ERROR);
        store.setDefault(DBPlugin.PREF_VALIDATE_LOGICAL_COLUMN_NAME_REQUIRED, DBPlugin.LEVEL_WARNING);
        store.setDefault(DBPlugin.PREF_VALIDATE_LOGICAL_COLUMN_NAME_DUPLICATED, DBPlugin.LEVEL_WARNING);
        store.setDefault(DBPlugin.PREF_VALIDATE_NO_COLUMNS, DBPlugin.LEVEL_ERROR);
        store.setDefault(DBPlugin.PREF_VALIDATE_PRIMARY_KEY, DBPlugin.LEVEL_WARNING);
        store.setDefault(DBPlugin.PREF_VALIDATE_FOREIGN_KEY_COLUMN_TYPE, DBPlugin.LEVEL_ERROR);
        store.setDefault(DBPlugin.PREF_VALIDATE_FOREIGN_KEY_COLUMN_SIZE, DBPlugin.LEVEL_ERROR);
		store.setDefault(DBPlugin.PREF_SHOW_GRID, false);
		store.setDefault(DBPlugin.PREF_GRID_SIZE, 10);
		store.setDefault(DBPlugin.PREF_SNAP_GEOMETRY, false);

		StringBuilder sb = new StringBuilder();
		for(DictionaryEntry entry: NameConverter.loadDefaultDictionary()){
			sb.append(entry.toString()).append("\n");
		}
		store.setDefault(DBPlugin.PREF_DICTIONALY, sb.toString());

		// for SQL editor
		store.setDefault(DBPlugin.PREF_COLOR_DEFAULT, StringConverter.asString(new RGB(0,0,0)));
		store.setDefault(DBPlugin.PREF_COLOR_COMMENT, StringConverter.asString(new RGB(0,128,0)));
		store.setDefault(DBPlugin.PREF_COLOR_STRING, StringConverter.asString(new RGB(0,0,255)));
		store.setDefault(DBPlugin.PREF_COLOR_KEYWORD, StringConverter.asString(new RGB(128,0,128)));
	}

}
