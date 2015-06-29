package net.java.amateras.uml.activitydiagram.figure;

import net.java.amateras.uml.activitydiagram.model.ObjectModel;
import net.java.amateras.uml.figure.EntityFigure;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.RectangleFigure;
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * 
 * @author Naoki Takezoe
 */
public class ObjectFigure extends RectangleFigure implements EntityFigure {
	
	private Label objectName = new Label();
	private Label stereoType = new Label();
	private Label objectState = new Label();
	
	public ObjectFigure(){
		setLayoutManager(new BorderLayout());
		add(stereoType, BorderLayout.TOP);
		add(objectName, BorderLayout.CENTER);
		add(objectState, BorderLayout.BOTTOM);
		add(createSpacer(), BorderLayout.LEFT);
		add(createSpacer(), BorderLayout.RIGHT);
	}
	
	private static Figure createSpacer(){
		Figure figure = new Figure();
		figure.setSize(10,0);
		return figure;
	}
	
	public void updatePresentation(ObjectModel model){
		if(model.getStereoType().length()==0){
			stereoType.setText("");
		} else {
			stereoType.setText("<<" + model.getStereoType() + ">>");
		}
		
		objectName.setText(model.getObjectName());
		
		if(model.getObjectState().length()==0){
			objectState.setText("");
		} else {
			objectState.setText("[" + model.getObjectState() + "]");
		}
		
		objectName.setBackgroundColor(model.getBackgroundColor());
		objectName.setForegroundColor(model.getForegroundColor());
		objectState.setBackgroundColor(model.getBackgroundColor());
		objectState.setForegroundColor(model.getForegroundColor());
		stereoType.setBackgroundColor(model.getBackgroundColor());
		stereoType.setForegroundColor(model.getForegroundColor());
		setBackgroundColor(model.getBackgroundColor());
		setForegroundColor(model.getForegroundColor());
	}
	
	public Rectangle getCellEditorRectangle() {
		return objectName.getBounds().getCopy();
	}

	public Label getLabel() {
		return objectName;
	}

}
