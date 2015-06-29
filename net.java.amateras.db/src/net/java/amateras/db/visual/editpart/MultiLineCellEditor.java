/**
 * 
 */
package net.java.amateras.db.visual.editpart;

import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * This class has been ported from AmaterasUML.
 * 
 * @author Takahiro Shida
 * @since 1.0.6
 */
public class MultiLineCellEditor extends TextCellEditor {

	public MultiLineCellEditor(Composite composite) {
		super(composite, SWT.MULTI | SWT.V_SCROLL);
	}

}
