package net.java.amateras.uml.synchronizer;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

import net.java.amateras.uml.action.AsyncSyncAction;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.java.UMLJavaUtils;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

public class AsyncMoveAction implements AsyncSyncAction {
	
	private final List<AbstractUMLEntityModel> targetClasses;
	private final IPath movedToFile;
	private final String movedToFileStr;
	
	public AsyncMoveAction(List<AbstractUMLEntityModel> targetClasses, IPath movedToFile) {
		this.targetClasses = targetClasses;
		this.movedToFile = movedToFile;
		this.movedToFileStr = UMLJavaUtils.stripGenerics(movedToFile.toString());
	}

	@Override
	public void doSyncAction() {
		for(AbstractUMLEntityModel model: targetClasses) {
			
			CommonEntityModel modelComEntity = (CommonEntityModel)model;
			
			IJavaProject javaProject = null;
			String path = movedToFileStr;
			String[] pathPart = path.split("/");
			String searchProject = pathPart[1];
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IProject project = workspace.getRoot().getProject(searchProject);
			if (project.isOpen()) {
				javaProject = JavaCore.create(project);
			}
			
			if (javaProject != null) {
				try {
					String originType = modelComEntity.getSimpleName();
					String originTypeNoGeneric = UMLJavaUtils.stripGenerics(originType);
					String genericPart = originType.replaceFirst(originTypeNoGeneric, "");
					String[] splitOrigPath = modelComEntity.getPath().split("/");
					String originJavaFile = splitOrigPath[splitOrigPath.length-1].replaceFirst(".java", "");
					String moveToPackage = javaProject.findPackageFragment(movedToFile.removeLastSegments(1)).getElementName();
					String moveToClass = movedToFile.removeFileExtension().lastSegment();
					
					boolean nestedClass = !originTypeNoGeneric.equals(originJavaFile);
					String nestedSuffix = "";
					if (nestedClass) {
						nestedSuffix = "." + originType;
					}
					
					modelComEntity.setName(moveToPackage + "." + moveToClass + nestedSuffix + genericPart);
					modelComEntity.setPath(movedToFileStr);
					
					List<AbstractUMLModel> childrenAttr = modelComEntity.getChildren();
					for (AbstractUMLModel childUML : childrenAttr) {
						if (childUML instanceof AttributeModel) {
							AttributeModel childAttr = (AttributeModel) childUML;
							if (originType.compareTo(childAttr.getType()) == 0) {
								childAttr.setType(moveToClass);
							}
						}
					}
				} catch (JavaModelException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
