/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.usecasediagram.figure.ext.SystemFigureEx;
import net.java.amateras.uml.usecasediagram.figure.ext.UsecaseFigureEx;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

/**
 * @author shidat
 *
 */
public class UsecaseFigureFactory {

	public static UsecaseFigure getUsecaseFigure(UsecaseModel model) {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new UsecaseFigureEx(model);
		}
		return new UsecaseFigure(model);
	}
	
	public static SystemFigure getSystemFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new SystemFigureEx();
		}
		return new SystemFigure();		
	}
}
