/**
 * 
 */
package net.java.amateras.uml.usecasediagram.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

/**
 * @author shida
 *
 */
public class UsecaseRootModel extends RootModel {

	public List<AbstractUMLModel> getChildren() {
		List<AbstractUMLModel> children = new ArrayList<AbstractUMLModel>(super.getChildren());
		Collections.sort(children, new Comparator<AbstractUMLModel>() {
			public int compare(AbstractUMLModel arg0, AbstractUMLModel arg1) {
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
