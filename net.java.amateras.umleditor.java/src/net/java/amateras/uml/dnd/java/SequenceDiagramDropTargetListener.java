/**
 * 
 */
package net.java.amateras.uml.dnd.java;

import net.java.amateras.uml.java.UMLJavaUtils;

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

/**
 * 
 * @author shida
 * @author Naoki Takezoe
 */
public class SequenceDiagramDropTargetListener extends
		AbstractTransferDropTargetListener {

	public SequenceDiagramDropTargetListener(EditPartViewer viewer, Transfer xfer) {
		super(viewer, xfer);
	}

	protected void handleDragOver() {
		getCurrentEvent().detail = DND.DROP_COPY;
		super.handleDragOver();
	}
	
	protected Request createTargetRequest() {
		CreateRequest request = new CreateRequest();
		return request;
	}

	private InstanceModelConverter getConverter(IStructuredSelection selection){
		Object firstElement = selection.getFirstElement();
		if(firstElement instanceof IJavaElement){
			IType[] types = UMLJavaUtils.getTypes((IJavaElement) firstElement);
			if(types != null && types.length > 0){
				return new InstanceModelConverter(types[0]);
			}
		}
		return null;
	}
	
	protected void updateTargetRequest() {
		((CreateRequest) getTargetRequest()).setLocation(getDropLocation());
	}
	
	protected Command getCommand() {
		InstanceModelConverter converter = getConverter((IStructuredSelection) getCurrentEvent().data);
		if (converter == null) {
			return null;
		}
		return super.getCommand();
	}
	
	protected void handleDrop() {
		InstanceModelConverter converter = getConverter((IStructuredSelection) getCurrentEvent().data);
		if(converter!=null){
			((CreateRequest) getTargetRequest()).setFactory(converter);
		}
		super.handleDrop();
		getCurrentEvent().detail = DND.DROP_COPY;
	}
}
