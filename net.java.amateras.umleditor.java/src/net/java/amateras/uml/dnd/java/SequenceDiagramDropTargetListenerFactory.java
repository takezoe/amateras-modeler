/**
 * 
 */
package net.java.amateras.uml.dnd.java;

import net.java.amateras.uml.dnd.UMLDropTargetListenerFactory;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;

/**
 * @author shida
 *
 */
public class SequenceDiagramDropTargetListenerFactory extends
		UMLDropTargetListenerFactory {

	/* (non-Javadoc)
	 * @see net.java.amateras.uml.dnd.UMLDropTargetListenerFactory#getDropTargetListener(org.eclipse.gef.EditPartViewer)
	 */
	public TransferDropTargetListener getDropTargetListener(
			EditPartViewer viewer) {
		return new SequenceDiagramDropTargetListener(viewer, LocalSelectionTransfer.getTransfer());
	}

	/* (non-Javadoc)
	 * @see net.java.amateras.uml.dnd.UMLDropTargetListenerFactory#accept(java.lang.String)
	 */
	public boolean accept(String key) {
		if ("sequence".equals(key)) {
			return true;
		}
		return false;
	}

}
