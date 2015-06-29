/**
 * 
 */
package net.java.amateras.uml.activitydiagram.figure;

import net.java.amateras.uml.activitydiagram.model.VerticalPartitionModel;
import net.java.amateras.uml.figure.EntityFigure;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;

public class VerticalPartitionFigure extends RectangleFigure implements EntityFigure {
	
	private Label label = new Label();
	
	public VerticalPartitionFigure(){
		setLayoutManager(new XYLayout());
		label.setBorder(new LineBorder(){
			public void paint(IFigure figure, Graphics graphics, Insets insets) {
				tempRect.setBounds(getPaintRectangle(figure, insets));
				if (getWidth() % 2 == 1) {
					tempRect.width--;
					tempRect.height--;
				}
				tempRect.shrink(getWidth() / 2, getWidth() / 2);
				graphics.setLineWidth(getWidth());
				if (getColor() != null)
					graphics.setForegroundColor(getColor());
				graphics.drawLine(tempRect.x, tempRect.y + tempRect.height, 
						tempRect.x + tempRect.width, tempRect.y + tempRect.height);
			}
		});
		
		add(label);
		setOpaque(false);
		setFill(false);
	}
	
	public void updatePresentation(VerticalPartitionModel model){
		this.label.setText(model.getPartitionName());
		setConstraint(this.label, new Rectangle(0, 0, 
				model.getConstraint().width, 20));
	}

	public Rectangle getCellEditorRectangle() {
		return label.getBounds().getCopy();
	}

	public Label getLabel() {
		return this.label;
	}
}