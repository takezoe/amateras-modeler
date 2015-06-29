package net.java.amateras.uml.classdiagram.action;

import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.model.OperationModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.commands.CommandStack;

public class AddOperationAction extends AbstractTypeAction {

	public AddOperationAction(CommandStack stack, GraphicalViewer viewer){
		super(UMLPlugin.getDefault().getResourceString("menu.addOperation"), stack, viewer);
	}
	
	public void run(){
		stack.execute(new AddOperationCommand(target));
	}
	
	private static class AddOperationCommand extends Command {
		
		private OperationModel operation;
		private AbstractUMLEntityModel target;
		
		public AddOperationCommand(AbstractUMLEntityModel target){
			this.target = target;
		}
		
		public void execute(){
			operation = new OperationModel();
			int count = 1;
			List<AbstractUMLModel> list = target.getChildren();
			for(int i=0;i<list.size();i++){
				if(list.get(i) instanceof OperationModel){
					count++;
				}
			}
			operation.setName("operation" + count);
			target.copyPresentation(operation);
			target.addChild(operation);
		}
		
		public void undo() {
			target.removeChild(operation);
		}
	}

}
