/**
 * 
 */
package net.java.amateras.uml.activitydiagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.activitydiagram.figure.ext.ActionFigureEx;
import net.java.amateras.uml.activitydiagram.figure.ext.ObjectFigureEx;
import net.java.amateras.uml.activitydiagram.figure.ext.VerticalPartitionFigureEx;

/**
 * @author shidat
 *
 */
public class ActivityFigureFactory {

	public static ActionFigure getActionFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new ActionFigureEx();
		}
		return new ActionFigure();
	}
	
	public static ObjectFigure getObjectFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new ObjectFigureEx();
		}
		return new ObjectFigure();
	}

	public static VerticalPartitionFigure getPartitionFigure() {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_NEWSTYLE)) {
			return new VerticalPartitionFigureEx();
		}
		return new VerticalPartitionFigure();
	}

}
