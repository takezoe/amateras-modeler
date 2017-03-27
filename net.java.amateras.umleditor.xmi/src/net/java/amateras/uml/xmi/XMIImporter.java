package net.java.amateras.uml.xmi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.classdiagram.model.Argument;
import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.GeneralizationModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.RealizationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.model.AbstractUMLEntityModel;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.util.EList;
import org.eclipse.uml2.uml.Association;
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
import org.eclipse.uml2.uml.PrimitiveType;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.VisibilityKind;

public class XMIImporter {

	private Map ecoreTypeMap = new HashMap();

	private int x;

	private int y;

	private List amaterasModels;
	
	public XMIImporter() {
		amaterasModels = new ArrayList();
		
	}

	public void convertNodes(Element element) {
		if (element instanceof Class) {
			ClassModel classModel = createClassModel((Class) element);
			String name = createFullyQualifiedName((Classifier) element);
			classModel.setName(name);
			layoutModel(classModel);
			ecoreTypeMap.put(element, classModel);
			amaterasModels.add(classModel);
		} else if (element instanceof Interface) {
			InterfaceModel interfaceModel = createInterfaceModel((Interface) element);
			String name = createFullyQualifiedName((Classifier) element);
			interfaceModel.setName(name);
			layoutModel(interfaceModel);
			ecoreTypeMap.put(element, interfaceModel);
			amaterasModels.add(interfaceModel);
		} else if (element instanceof DataType
				&& !(element instanceof PrimitiveType)) {
			DataType type = (DataType) element;
			ClassModel classModel = new ClassModel();
			classModel.setName(type.getName());
			classModel.setStereoType("DataType");
			layoutModel(classModel);
			ecoreTypeMap.put(type, classModel);
			amaterasModels.add(classModel);
		}
		//TODO JLD : manage enum case
	}
	
	public void convertLinks(Element element) {
		if (element instanceof Generalization) {
			createGeneralization((Generalization) element);
		} else if (element instanceof InterfaceRealization) {
			createRealization(element);
		} else if (element instanceof Association) {
			createAssociation(element);
		}		
	}
	
	private void layoutModel(AbstractUMLEntityModel model) {
		model.setConstraint(new Rectangle(x, y, -1, -1));
		x += 200;
		if (x > 1000) {
			x = 0;
			y += 200;
		}		
	}
	
	public Collection getConvertedModel() {
		return amaterasModels;
	}

	private String createFullyQualifiedName(Classifier classifier) {
		if (classifier.eContainer() instanceof Package && !(classifier.eContainer() instanceof Model)) {
			return getPackageName((Package) classifier.eContainer()) + "." + classifier.getName();
		} else {
			return classifier.getName();
		}
	}
	
	private String getPackageName(Package pkg) {
		if (pkg.eContainer() instanceof Package && !(pkg.eContainer() instanceof Model)) {
			return getPackageName((Package) pkg.eContainer()) + "." + pkg.getName();
		} else {
			return pkg.getName() != null ? pkg.getName() : "";
		}
	}
	private void createAssociation(Element element) {
		try {
			Association association = (Association) element;
			EList ends = association.getMemberEnds();
			AbstractUMLEntityModel sourceModel = null;
			AbstractUMLEntityModel targetModel = null;
			// TODO force convert to Association model
			AssociationModel model = new AssociationModel();

			if (ends.size() == 2) {
				for (Iterator iterator = ends.iterator(); iterator.hasNext();) {
					Property p = (Property) iterator.next();
					if (p.eContainer().equals(association)) {
						targetModel = (AbstractUMLEntityModel) ecoreTypeMap.get(p
								.getType());
						int upper = p.getUpper();
						model.setToMultiplicity(upper == -1 ? "*" : String
								.valueOf(upper));
					} else {
						sourceModel = (AbstractUMLEntityModel) ecoreTypeMap.get(p
								.getType());
						int upper = p.getUpper();
						model.setFromMultiplicity(upper == -1 ? "*"
								: String.valueOf(upper));
					}
				}
				if (sourceModel != null && targetModel != null) {
					model.setSource(sourceModel);
					model.setTarget(targetModel);
					model.attachSource();
					model.attachTarget();
				}
			}
		} catch (RuntimeException e) {
			Activator.getDefault().warnning("Association", element.toString(), e);
		}
	}

	private void createRealization(Element element) {
		try {
			InterfaceRealization realization = (InterfaceRealization) element;
			RealizationModel realizationModel = new RealizationModel();
			AbstractUMLEntityModel source = (AbstractUMLEntityModel) ecoreTypeMap
					.get(realization.getImplementingClassifier());
			AbstractUMLEntityModel target = (AbstractUMLEntityModel) ecoreTypeMap
					.get(realization.getContract());
			if (source != null && target != null) {
				realizationModel.setSource(source);
				realizationModel.setTarget(target);
				realizationModel.attachSource();
				realizationModel.attachTarget();
			}
		} catch (RuntimeException e) {
			Activator.getDefault().warnning("Realization", element.toString(),
					e);
		}
	}

	private void createGeneralization(Generalization generalization) {
		try {
			GeneralizationModel generalizationModel = new GeneralizationModel();
			AbstractUMLEntityModel source = (AbstractUMLEntityModel) ecoreTypeMap
					.get(generalization.getSpecific());
			AbstractUMLEntityModel target = (AbstractUMLEntityModel) ecoreTypeMap
					.get(generalization.getGeneral());
			if (source != null && target != null) {
				generalizationModel.setSource(source);
				generalizationModel.setTarget(target);
				generalizationModel.attachSource();
				generalizationModel.attachTarget();
			}
		} catch (RuntimeException e) {
			Activator.getDefault().warnning("Generalization",
					generalization.toString(), e);
		}
	}

	private ClassModel createClassModel(Class c) {
		ClassModel cm = new ClassModel();
		try {
			cm.setName(c.getName());
			EList attributes = c.getAttributes();
			for (Iterator iterator = attributes.iterator(); iterator.hasNext();) {
				Property prop = (Property) iterator.next();
				AttributeModel am = new AttributeModel();
				am.setStatic(prop.isStatic());
				am.setVisibility(getVisibility(prop.getVisibility()));
				am.setName(prop.getName());
				am.setType(prop.getType().getName());
				cm.addChild(am);
			}

			EList operations = c.getOperations();
			for (Iterator iterator = operations.iterator(); iterator.hasNext();) {
				Operation ope = (Operation) iterator.next();
				OperationModel model = createOperationModel(ope);
				cm.addChild(model);
			}
		} catch (RuntimeException e) {
			Activator.getDefault().warnning("Class", c.getName(), e);
		}
		return cm;
	}

	private InterfaceModel createInterfaceModel(Interface interface1) {
		InterfaceModel model = new InterfaceModel();
		try {
			model.setName(interface1.getName());
			EList operations = interface1.getOperations();
			for (Iterator iterator = operations.iterator(); iterator.hasNext();) {
				Operation ope = (Operation) iterator.next();
				OperationModel operationModel = createOperationModel(ope);
				model.addChild(operationModel);
			}
		} catch (RuntimeException e) {
			Activator.getDefault().warnning("Interface", interface1.getName(),
					e);
		}
		return model;
	}

	private OperationModel createOperationModel(Operation ope) {
		OperationModel model = null;
		try {
			model = new OperationModel();
			model.setAbstract(ope.isAbstract());
			model.setStatic(ope.isStatic());
			model.setName(ope.getName());
			model.setVisibility(getVisibility(ope.getVisibility()));
			if (ope.getReturnResult() != null) {
				model.setType(ope.getReturnResult().getType().getName());
			}
			EList parameters = ope.getOwnedParameters();
			List params = new ArrayList();
			for (Iterator ite = parameters.iterator(); ite.hasNext();) {
				Parameter param = (Parameter) ite.next();
				if (!param.equals(ope.getReturnResult())) {
					Argument argument = new Argument();
					argument.setName(param.getName());
					argument.setType(param.getType().getName());
					params.add(argument);
				}
			}
			model.setParams(params);
		} catch (RuntimeException e) {
			Activator.getDefault().warnning("Operation", ope.getName(), e);
		}
		return model;
	}

	private Visibility getVisibility(VisibilityKind kind) {
		int value = kind.getValue();
		switch (value) {
		case VisibilityKind.PACKAGE:
			return Visibility.PACKAGE;
		case VisibilityKind.PRIVATE:
			return Visibility.PRIVATE;
		case VisibilityKind.PROTECTED:
			return Visibility.PROTECTED;
		case VisibilityKind.PUBLIC:
			return Visibility.PUBLIC;
		default:
			return Visibility.PACKAGE;
		}
	}
}
