package net.java.amateras.uml.activitydiagram.figure;

import net.java.amateras.uml.activitydiagram.model.ActionModel;
import net.java.amateras.uml.figure.EntityFigure;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.RoundedRectangle;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author Naoki Takezoe
 */
public class ActionFigure extends RoundedRectangle implements EntityFigure {
	
	private Label label;
	
	public ActionFigure(){
		setCornerDimensions(new Dimension(20, 20));
		setLayoutManager(new BorderLayout());
		
		label = new Label();
		label.setBorder(new MarginBorder(10));
		
		add(label, BorderLayout.CENTER);		
	}

	public void updatePresentation(ActionModel model) {
		label.setText(model.getActionName());
		label.setBackgroundColor(model.getBackgroundColor());
		label.setForegroundColor(model.getForegroundColor());
		setBackgroundColor(model.getBackgroundColor());
		setForegroundColor(model.getForegroundColor());
	}	
	
	public Rectangle getCellEditorRectangle() {
		return label.getBounds().getCopy();
	}

	public Label getLabel() {
		return label;
	}

}
