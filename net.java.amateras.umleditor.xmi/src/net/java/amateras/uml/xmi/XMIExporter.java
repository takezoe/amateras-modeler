/**
 * 
 */
package net.java.amateras.uml.xmi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.BehavioredClassifier;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Element;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Interface;
import org.eclipse.uml2.uml.InterfaceRealization;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Type;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.VisibilityKind;

/**
 * @author shida
 * 
 */
public class XMIExporter {

	private UMLFactory factory = UMLFactory.eINSTANCE;

	private Model root;

	private Map packageMap = new HashMap();

	private Map typeMap = new HashMap();

	public XMIExporter() {
		root = factory.createModel();
		PrimitiveType intType = factory.createPrimitiveType();
		intType.setName("int");
		PrimitiveType booleanType = factory.createPrimitiveType();
		booleanType.setName("boolean");
		PrimitiveType shortType = factory.createPrimitiveType();
		shortType.setName("short");
		PrimitiveType longType = factory.createPrimitiveType();
		longType.setName("long");
		PrimitiveType floatType = factory.createPrimitiveType();
		floatType.setName("float");
		PrimitiveType doubleType = factory.createPrimitiveType();
		doubleType.setName("double");
		PrimitiveType charType = factory.createPrimitiveType();
		charType.setName("char");
		PrimitiveType stringType = factory.createPrimitiveType();
		stringType.setName("string");

		typeMap.put("short", shortType);
		typeMap.put("int", intType);
		typeMap.put("long", longType);
		typeMap.put("float", floatType);
		typeMap.put("double", doubleType);
		typeMap.put("boolean", booleanType);
		typeMap.put("char", charType);
		typeMap.put("string", stringType);

		root.getPackagedElements().add(booleanType);
		root.getPackagedElements().add(intType);
		root.getPackagedElements().add(shortType);
		root.getPackagedElements().add(longType);
		root.getPackagedElements().add(floatType);
		root.getPackagedElements().add(doubleType);
		root.getPackagedElements().add(charType);
		root.getPackagedElements().add(stringType);
	}

	public void convertType(AbstractUMLEntityModel model) {
		if (model instanceof ClassModel) {
			createClass((ClassModel) model);
		} else if (model instanceof InterfaceModel) {
			createInterface((InterfaceModel) model);
		}
		//TODO JLD : Manage enum case
	}

	public void convertStructure(AbstractUMLEntityModel model) {
		addAttributes(model);
		addOperations(model);
	}

	public void convertLink(AbstractUMLEntityModel model) {
		createGeneralization(model);
		createRealization(model);
	}
	
	public Model getRoot() {
		return root;
	}

	private void createClass(ClassModel model) {
		Class clazz = factory.createClass();
		clazz.setName(getSimpleName(model.getName()));
		Package pkg = getPackage(model.getName());
		pkg.getPackagedElements().add(clazz);
		typeMap.put(clazz.getName(), clazz);
	}

	private void createInterface(InterfaceModel model) {
		Interface interface1 = factory.createInterface();
		interface1.setName(getSimpleName(model.getName()));
		Package pkg = getPackage(model.getName());
		pkg.getPackagedElements().add(interface1);
		typeMap.put(interface1.getName(), interface1);
	}

	private Type createDataType(String fqcn) {
		String simpleName = getSimpleName(fqcn);
		DataType type = factory.createDataType();
		type.setName(simpleName);
		Package package1 = getPackage(fqcn);
		package1.getPackagedElements().add(type);
		typeMap.put(fqcn, type);
		return type;
	}

	private void addAttributes(AbstractUMLEntityModel model) {
		List attrs = getAttributes(model);
		Classifier classifier = (Classifier) typeMap
				.get(getName(model));
		for (Iterator iter = attrs.iterator(); iter.hasNext();) {
			AttributeModel element = (AttributeModel) iter.next();
			Property property = factory.createProperty();
			property.setName(element.getName());
			if (typeMap.get(element.getType()) == null) {
				createDataType(element.getType());
			}
			Type t = (Type) typeMap.get(element.getType());
			property.setType(t);
			property.setVisibility(getVisibility(element.getVisibility()));
			if (classifier instanceof Class) {
				Class clazz = (Class) classifier;
				clazz.getOwnedAttributes().add(property);
			} else if (classifier instanceof Interface) {
				Interface interface1 = (Interface) classifier;
				interface1.getOwnedAttributes().add(property);
			}
		}
	}

	private void addOperations(AbstractUMLEntityModel model) {
		List opes = getOperations(model);
		Classifier classifier = (Classifier) typeMap
				.get(getName(model));
		for (Iterator iter = opes.iterator(); iter.hasNext();) {
			OperationModel element = (OperationModel) iter.next();
			Operation operation = factory.createOperation();
			operation.setName(element.getName());
			operation.setVisibility(getVisibility(element.getVisibility()));
			if (element.getType() != null && !"void".equals(element.getType())) {
				Parameter parameter = factory.createParameter();
				parameter.setName("return");
				Type t = (Type) typeMap.get(element.getType());
				if (t == null) {
					t = createDataType(element.getType());
				}
				parameter.setType(t);
				parameter.setDirection(ParameterDirectionKind.get(ParameterDirectionKind.RETURN));
				operation.getOwnedParameters().add(parameter);
			}
			
			List params = element.getParams();
			for (Iterator iterator = params.iterator(); iterator.hasNext();) {
				Argument arg = (Argument) iterator.next();
				Parameter parameter = factory.createParameter();
				parameter.setName(arg.getName());
				Type t = (Type) typeMap.get(arg.getType());
				if (t == null) {
					t = createDataType(arg.getType());
				}
				parameter.setType(t);
				operation.getOwnedParameters().add(parameter);
			}
			if (classifier instanceof Class) {
				Class class1 = (Class) classifier;
				class1.getOwnedOperations().add(operation);
			} else if (classifier instanceof Interface) {
				Interface interface1 = (Interface) classifier;
				interface1.getOwnedOperations().add(operation);
			}
		}
	}

	private String getName(AbstractUMLEntityModel model) {
		if (model instanceof ClassModel) {
			ClassModel classModel = (ClassModel) model;
			return getSimpleName(classModel.getName());
		} else if (model instanceof InterfaceModel) {
			InterfaceModel interfaceModel = (InterfaceModel) model;
			return getSimpleName(interfaceModel.getName());
		}
		return "";
	}

	private List getAttributes(AbstractUMLEntityModel model) {
		List list = model.getChildren();
		List rv = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			if (element instanceof AttributeModel) {
				rv.add(element);
			}
		}
		return rv;
	}

	private List getOperations(AbstractUMLEntityModel model) {
		List list = model.getChildren();
		List rv = new ArrayList();
		for (Iterator iter = list.iterator(); iter.hasNext();) {
			AbstractUMLModel element = (AbstractUMLModel) iter.next();
			if (element instanceof OperationModel) {
				rv.add(element);
			}
		}
		return rv;
	}

	private String getSimpleName(String fqcn) {
		String[] pkgs = fqcn.split("\\.");
		return pkgs[pkgs.length - 1];
	}

	private Package getPackage(String fqcn) {
		String[] pkgs = fqcn.split("\\.");
		String pkgName = "";
		Package lastPackage = null;

		if (pkgs.length > 2) {
			for (int i = 0; i < pkgs.length - 1; i++) {
				String p = pkgs[i];
				pkgName += p + ".";
				if (packageMap.get(pkgName) == null) {
					Package pkg = factory.createPackage();
					pkg.setName(p);
					if (lastPackage != null) {
						if (!isContain(lastPackage, p)) {
							lastPackage.getPackagedElements().add(pkg);
						}
					} else {
						if (!isContain(root, p)) {
							root.getPackagedElements().add(pkg);
						}
					}
					packageMap.put(pkgName, pkg);
				}

				lastPackage = (Package) packageMap.get(pkgName);
			}
		}
		if (lastPackage != null) {
			return lastPackage;
		} else {
			return root;
		}
	}

	private boolean isContain(Package pkg, String name) {
		EList ownedMembers = pkg.getOwnedMembers();
		for (Iterator iter = ownedMembers.iterator(); iter.hasNext();) {
			Element element = (Element) iter.next();
			if (element instanceof Package) {
				Package p = (Package) element;
				if (p.getName().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	private void createGeneralization(AbstractUMLEntityModel model) {
		Classifier source = (Classifier) typeMap.get(getSimpleName(getName(model)));
		List connections = model.getModelSourceConnections();
		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			AbstractUMLConnectionModel element = (AbstractUMLConnectionModel) iter.next();
			Generalization association = factory.createGeneralization();
			association.setSpecific(source);
			Classifier target = (Classifier) typeMap.get(getSimpleName(getName(element.getTarget())));
			association.setGeneral(target);
		}
	}

	private void createRealization(AbstractUMLEntityModel model) {
		Classifier source = (Classifier) typeMap.get(getSimpleName(getName(model)));
		if (!(source instanceof Interface)) {
			return;
		}
		List connections = model.getModelSourceConnections();
		for (Iterator iter = connections.iterator(); iter.hasNext();) {
			AbstractUMLConnectionModel element = (AbstractUMLConnectionModel) iter.next();
			InterfaceRealization realization = factory.createInterfaceRealization();
			realization.setContract((Interface) source);
			Classifier target = (Classifier) typeMap.get(getSimpleName(getName(element.getTarget())));
			if (target instanceof BehavioredClassifier) {
				realization.setImplementingClassifier((BehavioredClassifier) target);
			}
		}
	}
	
	private VisibilityKind getVisibility(Visibility kind) {
		if (kind.equals(Visibility.PACKAGE)) {
			return VisibilityKind.get(VisibilityKind.PACKAGE);
		} else if (kind.equals(Visibility.PRIVATE)) {
			return VisibilityKind.get(VisibilityKind.PRIVATE);
		} else if (kind.equals(Visibility.PROTECTED)) {
			return VisibilityKind.get(VisibilityKind.PROTECTED);
		} else {
			return VisibilityKind.get(VisibilityKind.PUBLIC);
		}
	}
}
