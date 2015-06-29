package net.java.amateras.db.visual.generate;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.db.DBPlugin;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class GeneratorProvider {
	
//	private static IGenerater[] GENERATERS = {
//		new DDLGenerater(),
//		new HTMLGenerator()
//	};
	
	private static List<IGenerator> contributedGenerators = null;
	
	public static IGenerator[] getGeneraters(){
		if(contributedGenerators == null){
			// load contributed generators
			contributedGenerators = new ArrayList<IGenerator>();
			
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IExtensionPoint point = registry.getExtensionPoint(DBPlugin.PLUGIN_ID + ".generators");
			IExtension[] extensions = point.getExtensions();
			
			for(int i=0;i<extensions.length;i++){
				IConfigurationElement[] elements = extensions[i].getConfigurationElements();
				for (int j = 0; j < elements.length; j++) {
					try {
						if("generator".equals(elements[j].getName())) {
							IGenerator generator = (IGenerator) elements[j].createExecutableExtension("class");
							contributedGenerators.add(generator);
						}
					} catch(Exception ex){
						DBPlugin.logException(ex);
					}
				}
			}
		}
		
		return contributedGenerators.toArray(new IGenerator[contributedGenerators.size()]);
	}
	
}
