package net.java.amateras.uml.activitydiagram.figure;

import net.java.amateras.uml.figure.EntityFigure;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.Rectangle;

public class FlowFigure extends PolylineConnection implements EntityFigure {
	
	private Label label;

	public FlowFigure(){
		this.label = new Label();
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
		setTargetDecoration(new PolygonDecoration());
	}
	
	public Rectangle getCellEditorRectangle() {
		return this.label.getBounds().getCopy();
	}

	public Label getLabel() {
		return this.label;
	}
	

}
