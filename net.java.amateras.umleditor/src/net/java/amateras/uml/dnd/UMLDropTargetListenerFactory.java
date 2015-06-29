/**
 * 
 */
package net.java.amateras.uml.dnd;

import org.eclipse.gef.EditPartViewer;
import org.eclipse.jface.util.TransferDropTargetListener;

/**
 * @author Takahiro Shida.
 *
 */
public abstract class UMLDropTargetListenerFactory {

	/**
	 * 
	 * @return implements of AbstractTransferDropTargetListener.
	 */
	public abstract TransferDropTargetListener getDropTargetListener(EditPartViewer viewer);

	public abstract boolean accept(String key);
}
