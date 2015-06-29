package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.UMLPlugin;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;

/**
 * アンダーラインつきラベル
 * 
 * @author Naoki Takezoe
 */
public class UnderLineLabel extends Label {

	public UnderLineLabel() {
	}

	public void paint(Graphics graphics) {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
			graphics.setAntialias(SWT.ON);
			graphics.setTextAntialias(SWT.ON);
		}
		super.paint(graphics);
	}
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		Rectangle bounds = getBounds();
		graphics.drawLine(bounds.x, bounds.y + bounds.height - 1, bounds.x
				+ bounds.width, bounds.y + bounds.height - 1);
	}

}
