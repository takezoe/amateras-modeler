/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.sequencediagram.figure.ext.ActivationFigureEx;
import net.java.amateras.uml.sequencediagram.figure.ext.InstanceFigureEx;

/**
 * @author shidat
 *
 */
public class SequenceFigureFactory {

	/**
	 * アクティベイションの図を取得する.
	 * @return
	 */
	public static ActivationFigure getActivationFigure() { 
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new ActivationFigureEx();
		}
		return new ActivationFigure();
	}
	
	/**
	 * インスタンスの図を取得する.
	 * @return
	 */
	public static InstanceFigure getInstanceFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new InstanceFigureEx();
		}
		return new InstanceFigure();
	}
}
