/**
 * 
 */
package net.java.amateras.uml.dnd.java;

import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.java.UMLJavaUtils;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

/**
 * 
 * @author Takahiro Shida
 * @author Naoki Takezoe
 */
class ClassModelConverter implements CreationFactory {

	protected IType type;

	public ClassModelConverter(IType type) {
		this.type = type;
	}

	public Object getNewObject() {
		try {
			AbstractUMLEntityModel rv = null;
			if (type.isInterface()) {
				rv = new InterfaceModel();
				((InterfaceModel) rv).setName(type.getFullyQualifiedName());
			} else if (type.isClass()) {
				rv = new ClassModel();
				((ClassModel) rv).setName(type.getFullyQualifiedName());
			} else if (type.isEnum()) {
				rv = new EnumModel();
				((EnumModel) rv).setName(type.getFullyQualifiedName());
			}
			
			AttributeModel[] fields = UMLJavaUtils.getFields(type);
			for(int i=0;i<fields.length;i++){
				rv.addChild(fields[i]);
			}
			OperationModel[] methods = UMLJavaUtils.getMethods(type);
			for(int i=0;i<methods.length;i++){
				rv.addChild(methods[i]);
			}
			
			return rv;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Object getObjectType() {
		try {
			if (type.isInterface()) {
				return InterfaceModel.class;
			} else if (type.isEnum()) {
				return EnumModel.class;
			} else {
				return ClassModel.class;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return ClassModel.class;
	}

}