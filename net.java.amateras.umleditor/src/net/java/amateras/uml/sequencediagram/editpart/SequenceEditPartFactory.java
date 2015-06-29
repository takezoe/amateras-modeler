package net.java.amateras.uml.sequencediagram.editpart;

import net.java.amateras.uml.editpart.BaseUMLEditPartFactory;
import net.java.amateras.uml.sequencediagram.model.ActivationModel;
import net.java.amateras.uml.sequencediagram.model.FragmentModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.InteractionModel;
import net.java.amateras.uml.sequencediagram.model.LifeLineModel;
import net.java.amateras.uml.sequencediagram.model.ReturnMessageModel;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

import org.eclipse.gef.EditPart;

/**
 * EditPartのファクトリクラス。
 * @author Takahiro Shida.
 */
public class SequenceEditPartFactory extends BaseUMLEditPartFactory {

	public EditPart createUMLEditPart(EditPart context, Object model) {
		EditPart part = null;
		if(model instanceof InteractionModel){
			part = new InteractionEditPart();
		} else if(model instanceof InstanceModel){
			part = new InstanceEditPart();
		} else if(model instanceof LifeLineModel){
			part = new LifeLineEditPart();
		} else if(model instanceof ActivationModel){
			part = new ActivationEditPart();
		} else if(model instanceof SyncMessageModel) {
			part = new SyncMessageEditPart();
		} else if (model instanceof ReturnMessageModel) {
			part = new ReturnMessageEditPart();
		} else if (model instanceof FragmentModel) {
			part = new FragmentEditPart();
		}
		return part;
	}
	
}
