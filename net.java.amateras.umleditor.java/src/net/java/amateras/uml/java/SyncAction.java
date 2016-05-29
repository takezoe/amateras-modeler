package net.java.amateras.uml.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart.DeleteCommand;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

public class SyncAction implements IEditorActionDelegate {
	
	private ClassDiagramEditor editor;
	private List<AbstractUMLEntityModel> target = new ArrayList<AbstractUMLEntityModel>();

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		this.editor = (ClassDiagramEditor)targetEditor;
		action.setEnabled(false);
	}

	public void run(IAction action) {
		if(target.isEmpty()){
			return;
		}
		
		for(AbstractUMLEntityModel model: new ArrayList<AbstractUMLEntityModel>(target)){
			String className  = null;
			
			if(model instanceof ClassModel){
				className = ((ClassModel) model).getName();
				
			} else if(model instanceof InterfaceModel){
				className = ((InterfaceModel) model).getName();
			} else if(model instanceof EnumModel){
				className = ((EnumModel) model).getName();
			}
			
			className = UMLJavaUtils.stripGenerics(className);
			
			IEditorInput input = this.editor.getEditorInput();
			if(input instanceof IFileEditorInput){
				try {
					IFile file = ((IFileEditorInput) input).getFile();
					IJavaProject javaProject = JavaCore.create(file.getProject());
					IType type = javaProject.findType(className);
					if(type != null && type.exists()){
						RootModel root = (RootModel) this.editor.getAdapter(RootModel.class);
						CommandStack stack = (CommandStack) editor.getAdapter(CommandStack.class);
						
						CommandChain commandChain = new CommandChain();
						
						DeleteCommand deleteCommand = new DeleteCommand();
						deleteCommand.setRootModel(root);
						deleteCommand.setTargetModel(model);
						commandChain.add(deleteCommand);
						
						ImportClassModelCommand importCommand = new ImportClassModelCommand(root, type);
						Rectangle rect = ((AbstractUMLEntityModel) model).getConstraint();
						importCommand.setLocation(new Point(rect.x, rect.y));
						commandChain.add(importCommand);
						
						stack.execute(commandChain);
					}
				} catch(JavaModelException ex){
					ex.printStackTrace();
				}
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		target.clear();
		
		// updates status (enable or disable)
		if(selection != null && selection instanceof IStructuredSelection){
			for(Object obj: ((IStructuredSelection) selection).toArray()){
				if(obj instanceof EditPart){
					Object model = ((EditPart) obj).getModel();
					if(model instanceof ClassModel || model instanceof InterfaceModel || model instanceof EnumModel){
						target.add((AbstractUMLEntityModel) model);
					}
				}
			}
		}
		if(target.isEmpty()){
			action.setEnabled(false);
		} else {
			action.setEnabled(true);
		}
	}
	
	/**
	 * This class is a wrapper to execute multiple commands as one command.
	 * 
	 * @author Naoki Takezoe
	 */
	private static class CommandChain extends Command {
		
		private List<Command> commandList = new ArrayList<Command>();
		
		public void add(Command command){
			commandList.add(command);
		}
		
		public void execute(){
			for(Command command: commandList){
				command.execute();
			}
		}
		
		public void undo(){
			List<Command> revervseList = new ArrayList<Command>(commandList);
			Collections.reverse(revervseList);
			
			for(Command command: revervseList){
				command.undo();
			}
		}
		
	}

}
