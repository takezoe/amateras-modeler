package net.java.amateras.uml.java;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.AggregationModel;
import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.GeneralizationModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.RealizationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * This class provides utility methods for the AmaterasUML-Java Plug-In.
 * 
 * @author Naoki Takezoe
 */
public class UMLJavaUtils {
	
	/**
	 * Get classname from {@link ClassModel} or {@link InterfaceModel}.
	 * 
	 * @param model ClassModel or InterfaceModel
	 * @return classname
	 */
	public static String getClassName(Object model){
		if(model instanceof ClassModel){
			return ((ClassModel)model).getName();
		} else if(model instanceof InterfaceModel){
			return ((InterfaceModel)model).getName();
		}
		
		return null;
	}
	
	/**
	 * This method judges whether the type is a primitive type. 
	 * 
	 * @param type type (classname or primitive type)
	 * @return 
	 * <ul>
	 *   <li>true - primitive type</li>
	 *   <li>false - not primitive type</li>
	 * </ul>
	 */
	public static boolean isPrimitive(String type){
		if(type.equals("int") || type.equals("long") || type.equals("double") || type.equals("float") || 
				type.equals("char") || type.equals("boolean") || type.equals("byte")){
			return true;
		}
		return false;
	}

	/**
	 * Creates a qualified class name from a class name which doesn't contain package name.
	 * 
	 * @param parent a full qualified class name of the class which uses this variable
	 * @param type a class name which doesn't contain package name
	 * @return full a created qualified class name
	 */
	public static String getFullQName(IType parent,String type){
		
		type = stripGenerics(type);
		
		if(type.indexOf('.') >= 0){
			return type;
		}
		if(isPrimitive(type)){
			return type;
		}
		
		IJavaProject project = parent.getJavaProject();
		try {
			IType javaType = project.findType("java.lang." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IType javaType = project.findType(parent.getPackageFragment().getElementName() + "." + type);
			if(javaType!=null && javaType.exists()){
				return javaType.getFullyQualifiedName();
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		try {
			IImportDeclaration[] imports = parent.getCompilationUnit().getImports();
			for(int i=0;i<imports.length;i++){
				String importName = imports[i].getElementName();
				if(importName.endsWith("." + type)){
					return importName;
				}
				if(importName.endsWith(".*")){
					try {
						IType javaType = project.findType(importName.replaceFirst("\\*$",type));
						if(javaType!=null && javaType.exists()){
							return javaType.getFullyQualifiedName();
						}
					} catch(Exception ex){
					}
				}
			}
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return type;
	}
	
	public static AttributeModel[] getFields(IType type){
		try {
			IField[] fields = type.getFields();
			AttributeModel[] result = new AttributeModel[fields.length];
			for(int i=0;i<fields.length;i++){
				AttributeModel attr = new AttributeModel();
				attr.setName(fields[i].getElementName());
				attr.setType(Signature.toString(fields[i].getTypeSignature()));
				attr.setStatic(Flags.isStatic(fields[i].getFlags()));
				
				if(type.isInterface()){
					attr.setVisibility(Visibility.PUBLIC);
				} else if(Flags.isPublic(fields[i].getFlags())){
					attr.setVisibility(Visibility.PUBLIC);
				} else if(Flags.isPrivate(fields[i].getFlags())){
					attr.setVisibility(Visibility.PRIVATE);
				} else if(Flags.isProtected(fields[i].getFlags())){
					attr.setVisibility(Visibility.PROTECTED);
				} else {
					attr.setVisibility(Visibility.PACKAGE);
				}
				result[i] = attr;
			}
			return result;
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return new AttributeModel[0];
	}

	public static OperationModel[] getMethods(IType type){
		try{
			IMethod[] methods = type.getMethods();
			OperationModel[] result = new OperationModel[methods.length];
			for(int i=0;i<methods.length;i++){
				OperationModel ope = new OperationModel();
				ope.setName(methods[i].getElementName());
				ope.setType(Signature.toString(methods[i].getReturnType()));
				if(Flags.isPublic(methods[i].getFlags())){
					ope.setVisibility(Visibility.PUBLIC);
				} else if(Flags.isPrivate(methods[i].getFlags())){
					ope.setVisibility(Visibility.PRIVATE);
				} else if(Flags.isProtected(methods[i].getFlags())){
					ope.setVisibility(Visibility.PROTECTED);
				} else {
					ope.setVisibility(Visibility.PACKAGE);
				}
				ope.setAbstract(Flags.isAbstract(methods[i].getFlags()));
				ope.setStatic(Flags.isStatic(methods[i].getFlags()));
				List<Argument> params = new ArrayList<Argument>();
				String[] names = methods[i].getParameterNames();
				String[] types = methods[i].getParameterTypes();
				for(int j=0;j<names.length;j++){
					Argument arg = new Argument();
					arg.setName(names[j]);
					arg.setType(Signature.toString(types[j]));
					params.add(arg);
				}
				ope.setParams(params);
				result[i] = ope;
			}
			return result;
		} catch(Exception ex){
			ex.printStackTrace();
		}
		return new OperationModel[0];
	}
	
	public static void appendSuperClassConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model) throws JavaModelException {
		
		if(type.getSuperclassName()==null){
			return;
		}
		
		String superClass = UMLJavaUtils.getFullQName(type, type.getSuperclassName());
		
		List<AbstractUMLModel> children = root.getChildren();
		for(int i=0;i<children.size();i++){
			Object obj = children.get(i);
			String className = stripGenerics(UMLJavaUtils.getClassName(obj));
			if(className!=null && className.equals(superClass)){
				GeneralizationModel conn = new GeneralizationModel();
				conn.setSource(model);
				conn.setTarget((AbstractUMLEntityModel)obj);
				conn.attachSource();
				conn.attachTarget();
				break;
			}
		}
	}
	
	public static void appendSubConnection(RootModel root, IJavaProject project, AbstractUMLEntityModel model){
		List<AbstractUMLModel>  children = root.getChildren();
		for(int i=0; i< children.size(); i++){
			AbstractUMLEntityModel child = (AbstractUMLEntityModel) children.get(i);
			if(child != model){
				if(child instanceof InterfaceModel){
					String name = ((InterfaceModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							appendInterfacesConnection(root, type, child);
						}
					} catch(JavaModelException ex){
					}
				}
				if(child instanceof ClassModel){
					String name = ((ClassModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							appendSuperClassConnection(root, type, child);
							appendInterfacesConnection(root, type, child);
							appendAggregationConnection(root, type, (ClassModel) child);
						}
					} catch(JavaModelException ex){
					}
				}
			}
		}
	}
	
	public static void appendInterfacesConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model) throws JavaModelException {
		
		String[] interfaces = type.getSuperInterfaceNames();
		
		for(int i=0;i<interfaces.length;i++){
			String interfaceName = UMLJavaUtils.getFullQName(type, interfaces[i]);
			List<AbstractUMLModel>  children = root.getChildren();
			for(int j=0;j<children.size();j++){
				Object obj = children.get(j);
				if(obj instanceof InterfaceModel){
					String className = stripGenerics(((InterfaceModel)obj).getName());
					if(className != null && className.equals(interfaceName)){
						AbstractUMLConnectionModel conn = null;
						if(model instanceof ClassModel){
							conn = new RealizationModel();
						} else if(model instanceof InterfaceModel){
							conn = new GeneralizationModel();
						}
						conn.setSource(model);
						conn.setTarget((AbstractUMLEntityModel)obj);
						conn.attachSource();
						conn.attachTarget();
						break;
					}
				}
			}
		}
	}
	
	public static void appendAggregationConnection(RootModel root, IType type, 
			ClassModel model) throws JavaModelException {
		List<AbstractUMLModel>  children = model.getChildren();
		for(AbstractUMLModel obj: children){
			if(obj instanceof AttributeModel){
				AttributeModel attr = (AttributeModel) obj;
				String attrType = attr.getType();
				if(attrType.startsWith("List") || attrType.startsWith("java.util.List")){
					int fromIndex = attrType.indexOf('<');
					int endIndex = attrType.indexOf('>');
					if(fromIndex >= 0 && endIndex >= 0){
						attrType = attrType.substring(fromIndex + 1, endIndex);
						//System.out.println(attrType);
					}
				}
				attrType = attrType.replaceAll("<.*>", "");
				attrType = attrType.replaceAll("\\[\\]", "");
				attrType = getFullQName(type, attrType);
				
				List<AbstractUMLModel> entities = root.getChildren();
				for(AbstractUMLModel entity: entities){
					if(entity instanceof ClassModel){
						if(stripGenerics(((ClassModel) entity).getName()).equals(attrType)){
							AbstractUMLConnectionModel conn = new AggregationModel();
							conn.setSource((ClassModel) entity);
							conn.setTarget(model);
							conn.attachSource();
							conn.attachTarget();
							break;
						}
					} else if(entity instanceof InterfaceModel){
						if(stripGenerics(((InterfaceModel) entity).getName()).equals(attrType)){
							AbstractUMLConnectionModel conn = new AggregationModel();
							conn.setSource((InterfaceModel) entity);
							conn.setTarget(model);
							conn.attachSource();
							conn.attachTarget();
							break;
						}
					}
				}
			}
		}
	}

	public static String stripGenerics(String className){
		if(className != null){
			className = className.replaceAll("<.+?>", "");
		}
		return className;
	}
	
	public static IType[] getTypes(IJavaElement element){
		try {
			List<IType> list = new ArrayList<IType>();
			
			if(element instanceof ICompilationUnit){
				IType[] types = ((ICompilationUnit)element).getTypes();
				for(int i=0; i< types.length; i++){
					list.add(types[i]);
					extractTypes(list, types[i]);
				}
				
			} else if(element instanceof IClassFile){
				IType type = ((IClassFile) element).getType();
				list.add(type);
				extractTypes(list, type);
				
			} else if(element instanceof IType){
				IType type = (IType) element;
				list.add(type);
				extractTypes(list, type);
			}
			
			return list.toArray(new IType[list.size()]);
			
		} catch(JavaModelException ex){
			UMLPlugin.logException(ex);
		}
		return null;
	}
	
	private static void extractTypes(List<IType> list, IType type){
		try {
			IType[] types = type.getTypes();
			for(int i=0;i<types.length;i++){
				if(!list.contains(types[i])){
					list.add(types[i]);
				}
			}
		} catch(JavaModelException ex){
		}
	}
	
}
