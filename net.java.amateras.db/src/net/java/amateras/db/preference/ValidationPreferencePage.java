package net.java.amateras.db.preference;

import net.java.amateras.db.DBPlugin;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class ValidationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
    
    private static final String[][] VALIDATION_LEVELS = new String[][]{
        { DBPlugin.getResourceString("preference.validation.level.warning"), DBPlugin.LEVEL_WARNING },
        { DBPlugin.getResourceString("preference.validation.level.error"), DBPlugin.LEVEL_ERROR },
        { DBPlugin.getResourceString("preference.validation.level.ignore"), DBPlugin.LEVEL_IGNORE },
    };
    
    public ValidationPreferencePage() {
        super(GRID); //$NON-NLS-1$
        setPreferenceStore(DBPlugin.getDefault().getPreferenceStore());
    }
    
    public void init(IWorkbench workbench) {
    }
    
    protected void createFieldEditors() {
        setTitle("AmaterasERD");
        
        Composite parent = getFieldEditorParent();
        
        addField(new BooleanFieldEditor(
                DBPlugin.PREF_VALIDATE_ON_SAVE,
                DBPlugin.getResourceString("preference.validation"), parent));
        
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_PHYSICAL_TABLE_NAME_REQUIRED,
                DBPlugin.getResourceString("preference.validation.tableName.required"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_PHYSICAL_TABLE_NAME_DUPLICATED,
                DBPlugin.getResourceString("preference.validation.tableName.duplicated"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_LOGICAL_TABLE_NAME_REQUIRED,
                DBPlugin.getResourceString("preference.validation.logicalTableName.required"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_LOGICAL_TABLE_NAME_DUPLICATED,
                DBPlugin.getResourceString("preference.validation.logicalTableName.duplicated"), 
                VALIDATION_LEVELS, parent));
        
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_PHYSICAL_COLUMN_NAME_REQUIRED,
                DBPlugin.getResourceString("preference.validation.columnName.required"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_PHYSICAL_COLUMN_NAME_DUPLICATED,
                DBPlugin.getResourceString("preference.validation.columnName.duplicated"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_LOGICAL_COLUMN_NAME_REQUIRED,
                DBPlugin.getResourceString("preference.validation.logicalColumnName.required"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_LOGICAL_COLUMN_NAME_DUPLICATED,
                DBPlugin.getResourceString("preference.validation.logicalColumnName.duplicated"), 
                VALIDATION_LEVELS, parent));
        
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_NO_COLUMNS,
                DBPlugin.getResourceString("preference.validation.noColumns"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_PRIMARY_KEY,
                DBPlugin.getResourceString("preference.validation.primaryKey"), 
                VALIDATION_LEVELS, parent));
        
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_FOREIGN_KEY_COLUMN_TYPE,
                DBPlugin.getResourceString("preference.validation.foreignKey.columnType"), 
                VALIDATION_LEVELS, parent));
        addField(new ComboFieldEditor(DBPlugin.PREF_VALIDATE_FOREIGN_KEY_COLUMN_SIZE,
                DBPlugin.getResourceString("preference.validation.foreignKey.columnSize"), 
                VALIDATION_LEVELS, parent));
    }
    
}
