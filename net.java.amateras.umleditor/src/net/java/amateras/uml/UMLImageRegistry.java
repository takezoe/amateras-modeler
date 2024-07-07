/**
 * 
 */
package net.java.amateras.uml;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 * @author Takahiro Shida.
 *
 */
public class UMLImageRegistry {

	private ImageRegistry registry;
	
	private static UMLImageRegistry imageRegistry;
	
	private UMLImageRegistry() {
		registry = new ImageRegistry();
	}
	
	public static Image getImage(String url) {
		if (imageRegistry == null) {
			imageRegistry = new UMLImageRegistry();
		}
		if (url == null) {
			return null;
		}
		return imageRegistry.createImage(url);
	}

	private Image createImage(String url) {
		try {
			ImageDescriptor descriptor = ImageDescriptor.createFromURL(new URI(url).toURL());
			if (registry.get(url) == null) {
				registry.put(url, descriptor);
			}
			return registry.get(url);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
