/**
 * 
 */
package net.java.amateras.uml;

import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

/**
 * @author Takahiro Shida.
 *
 */
public class UMLColorRegistry {

	private ColorRegistry registry;
	
	private static UMLColorRegistry instance;
	
	private UMLColorRegistry() {
		registry = new ColorRegistry();
	}
	public static final Color getColor(RGB rgb) {
		if (instance == null) {
			instance = new UMLColorRegistry();
		}
		if (rgb == null) {
			return null;
		}
		return instance.createColor(rgb);
	}
	
	private Color createColor(RGB rgb) {
		registry.put(rgb.toString(), rgb);
		return registry.get(rgb.toString());
	}

}
