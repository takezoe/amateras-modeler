/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure;

import net.java.amateras.uml.figure.CustomBorderLayout;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author shida
 *
 */
public class SystemFigure extends Figure implements PresentationFigure,
		EntityFigure {

	private Label name = null;
	
	private Figure panel = null;
	public SystemFigure() {
		CustomBorderLayout layout = new CustomBorderLayout();
		layout.setStretchCenterHeight(true);
		layout.setStretchCenterWidth(true);
		setLayoutManager(layout);
		setBorder(new LineBorder());
		setOpaque(false);
		name = new Label();
		name.setBorder(new MarginBorder(4));
		panel = new Figure();
		panel.setLayoutManager(new XYLayout());
		panel.setOpaque(false);
		add(name, CustomBorderLayout.TOP);
		add(panel, CustomBorderLayout.CENTER);
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		name.setForegroundColor(model.getForegroundColor());
		panel.setForegroundColor(model.getForegroundColor());
		setBackgroundColor(model.getBackgroundColor());
		setForegroundColor(model.getForegroundColor());
	}

	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.EntityFigure#getLabel()
	 */
	public Label getLabel() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.EntityFigure#getCellEditorRectangle()
	 */
	public Rectangle getCellEditorRectangle() {
		return name.getBounds().getCopy();
	}

	public Figure getPanel() {
		return panel;
	}
}
