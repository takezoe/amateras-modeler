package net.java.amateras.uml.java;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class UMLJavaPlugin extends Plugin {

	//The shared instance.
	private static UMLJavaPlugin plugin;
	
	private ResourceBundle resourceBundle;
	
	/**
	 * The constructor.
	 */
	public UMLJavaPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("net.java.amateras.uml.java.resource");
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
	public static UMLJavaPlugin getDefault() {
		return plugin;
	}
	
	public String getResourceString(String key){
		return resourceBundle.getString(key);
	}

}
