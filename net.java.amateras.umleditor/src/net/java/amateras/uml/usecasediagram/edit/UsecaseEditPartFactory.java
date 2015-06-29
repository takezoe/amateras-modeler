/**
 * 
 */
package net.java.amateras.uml.usecasediagram.edit;

import net.java.amateras.uml.editpart.BaseUMLEditPartFactory;
import net.java.amateras.uml.usecasediagram.model.SystemModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseActorModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseExtendModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseGeneralizationModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseIncludeModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseRelationModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseRootModel;

import org.eclipse.gef.EditPart;

/**
 * @author shida
 *
 */
public class UsecaseEditPartFactory extends BaseUMLEditPartFactory {

	protected EditPart createUMLEditPart(EditPart context, Object model) {
		if(model instanceof UsecaseRootModel){
			return new UsecaseRootEditPart();
		} else if(model instanceof UsecaseModel){
			return new UsecaseEditPart();
		} else if(model instanceof UsecaseActorModel) {
			return new UsecaseActorEditPart();
		} else if (model instanceof SystemModel) {
			return new SystemEditPart();
		} else if (model instanceof UsecaseRelationModel) {
			return new UsecaseRelationEditPart();
		} else if (model instanceof UsecaseGeneralizationModel) {
			return new UsecaseGeneralizationEditPart();
		} else if (model instanceof UsecaseIncludeModel) {
			return new UsecaseIncludeEditPart();
		} else if (model instanceof UsecaseExtendModel) {
			return new UsecaseExtendEditPart();
		}
		return null;
	}

}
