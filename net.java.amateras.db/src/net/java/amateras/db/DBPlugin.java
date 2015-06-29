package net.java.amateras.db;

import java.util.HashMap;
import java.util.Map;

import net.java.amateras.db.dialect.IDialect;
import net.java.amateras.db.sqleditor.EditorColorProvider;
import net.java.amateras.db.util.ColorRegistry;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class DBPlugin extends AbstractUIPlugin {

    // preference keys
    public static final String PREF_VALIDATE_ON_SAVE = "pref_validate_on_save";

    public static final String PREF_VALIDATE_PHYSICAL_TABLE_NAME_REQUIRED = "pref_validate_physical_table_name_required";
    public static final String PREF_VALIDATE_PHYSICAL_TABLE_NAME_DUPLICATED = "pref_validate_physical_table_name_duplicated";
    public static final String PREF_VALIDATE_LOGICAL_TABLE_NAME_REQUIRED = "pref_validate_on_logical_table_name_required";
    public static final String PREF_VALIDATE_LOGICAL_TABLE_NAME_DUPLICATED = "pref_validate_on_logical_table_name_duplicated";
    public static final String PREF_VALIDATE_PHYSICAL_COLUMN_NAME_REQUIRED = "pref_validate_physical_column_name_required";
    public static final String PREF_VALIDATE_PHYSICAL_COLUMN_NAME_DUPLICATED = "pref_validate_physical_column_name_duplicatedl";
    public static final String PREF_VALIDATE_LOGICAL_COLUMN_NAME_REQUIRED = "pref_validate_physical_column_name_required";
    public static final String PREF_VALIDATE_LOGICAL_COLUMN_NAME_DUPLICATED = "pref_validate_physical_column_name_duplicated";
    public static final String PREF_VALIDATE_PRIMARY_KEY = "pref_validate_primary_key";
    public static final String PREF_VALIDATE_NO_COLUMNS = "pref_validate_on_columns";
    public static final String PREF_VALIDATE_FOREIGN_KEY_COLUMN_TYPE = "pref_validate_foreign_key_column_type";
    public static final String PREF_VALIDATE_FOREIGN_KEY_COLUMN_SIZE = "pref_validate_foreign_key_column_size";
	public static final String PREF_SHOW_GRID = "pref_show_grid";
	public static final String PREF_GRID_SIZE = "pref_grid_size";
	public static final String PREF_SNAP_GEOMETRY = "pref_snap_geometry";
	public static final String PREF_SHOW_NOT_NULL = "pref_show_notnull";
	public static final String PREF_FONT = "pref_font";
	public static final String PREF_DICTIONALY = "pref_dictionary";

	// for SQL editor
	public static final String PREF_COLOR_DEFAULT = "colorDefault";
	public static final String PREF_COLOR_COMMENT = "colorComment";
	public static final String PREF_COLOR_STRING = "colorString";
	public static final String PREF_COLOR_KEYWORD = "colorKeyword";

    // validation levels
    public static final String LEVEL_ERROR = "ERROR";
    public static final String LEVEL_WARNING = "WARNING";
    public static final String LEVEL_IGNORE = "IGNORE";

	//The shared instance.
	private static DBPlugin plugin;

	public static final String PLUGIN_ID = "net.java.amateras.db";

	private Map<String, IDialect> contributedDialects = null;
	private ColorRegistry colorRegistry = new ColorRegistry();
	private EditorColorProvider colorProvider;

	public static final String ICON_TABLE = "icons/table.gif";
	public static final String ICON_COLUMN = "icons/column.gif";
	public static final String ICON_PK_COLUMN = "icons/pk_column.gif";
	public static final String ICON_INDEX = "icons/index.gif";
	public static final String ICON_FOLDER = "icons/folder.gif";
	public static final String ICON_DOMMAIN = "icons/domain.gif";
	public static final String ICON_ERROR = "icons/error.gif";
	public static final String ICON_WARNING = "icons/warning.gif";
    public static final String ICON_OVERLAY_ERROR = "icons/ovr_error.gif";
    public static final String ICON_OVERLAY_WARNING = "icons/ovr_warning.gif";
    public static final String ICON_REFRESH = "icons/refresh.gif";

	/**
	 * The constructor.
	 */
	public DBPlugin() {
		plugin = this;
	}

	public static String getResourceString(String key){
		return Messages.getResourceString(key);
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		this.colorProvider = new EditorColorProvider(getPreferenceStore());
	}

	/**
	 * Returns a <code>Image</code> by a given path.
	 * <p>
	 * Created <code>Image</code> is cached by the <code>ImageRegistry</code>.
	 * If <code>ImageRegistry</code> already has cached <code>Image</code>,
	 * this method returns cached <code>Image</code>.
	 * <p>
	 * Cached images are disposed at {@link DBPlugin#stop(BundleContext)}.
	 *
	 * @param path
	 * @return
	 */
	public static Image getImage(String path){
		ImageRegistry images = getDefault().getImageRegistry();
		Image image = images.get(path);
		if(image == null){
			image = getImageDescriptor(path).createImage();
			images.put(path, image);
		}
		return image;
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);

		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				getImageRegistry().dispose();
				colorRegistry.dispose();
			}
		});

		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static DBPlugin getDefault() {
		return plugin;
	}

	public Color getColor(RGB rgb){
		return colorRegistry.getColor(rgb);
	}

	public EditorColorProvider getEditorColorProvider(){
		return this.colorProvider;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.java.amateras.db", path);
	}

	public static void logException(Exception ex){
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "Error", ex);
		getDefault().getLog().log(status);
		ex.printStackTrace(); // TODO Debug
	}

	public String createMessage(String key, String[] values){
		String message = getResourceString(key);
		for(int i=0;i<values.length;i++){
			message = message.replaceAll("\\{"+i+"\\}", values[i]);
		}
		return message;
	}

	/**
	 * Returns contributed <code>IDialect</code>s.
	 *
	 * @return contributed dialects.
	 */
	public Map<String, IDialect> getContributedDialects(){
		if(this.contributedDialects==null){
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(PLUGIN_ID + ".dialects");
			IExtension[] extensions = point.getExtensions();
			this.contributedDialects = new HashMap<String, IDialect>();

			for(int i=0;i<extensions.length;i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					try {
						if("dialect".equals(elements[j].getName())) {
							String name = elements[j].getAttribute("name");
							IDialect dialect = (IDialect) elements[j].createExecutableExtension("class");
							this.contributedDialects.put(name, dialect);
						}
					} catch(Exception ex){
						logException(ex);
					}
				}
			}
		}
		return this.contributedDialects;
	}
}
