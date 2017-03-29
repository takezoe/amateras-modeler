package net.java.amateras.uml.java;

import java.util.List;

import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.EditPart;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * Opens the selected element in the editor.
 * 
 * @author Naoki Takezoe
 */
public class OpenSourceAction implements IEditorActionDelegate {
	
	private ClassDiagramEditor editor;
	private IStructuredSelection selection;
	
	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = (ClassDiagramEditor)targetEditor;
		action.setEnabled(false);
	}

	public void run(IAction action) {
		if(selection==null || selection.getFirstElement()==null){
			return;
		}
		
		Object obj = selection.getFirstElement();
		if(obj==null && !(obj instanceof EditPart)){
			return;
		}
		
		String className  = null;
		String methodName = null;
		String fieldName  = null;
		int methodIndex = 0;
		Object model = ((EditPart)obj).getModel();
		
		if(model instanceof ClassModel){
			className = ((ClassModel)model).getName();
			
		} else if(model instanceof InterfaceModel){
			className = ((InterfaceModel)model).getName();
			
		} else if(model instanceof EnumModel){
			className = ((EnumModel)model).getName();
			
		} else if(model instanceof OperationModel){
			EditPart parent = ((EditPart)obj).getParent();
			className = UMLJavaUtils.getClassName(parent.getModel());
			methodName = ((OperationModel)model).getName();
			methodIndex = getMethodIndex(parent, (OperationModel)model);
			
		} else if(model instanceof AttributeModel){
			EditPart parent = ((EditPart)obj).getParent();
			className = UMLJavaUtils.getClassName(parent.getModel());
			fieldName = ((AttributeModel)model).getName();
		}
		
		if(className!=null){
			className = UMLJavaUtils.stripGenerics(className);
			
			IFileEditorInput input = (IFileEditorInput)editor.getEditorInput();
			IJavaProject project = JavaCore.create(input.getFile().getProject());
			try {
				IType type = project.findType(className);
				if(type!=null && type.exists()){
					if(methodName!=null){
						IMethod method = getMethod(type, methodName, methodIndex);
						if(openInEditor(method)){
							return;
						}
					}
					if(fieldName!=null){
						IField field = type.getField(fieldName);
						if(openInEditor(field)){
							return;
						}
					}
					JavaUI.openInEditor(type);
				}
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	private boolean openInEditor(IJavaElement element){
		try {
			if(element!=null && element.exists()){
				//OpenActionUtil.open(element,true);
				JavaUI.openInEditor(element, true, true);
				return true;
			}
		} catch(Exception ex){
		}
		return false;
	}
	
	private int getMethodIndex(EditPart editPart, OperationModel model){
		Object parentModel = editPart.getModel();
		List<AbstractUMLModel> children = ((AbstractUMLEntityModel) parentModel).getChildren();
		int index = 0;
		for(AbstractUMLModel obj: children){
			if(obj instanceof OperationModel){
				if(((OperationModel)obj).getName().equals(model.getName())){
					if(obj==model){
						break;
					}
					index++;
				}
			}
		}
		return index;
	}
	
	private IMethod getMethod(IType type, String methodName, int index){
		try {
			IMethod[] methods = type.getMethods();
			int count = 0;
			for(int i=0;i<methods.length;i++){
				if(methods[i].getElementName().equals(methodName)){
					if(count==index){
						return methods[i];
					} else {
						count++;
					}
				}
			}
		} catch(Exception ex){
		}
		return null;
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		if(selection instanceof IStructuredSelection){
			this.selection = (IStructuredSelection)selection;
		} else {
			this.selection = null;
		}
		
		// updates status (enable or disable)
		if(this.selection!=null && this.selection.getFirstElement()!=null){
			Object obj = this.selection.getFirstElement();
			if(obj instanceof EditPart){
				Object model = ((EditPart)obj).getModel();
				if(model instanceof ClassModel || model instanceof InterfaceModel || model instanceof EnumModel ||
						model instanceof AttributeModel || model instanceof OperationModel){
					action.setEnabled(true);
					return;
				}
			}
		}
		action.setEnabled(false);
	}

}
