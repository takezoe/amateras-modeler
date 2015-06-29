/**
 * 
 */
package net.java.amateras.uml.usecasediagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.java.amateras.uml.model.RootModel;

/**
 * @author shida
 *
 */
public class UsecaseRootModel extends RootModel {

	public List getChildren() {
		List children = new ArrayList(super.getChildren());
		Collections.sort(children, new Comparator() {

			public int compare(Object arg0, Object arg1) {
			    if (arg0 instanceof SystemModel && !(arg1 instanceof SystemModel)) {
			    	return -1;
			    } else if (arg1 instanceof SystemModel && !(arg0 instanceof SystemModel)) {
			    	return 1;
			    }
				return 0;
			}
			
		});
		return children;
	}
}
