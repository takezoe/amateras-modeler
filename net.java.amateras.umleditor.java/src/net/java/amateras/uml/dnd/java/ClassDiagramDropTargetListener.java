/**
 * 
 */
package net.java.amateras.uml.dnd.java;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.java.amateras.uml.java.ImportClassModelCommand;
import net.java.amateras.uml.java.UMLJavaUtils;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.dnd.AbstractTransferDropTargetListener;
import org.eclipse.gef.requests.CreateRequest;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;

class ClassDiagramDropTargetListener extends AbstractTransferDropTargetListener {
	
	public ClassDiagramDropTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	@Override
	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}
	
	@Override
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		return request;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.gef.dnd.AbstractTransferDropTargetListener#getCommand()
	 */
	@Override
	protected Command getCommand() {
		RootModel root = (RootModel)getViewer().getContents().getModel();
		IStructuredSelection selection = (IStructuredSelection) getCurrentEvent().data;
		
		List<IType> types = new ArrayList<IType>();
		for(Iterator<?> ite = selection.iterator(); ite.hasNext();){
			Object element = ite.next();
			if(element instanceof IJavaElement){
				IType[] typeArray = UMLJavaUtils.getTypes((IJavaElement) element);
				for(int i=0;i<typeArray.length;i++){
					if(!types.contains(typeArray[i])){
						types.add(typeArray[i]);
					}
				}
			}
		}
		
		if(!types.isEmpty()){
			ImportClassModelCommand command = new ImportClassModelCommand(
					root, types.toArray(new IType[types.size()]));
			command.setLocation(getDropLocation());
			return command;
		}
		return null;
	}

	@Override
	protected void handleDrop() {
		super.handleDrop();
		getCurrentEvent().detail = DND.DROP_COPY;
	}

	@Override
	protected void updateTargetRequest() {
		// Nothing to do
	}

}