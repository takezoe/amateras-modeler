/**
 * 
 */
package net.java.amateras.uml.dnd.java;


import net.java.amateras.uml.dnd.UMLDropTargetListenerFactory;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.util.TransferDropTargetListener;

/**
 * @author Takahiro Shida.
 *
 */
public class ClassDiagramDropTargetListenerFactory extends
		UMLDropTargetListenerFactory {

	/* (non-Javadoc)
	 * @see net.java.amateras.uml.dnd.UMLDropTargetListenerFactory#getDropTargetListener(org.eclipse.gef.EditPartViewer)
	 */
	public TransferDropTargetListener getDropTargetListener(
			EditPartViewer viewer) {
		return new ClassDiagramDropTargetListener(viewer, LocalSelectionTransfer.getTransfer());
	}

	public boolean accept(String key) {
		if ("class".equals(key)) {
			return true;
		}
		return false;
	}

}
