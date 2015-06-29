/**
 * 
 */
package net.java.amateras.uml.dnd.java;

import net.java.amateras.uml.model.TypeEntityModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;

import org.eclipse.jdt.core.IType;

/**
 * 
 * @author shida
 * @author Naoki Takezoe
 */
public class InstanceModelConverter extends ClassModelConverter {

	private String name;
	
	public InstanceModelConverter(IType type) {
		super(type);
		this.name = this.type.getFullyQualifiedName();
	}

	public Object getNewObject() {
		TypeEntityModel model = (TypeEntityModel) super.getNewObject();
		model.setName(this.name);
		InstanceModel instance = new InstanceModel();
		instance.setType(model);
		return instance;
	}
	
	public Object getObjectType() {
		return InstanceModel.class;
	}
}
