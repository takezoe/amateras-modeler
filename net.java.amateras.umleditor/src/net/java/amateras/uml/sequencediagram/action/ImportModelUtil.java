/**
 * 
 */
package net.java.amateras.uml.sequencediagram.action;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import net.java.amateras.uml.DiagramSerializer;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.AttributeModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.EntityModel;
import net.java.amateras.uml.model.RootModel;
import net.java.amateras.uml.sequencediagram.ClassModelImageResolver;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;

/**
 * @author Takahiro Shida.
 *
 */
public class ImportModelUtil {

	public static IFile selectClassDiagram() {
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				new WorkbenchLabelProvider(), new WorkbenchContentProvider());
		dialog.setInput(ResourcesPlugin.getWorkspace());
		dialog.addFilter(new ViewerFilter(){
	    	public boolean select(Viewer viewer, Object parentElement, Object element){
				if(element instanceof IProject || element instanceof IFolder){
					return true;
				}
				if(element instanceof IFile){
					if(((IFile)element).getName().endsWith(".cld")){
						return true;
					}
				}
				return false;
			}
		});
		dialog.setAllowMultiple(false);
		dialog.setTitle(UMLPlugin.getDefault().getResourceString("fileSelectionDialog.title"));
		dialog.setValidator(new ISelectionStatusValidator() {

			public IStatus validate(Object[] selection) {
				for (int i = 0; i < selection.length; i++) {
					Object object = selection[i];
					if (object instanceof IFile) {
						IFile file = (IFile) object;
						if (file.getName().endsWith(".cld")) {
							return Status.OK_STATUS;
						}
						return new Status(IStatus.ERROR, UMLPlugin.PLUGIN_ID, 0,"",null);
					}
				}
				return new Status(IStatus.ERROR, UMLPlugin.PLUGIN_ID, 0,"",null);
			}
			
		});
		if (dialog.open() == Dialog.OK) {
			IFile file = (IFile) dialog.getFirstResult();
			return file;
		}
		return null;
	}
	
	public static AbstractUMLEntityModel selectClassModel(IFile file) {
		try {
			RootModel rootModel = DiagramSerializer.deserialize(new FileInputStream(file.getLocation().toFile()));
			ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					new ClassDiagramLabelProvider(),
					new ClassDiagramContentProvider()
					);
			dialog.setInput(rootModel);
			dialog.setAllowMultiple(false);
			dialog.setTitle(UMLPlugin.getDefault().getResourceString("modelSelectionDialog.title"));
			dialog.setValidator(new ISelectionStatusValidator() {

				public IStatus validate(Object[] selection) {
					for (int i = 0; i < selection.length; i++) {
						Object object = selection[i];
						if (object instanceof AbstractUMLEntityModel) {
							return Status.OK_STATUS;
						}
					}
					return new Status(IStatus.ERROR, UMLPlugin.PLUGIN_ID, 0,"",null);
				}
				
			});
			if (dialog.open() == Dialog.OK) {
				return (AbstractUMLEntityModel) dialog.getFirstResult();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static class ClassDiagramContentProvider implements ITreeContentProvider {

		public Object[] getChildren(Object parentElement) {
			if (parentElement instanceof RootModel) {
				RootModel model = (RootModel) parentElement;
				Object[] children = model.getChildren().toArray();
				Arrays.sort(children, new ClassModelComparator());
				return children;
			} else if (parentElement instanceof AbstractUMLEntityModel) {
				AbstractUMLEntityModel model = (AbstractUMLEntityModel) parentElement;
				Object[] children = model.getChildren().toArray();
				List attributes = new ArrayList();
				List operations = new ArrayList();
				for(int i=0;i<children.length;i++){
					if(children[i] instanceof AttributeModel){
						attributes.add(children[i]);
					} else if(children[i] instanceof OperationModel){
						operations.add(children[i]);
					}
				}
				attributes.addAll(operations);
				return attributes.toArray();
			} 
			return null;
		}

		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasChildren(Object element) {
			return element instanceof RootModel || element instanceof ClassModel;
		}

		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public void dispose() {
			
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			
		}
		
	}
	
	private static class ClassDiagramLabelProvider extends LabelProvider {
		
		public Image getImage(Object element) {
			if (element instanceof InterfaceModel) {
				return UMLPlugin.getImageDescriptor("icons/interface.gif").createImage();
			} else if (element instanceof ClassModel) {
				return UMLPlugin.getImageDescriptor("icons/class.gif").createImage();
			} else if (element instanceof EnumModel) {
				return UMLPlugin.getImageDescriptor("icons/enum.gif").createImage();
			} else if (element instanceof AttributeModel) {
				return ClassModelImageResolver.getAttributeImage((AttributeModel) element);
			} else if (element instanceof OperationModel) {
				return ClassModelImageResolver.getOperationImage((OperationModel) element);
			}
			return super.getImage(element);
		}
		
		public String getText(Object element) {
			if (element instanceof EntityModel) {
				return ((EntityModel) element).getName();
			}
			return super.getText(element);
		}
	}
	
	private static class ClassModelComparator implements Comparator {

		public int compare(Object obj1, Object obj2) {
			return getName(obj1).compareTo(getName(obj2));
		}
		
		private String getName(Object obj){
			if(obj instanceof ClassModel){
				return ((ClassModel)obj).getName();
			} else if(obj instanceof InterfaceModel){
				return ((InterfaceModel)obj).getName();
			} else if(obj instanceof EnumModel){
				return ((EnumModel)obj).getName();
			}
			return obj.toString();
		}
	}
}
