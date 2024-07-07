package net.java.amateras.uml;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class UMLPlugin extends AbstractUIPlugin {

	//The shared instance.
	private static UMLPlugin plugin;

	private ResourceBundle resourceBundle;

	public static final String PLUGIN_ID = "net.java.amateras.umleditor";

	//============================================================================
	// Common settings
	//============================================================================
	public static final String PREF_SHOW_GRID = "pref.showgrid";
	public static final String PREF_GRID_SIZE = "pref.gridsize";
	public static final String PREF_SNAP_GEOMETRY = "pref.snapgeometry";
	public static final String PREF_ZOOMABLE_WITH_CTRL_AND_SCROLL = "pref_zoomable_with_ctrl_and_scroll";

	//============================================================================
	// Appearance settings
	//============================================================================
	public static final String PREF_ANTI_ALIAS = "pref.antialias";
	public static final String PREF_NEWSTYLE = "pref.style.new";


	//============================================================================
	// Class diagram settings
	//============================================================================
	/**
	 * Show simple name of classes to make Class seem shorter in class diagrams
	 */
	public static final String PREF_CLASS_DIAGRAM_SHOW_SIMPLE_NAME = "pref.classdiagram.simplename";

	/**
	 * Show parameter name or not, methods will be shorter if parameter name isn't shown but only parameter type
	 */
	public static final String PREF_CLASS_DIAGRAM_SHOW_PARAMETER_NAME = "pref.classdiagram.show_parameter_name";

	public static final String PREF_CLASS_DIAGRAM_CREATE_AGGREGATION_ON_IMPORT = "pref.classdiagram.create_aggreation_on_import";

	//============================================================================
	// Sequence diagram settings
	//============================================================================
	/**
	 * Create a return message automatically in sequence diagrams
	 */
	public static final String PREF_SEQUENCE_DIAGRAM_CREATE_RETURN = "pref.sequence.createreturn";

	/**
	 * Show simple name of classes to make Class seem shorter in sequence diagrams
	 */
	public static final String PREF_SEQUENCE_DIAGRAM_SHOW_SIMPLE_NAME = "pref.sequence.simplename";

//	private List dndListeners = new ArrayList();

	/**
	 * The constructor.
	 */
	public UMLPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("net.java.amateras.uml.UMLPlugin");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static UMLPlugin getDefault() {
		return plugin;
	}

	/**
	 * Getsi18n message from <tt>UMLPlugin.properties</tt>.
	 *
	 * @param key a message key
	 * @return i18n message
	 */
	public String getResourceString(String key) {
		return resourceBundle.getString(key);
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path.
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("net.java.amateras.umleditor", path);
	}

	/**
	 * Logging debug information.
	 *
	 * @param message message
	 */
	public static void logDebug(String message) {
		ILog log = getDefault().getLog();
		IStatus status = new Status(IStatus.INFO, PLUGIN_ID, 0, message, null);
		log.log(status);
	}

	/**
	 * Logging error information.
	 *
	 * @param message message
	 */
	public static void logError(String message) {
		ILog log = getDefault().getLog();
		IStatus status = new Status(IStatus.ERROR, PLUGIN_ID, 0, message, null);
		log.log(status);
	}

	/**
	 * Logging exception information.
	 *
	 * @param ex exception
	 */
	public static void logException(Throwable ex) {
		ILog log = getDefault().getLog();
		IStatus status = null;
		if (ex instanceof CoreException) {
			status = ((CoreException) ex).getStatus();
		} else {
			status = new Status(IStatus.ERROR, PLUGIN_ID, 0, ex.toString(), ex);
		}
		log.log(status);

		// TODO debug
		ex.printStackTrace();
	}

	//	public String getConnectionRouter(){
	//		IPreferenceStore store = getPreferenceStore();
	//		String type = store.getString(UMLPlugin.PREF_CONNECTION_TYPE);
	//		return type;
	//	}

//	public List getDndListeners() {
//		return dndListeners;
//	}
}
