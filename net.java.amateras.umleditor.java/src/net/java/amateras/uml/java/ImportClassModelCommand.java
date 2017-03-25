package net.java.amateras.uml.java;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.classdiagram.model.DependencyModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.GeneralizationModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.classdiagram.model.RealizationModel;
import net.java.amateras.uml.model.AbstractUMLConnectionModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

/**
 * The command to add Java types to the class diagram.
 * 
 * @author Naoki Takezoe
 */
public class ImportClassModelCommand extends Command {
	
	private IType[] types;
	private RootModel root;
	private List<AbstractUMLEntityModel> models;
	private Point location;
	private boolean synchronizeAction = false;
	
	private List<RealizationModel> oldRealisationConnections = new ArrayList<RealizationModel>();
	private List<GeneralizationModel> oldGeneralizationConnections = new ArrayList<GeneralizationModel>();
	private List<DependencyModel> oldDependencyConnections = new ArrayList<DependencyModel>();
	private List<AssociationModel> oldAssociationConnections;
	
	/**
	 * Constructor for the one type adding.
	 * 
	 * @param root the root model
	 * @param type the type to add
	 */
	public ImportClassModelCommand(RootModel root,IType type){
		this(root, new IType[]{ type });
	}
	
	public ImportClassModelCommand(RootModel root,IType type, boolean synchronizeAction){
		this(root, new IType[]{ type });
		this.synchronizeAction = synchronizeAction;
	}
	
	/**
	 * Constructor for the two or more types adding.
	 * 
	 * @param root the root model
	 * @param types types to add
	 */
	public ImportClassModelCommand(RootModel root,IType[] types){
		this.root = root;
		this.types = types;
	}
	
	/**
	 * When an element is updated in class diagram, it is first deleted, its connections are also deleted.
	 * After it is reloaded from java file. Preexisting connections, may have memorized parameters (bendpoints...)
	 * So we keep the old connections and restore them.
	 * 
	 * @param preExistingSourceConnections
	 * @param preExistingTargetConnections
	 */
	public void setPreExistingConnections(	List<AbstractUMLConnectionModel> preExistingSourceConnections,
											List<AbstractUMLConnectionModel> preExistingTargetConnections) {
		oldAssociationConnections = new ArrayList<AssociationModel>();
		for (AbstractUMLConnectionModel connectionModel : preExistingSourceConnections) {
			if (connectionModel instanceof RealizationModel) {
				oldRealisationConnections.add((RealizationModel) connectionModel);
			}
			else if (connectionModel instanceof GeneralizationModel) {
				oldGeneralizationConnections.add((GeneralizationModel) connectionModel);
			}
			else if (connectionModel instanceof DependencyModel) {
				oldDependencyConnections.add((DependencyModel) connectionModel);
			}
			else if (connectionModel instanceof AssociationModel) {
				oldAssociationConnections.add((AssociationModel) connectionModel);
			}
		}
		for (AbstractUMLConnectionModel connectionModel : preExistingTargetConnections) {
			if (connectionModel instanceof RealizationModel) {
				oldRealisationConnections.add((RealizationModel) connectionModel);
			}
			else if (connectionModel instanceof GeneralizationModel) {
				oldGeneralizationConnections.add((GeneralizationModel) connectionModel);
			}
			else if (connectionModel instanceof DependencyModel) {
				oldDependencyConnections.add((DependencyModel) connectionModel);
			}
			else if (connectionModel instanceof AssociationModel) {
				oldAssociationConnections.add((AssociationModel) connectionModel);
			}
		}
	}
	
	public void setLocation(Point location){
		this.location = location;
	}
	
	@Override
	public void execute(){
		models = new ArrayList<AbstractUMLEntityModel>();
		List<AbstractUMLEntityModel> addedModels = new ArrayList<AbstractUMLEntityModel>();
		for(int i=0;i<types.length;i++){
			AbstractUMLEntityModel entity = createModel(types[i]);
			addedModels.add(entity);
			if(entity != null){
				if(location!=null){
					entity.setConstraint(new Rectangle(
							location.x + (i * 10), 
							location.y + (i * 10), -1, -1));
				} else {
					entity.setConstraint(new Rectangle(10, 10, -1, -1));
				}
				root.copyPresentation(entity);
				root.addChild(entity);
				models.add(entity);
			}
		}
		
		addConnections(addedModels);
		addAssociationsWithNoCorrespondingAttribute();
	}

	/**
	 * The deleted class may  previously had some association with other class, but no attribute of this kind.
	 * We try to had them again if possible.
	 */
	private void addAssociationsWithNoCorrespondingAttribute() {
		if (oldAssociationConnections == null) {
			return;
		}
		for (AssociationModel previousAssociation : oldAssociationConnections) {
			CommonEntityModel source = (CommonEntityModel) previousAssociation.getSource();
			CommonEntityModel target = (CommonEntityModel) previousAssociation.getTarget();
			for (AbstractUMLModel child : root.getChildren()) {
				if (child instanceof CommonEntityModel) {
					CommonEntityModel entityModel = (CommonEntityModel) child;
					if (source.getName().equals(entityModel.getName())) {
						for (AbstractUMLModel child2 : root.getChildren()) {
							if (child2 instanceof CommonEntityModel) {
								CommonEntityModel entityModel2 = (CommonEntityModel) child2;
								if (target.getName().equals(entityModel2.getName())) {
									previousAssociation.setSource(entityModel);
									previousAssociation.setTarget(entityModel2);
									previousAssociation.attachSource();
									previousAssociation.attachTarget();
									break;
								}
							}
						}
						break;
					}
				}
			}
		}
	}
	
	private void addConnections(List<AbstractUMLEntityModel> addedModels){
		try {
			for(int i=0;i<addedModels.size();i++){
				AbstractUMLEntityModel model = addedModels.get(i);
				if(types[i].isInterface()){
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
				} else if (types[i].isEnum()){
					//Impossible for enum to inherit from superclass
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
					UMLJavaUtils.appendAssociationConnection(this.root, types[i], (EnumModel) model, oldAssociationConnections);
				} else {
					UMLJavaUtils.appendSuperClassConnection(this.root, types[i], model);
					UMLJavaUtils.appendInterfacesConnection(this.root, types[i], model);
					UMLJavaUtils.appendAssociationConnection(this.root, types[i], (ClassModel) model, oldAssociationConnections);
				}
				UMLJavaUtils.appendSubConnection(root, types[i].getJavaProject(), model, addedModels, synchronizeAction,
						oldAssociationConnections);
			}
		} catch(JavaModelException ex){
			UMLPlugin.logException(ex);
		}
	}
	
	private AbstractUMLEntityModel createModel(IType type){
		try {
			CommonEntityModel model = null;
			if(type.isInterface()){
				model = new InterfaceModel();
			}
			else if(type.isClass()){
				ClassModel modelClass = new ClassModel();
				if (Flags.isAbstract(type.getFlags())) {
					modelClass.setAbstract(true);
				}
				model = modelClass;
			}
			else if (type.isEnum()) {
				model = new EnumModel();
			}
			else {
				return null;
			}
			
			model.setName(type.getFullyQualifiedParameterizedName());
			model.setPath(type.getPath().toString());
			AttributeModel[] fieldsAttrs = UMLJavaUtils.getFields(type);
			for (int i = 0 ; i < fieldsAttrs.length ; ++i) {
				model.addChild(fieldsAttrs[i]);
			}
			OperationModel[] methods = UMLJavaUtils.getMethods(type);
			for(int i=0;i<methods.length;i++){
				model.addChild(methods[i]);
			}
			
			return model;
		} catch(Exception ex){
			UMLPlugin.logException(ex);
		}
		return null;
	}
	
	@Override
	public void undo(){
		for(AbstractUMLEntityModel model: models){
			for(AbstractUMLConnectionModel conn: model.getModelSourceConnections()){
				conn.detachSource();
				conn.detachTarget();
			}
			for(AbstractUMLConnectionModel conn: model.getModelTargetConnections()){
				conn.detachSource();
				conn.detachTarget();
			}
			this.root.removeChild(model);
		}
	}
}
