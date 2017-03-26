package net.java.amateras.uml.java;

import java.util.ArrayList;
import java.util.List;

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

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.AggregationModel;
import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.GeneralizationModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.RealizationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

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
		} else if(model instanceof EnumModel){
			return ((EnumModel)model).getName();
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
				attr.setFinal(Flags.isFinal(fields[i].getFlags()));
				attr.setEnumCst(fields[i].isEnumConstant());
				
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
				ope.setFinal(Flags.isFinal(methods[i].getFlags()));
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
			AbstractUMLEntityModel model,
			List<RealizationModel> oldRealisationConnections,
			List<GeneralizationModel> oldGeneralizationConnections) throws JavaModelException {
		appendSuperClassConnection(root, type, model, null, oldRealisationConnections, oldGeneralizationConnections);
	}
	public static void appendSuperClassConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model, CommonEntityModel restrict2Model,
			List<RealizationModel> oldRealisationConnections,
			List<GeneralizationModel> oldGeneralizationConnections) throws JavaModelException {
		
		if(type.getSuperclassName()==null){
			return;
		}
		
		String superClass = UMLJavaUtils.getFullQName(type, type.getSuperclassName());
		
		List<AbstractUMLModel> children = root.getChildren();
		for(int i=0;i<children.size();i++){
			Object obj = children.get(i);
			if (restrict2Model != null) {
				if (restrict2Model.equals(obj) == false) {
					//If we restrict super class connections to one model, we discard all others
					continue;
				}
			}
			String className = stripGenerics(UMLJavaUtils.getClassName(obj));
			if(className!=null && className.equals(superClass)){
				searchConnectPreExistingGeneralization((CommonEntityModel)obj, (CommonEntityModel) model, oldGeneralizationConnections);
				break;
			}
		}
	}
	
	public static void appendSubConnection(	RootModel root, IJavaProject project,
											AbstractUMLEntityModel model, List<AbstractUMLEntityModel> excludedModels,
											boolean synchronizeAction,
											List<AssociationModel> oldAssociationConnections,
											List<RealizationModel> oldRealisationConnections,
											List<GeneralizationModel> oldGeneralizationConnections){
		List<AbstractUMLModel>  children = root.getChildren();
		for(int i = 0 ; i< children.size(); i++){
			AbstractUMLEntityModel child = (AbstractUMLEntityModel) children.get(i);
			//Bug fix: with excluded model
			//When a set of class is added to a class diagram (drag and drop), we do not process sub connection elements
			//of a class which are already within the imported class (excludedModels) otherwise they would be processed
			//twice and thus leads to multiple association link
			if ((child != model) && (excludedModels.contains(child) == false)) {
				if(child instanceof InterfaceModel){
					String name = ((InterfaceModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							if (synchronizeAction == false) {
								appendInterfacesConnection(root, type, child, oldRealisationConnections,
										oldGeneralizationConnections);
							}
							else {
								appendInterfacesConnection(root, type, child, (CommonEntityModel) model,
										oldRealisationConnections, oldGeneralizationConnections);
							}
						}
					} catch(JavaModelException ex){
						ex.printStackTrace();
					}
				}
				if(child instanceof ClassModel){
					String name = ((ClassModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							if (synchronizeAction == false) {
								appendSuperClassConnection(root, type, child, oldRealisationConnections, oldGeneralizationConnections);
								appendInterfacesConnection(root, type, child, oldRealisationConnections, oldGeneralizationConnections);
								appendAssociationConnection(root, type, (ClassModel) child, oldAssociationConnections);
							}
							else {
								//In synchronization case (synchro of a class of model with java source file),
								//we force to refresh only sub connections linked with the model actually refreshed.
								//Otherwise it will leads to multiply association link of other classes.
								appendSuperClassConnection(root, type, child, (CommonEntityModel) model, oldRealisationConnections, oldGeneralizationConnections);
								appendInterfacesConnection(root, type, child, (CommonEntityModel) model, oldRealisationConnections, oldGeneralizationConnections);
								appendAssociationConnection(root, type, (ClassModel) child, (CommonEntityModel) model, oldAssociationConnections);
							}
						}
					} catch(JavaModelException ex){
						ex.printStackTrace();
					}
				}
				if(child instanceof EnumModel){
					String name = ((EnumModel) child).getName();
					try {
						IType type = project.findType(name);
						if(type != null){
							if (synchronizeAction == false) {
								appendInterfacesConnection(root, type, child, oldRealisationConnections, oldGeneralizationConnections);
								appendAssociationConnection(root, type, (EnumModel) child, oldAssociationConnections);
							}
							else {
								appendInterfacesConnection(root, type, child, (CommonEntityModel) model, oldRealisationConnections, oldGeneralizationConnections);
								appendAssociationConnection(root, type, (EnumModel) child, (CommonEntityModel) model, oldAssociationConnections);
							}
						}
					} catch(JavaModelException ex){
						ex.printStackTrace();
					}
				}
			}
		}
	}
	
	public static void appendInterfacesConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model,
			List<RealizationModel> oldRealisationConnections,
			List<GeneralizationModel> oldGeneralizationConnections) throws JavaModelException {
		appendInterfacesConnection(root, type, model, null, oldRealisationConnections, oldGeneralizationConnections);
	}
	
	public static void appendInterfacesConnection(RootModel root, IType type, 
			AbstractUMLEntityModel model, CommonEntityModel restrict2Model,
			List<RealizationModel> oldRealisationConnections,
			List<GeneralizationModel> oldGeneralizationConnections) throws JavaModelException {
		
		String[] interfaces = type.getSuperInterfaceNames();
		
		for(int i=0;i<interfaces.length;i++){
			String interfaceName = UMLJavaUtils.getFullQName(type, interfaces[i]);
			List<AbstractUMLModel>  children = root.getChildren();
			for(int j=0;j<children.size();j++){
				Object obj = children.get(j);
				if (restrict2Model != null) {
					if (restrict2Model.equals(obj) == false) {
						//If we restrict interface connections to one model, we discard all others
						continue;
					}
				}
				if(obj instanceof InterfaceModel){
					String className = stripGenerics(((InterfaceModel)obj).getName());
					if(className != null && className.equals(interfaceName)){
						if(model instanceof ClassModel || model instanceof EnumModel){
							searchConnectPreExistingRealizationModel((CommonEntityModel)obj, (CommonEntityModel) model, oldRealisationConnections);
						} else if(model instanceof InterfaceModel){
							searchConnectPreExistingGeneralization((CommonEntityModel)obj, (CommonEntityModel) model, oldGeneralizationConnections);
						}
						break;
					}
				}
			}
		}
	}
	
	private static void searchConnectPreExistingRealizationModel(CommonEntityModel target, CommonEntityModel source,
			List<RealizationModel> oldRealisationConnections) {
		RealizationModel conn = null;
		for (RealizationModel gc : oldRealisationConnections) {
			AbstractUMLEntityModel src = gc.getSource();
			AbstractUMLEntityModel trgt = gc.getTarget();
			if (src instanceof CommonEntityModel && trgt instanceof CommonEntityModel) {
				if (		((CommonEntityModel)src).getName().equals(source.getName())
						&&	((CommonEntityModel)trgt).getName().equals(target.getName())) {
					conn = gc;
					oldRealisationConnections.remove(conn);
					break;
				}
			}
		}
		if (conn == null) {
			conn = new RealizationModel();
		}
		conn.setSource(source);
		conn.setTarget(target);
		conn.attachSource();
		conn.attachTarget();
	}
	
	private static void searchConnectPreExistingGeneralization(CommonEntityModel target, CommonEntityModel source,
			List<GeneralizationModel> oldGeneralizationConnections) {
		GeneralizationModel conn = null;
		for (GeneralizationModel gc : oldGeneralizationConnections) {
			AbstractUMLEntityModel src = gc.getSource();
			AbstractUMLEntityModel trgt = gc.getTarget();
			if (src instanceof CommonEntityModel && trgt instanceof CommonEntityModel) {
				if (		((CommonEntityModel)src).getName().equals(source.getName())
						&&	((CommonEntityModel)trgt).getName().equals(target.getName())) {
					conn = gc;
					oldGeneralizationConnections.remove(conn);
					break;
				}
			}
		}
		if (conn == null) {
			conn = new GeneralizationModel();
		}
		conn.setSource(source);
		conn.setTarget(target);
		conn.attachSource();
		conn.attachTarget();
	}
	
	public static void appendAssociationConnection(RootModel root, IType type, 
			CommonEntityModel model, List<AssociationModel> oldAssociationConnections) throws JavaModelException {
		appendAssociationConnection(root, type, model, null, oldAssociationConnections);
	}
	
	//@param restrict2Model	if null the aggregation connection is done against any other element.
	//						If not null aggregation process only against "restrict2Model".
	//						Useful for synchronize action since in this case the element is suppressed
	//						and then added, only this element need to have its sub connection refreshed
	//						otherwise other elements would have multiple erroneous associations
	private static void appendAssociationConnection(RootModel root, IType type, 
			CommonEntityModel model, CommonEntityModel restrict2Model,
			List<AssociationModel> oldAssociationConnections) throws JavaModelException {
		List<AbstractUMLModel>  children = model.getChildren();
		for(AbstractUMLModel obj: children){
			if(obj instanceof AttributeModel){
				AttributeModel attr = (AttributeModel) obj;
				if (attr.isStatic()) {
					continue; //Static elements do not belong to instance of class, so there is no mean to aggregate them
				}
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
					if (restrict2Model != null) {
						if (restrict2Model.equals(entity) == false) {
							//If we restrict aggregation connections to one model, we discard all others
							continue;
						}
					}
					if(entity instanceof ClassModel){
						if(stripGenerics(((ClassModel) entity).getName()).equals(attrType)){
							searchConnectPreExistingConnection((ClassModel) entity, model, oldAssociationConnections);
							break;
						}
						else {
							// In case, if an association existed (and shall be relinked) but not an attribute with corresponding type exists in the class
							if (oldAssociationConnections != null) {
								searchConnectPreExistingConnection((ClassModel) entity, model, oldAssociationConnections);
							}
						}
					} else if(entity instanceof InterfaceModel){
						if(stripGenerics(((InterfaceModel) entity).getName()).equals(attrType)){
							searchConnectPreExistingConnection((InterfaceModel) entity, model, oldAssociationConnections);
							break;
						}
						else {
							// In case, if an association existed (and shall be relinked) but not an attribute with corresponding type exists in the class
							if (oldAssociationConnections != null) {
								searchConnectPreExistingConnection((InterfaceModel) entity, model, oldAssociationConnections);
							}
						}
					} else if(entity instanceof EnumModel){
						boolean addElmt = false;
						// Try to avoid that the enum is aggregate to itself, since by definition
						// an enum contains some elements of its own type which are static and final
						if (type.isEnum() == false) {
							addElmt = true;
						}
						else {
							if (model.getName().compareTo(((EnumModel) entity).getName()) != 0) {
								addElmt = true;
							}
						}
						if (addElmt) {
							if(stripGenerics(((EnumModel) entity).getName()).equals(attrType)){
								searchConnectPreExistingConnection((EnumModel) entity, model, oldAssociationConnections);
								break;
							}
							else {
								// In case, if an association existed (and shall be relinked) but not an attribute with corresponding type exists in the class
								if (oldAssociationConnections != null) {
									searchConnectPreExistingConnection((EnumModel) entity, model, oldAssociationConnections);
								}
							}
						}
					}
				}
			}
		}
	}
	
	/** Search and connect a preexisting connection association if exist, when diagram is synchronized.
	 * If oldAssociationConnections is null, a new connection is created: it corresponds of case when
	 * when a java class is newly dropped into class diagram */
	private static void searchConnectPreExistingConnection(CommonEntityModel source, CommonEntityModel target,
			List<AssociationModel> oldAssociationConnections) {
		List<AssociationModel> conns = new ArrayList<AssociationModel>();
		if (oldAssociationConnections != null) {
			for (AssociationModel ac : oldAssociationConnections) {
				AbstractUMLEntityModel src = ac.getSource();
				AbstractUMLEntityModel trgt = ac.getTarget();
				if (src instanceof CommonEntityModel && trgt instanceof CommonEntityModel) {
					if (		((CommonEntityModel)src).getName().equals(source.getName())
							&&	((CommonEntityModel)trgt).getName().equals(target.getName())) {
						conns.add(ac);
					}
				}
			}
			oldAssociationConnections.removeAll(conns);
		}
		else {
			// In case of class newly added (no already existing association), we can eventually
			// create new association
			if (addAggregationAutomatically()) {
				conns.add(new AggregationModel());
			}
		}
		for (AssociationModel conn : conns) {
			conn.setSource(source);
			conn.setTarget(target);
			conn.attachSource();
			conn.attachTarget();
		}
	}
	
	private static boolean addAggregationAutomatically() {
		return UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_CLASS_DIAGRAM_CREATE_AGGREGATION_ON_IMPORT);
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
