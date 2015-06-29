/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.classdiagram.model.DependencyModel;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PolylineDecoration;

/**
 * @author Takahiro Shida.
 *
 */
public class DependencyConnectionFigure extends PolylineConnection implements
		PresentationFigure {

	private Label label;
	
	public DependencyConnectionFigure(DependencyModel model) {
		label = new Label();
		if(!model.getStereoType().equals("")){
			label.setText("<<" + model.getStereoType() + ">>");
		}
		setLineStyle(Graphics.LINE_DASH);
		setTargetDecoration(new PolylineDecoration());
		add(label, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
	}
	
	public void update(DependencyModel model) {
		if(!model.getStereoType().equals("")){
			label.setText("<<" + model.getStereoType() + ">>");
		} else {
			label.setText("");
		}
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		setForegroundColor(model.getForegroundColor());
	}

	public Label getStereoTypeLabel() {
		return label;
	}
}
