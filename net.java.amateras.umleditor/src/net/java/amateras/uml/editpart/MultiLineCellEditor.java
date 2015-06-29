/**
 * 
 */
package net.java.amateras.uml.editpart;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Takahiro Shida.
 *
 */
public class MultiLineCellEditor extends TextCellEditor {

	public MultiLineCellEditor(Composite composite) {
		super(composite, SWT.MULTI | SWT.V_SCROLL);
	}

}
