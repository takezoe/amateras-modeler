/**
 * 
 */
package net.java.amateras.uml.usecasediagram.edit;

import net.java.amateras.uml.editpart.RootEditPart;
import net.java.amateras.uml.usecasediagram.model.SystemModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

/**
 * @author shida
 *
 */
public class UsecaseRootEditPart extends RootEditPart {

	public UsecaseRootEditPart() {
		addResizableClass(UsecaseModel.class);
		addResizableClass(SystemModel.class);
	}
}
