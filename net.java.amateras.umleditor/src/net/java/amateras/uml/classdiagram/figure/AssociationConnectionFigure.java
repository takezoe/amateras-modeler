/**
 * 
 */
package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ConnectionEndpointLocator;
import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;

/**
 * @author Takahiro Shida.
 *
 */
public class AssociationConnectionFigure extends PolylineConnection implements
		PresentationFigure {
	
	private Label labelStereoType;
	private Label labelFromMultiplicity;
	private Label labelToMultiplicity;
	
	public AssociationConnectionFigure(AssociationModel model) {
		labelStereoType = new Label();
		if(!model.getStereoType().equals("")){
			labelStereoType.setText("<<" + model.getStereoType() + ">>");
		}
		
		labelFromMultiplicity = new Label();
		labelFromMultiplicity.setText(model.getFromMultiplicity());
		
		labelToMultiplicity = new Label();
		labelToMultiplicity.setText(model.getToMultiplicity());
		
		add(labelStereoType, new ConnectionLocator(this, ConnectionLocator.MIDDLE));
		
		ConnectionEndpointLocator sourceEndpointLocator = new ConnectionEndpointLocator(this, false);
		sourceEndpointLocator.setVDistance(15);
		add(labelFromMultiplicity, sourceEndpointLocator);
		
		ConnectionEndpointLocator targetEndpointLocator = new ConnectionEndpointLocator(this, true);
		targetEndpointLocator.setVDistance(15);
		add(labelToMultiplicity, targetEndpointLocator);
		
	}
	
	public void update(AssociationModel model) {
		labelFromMultiplicity.setText(model.getFromMultiplicity());
		labelToMultiplicity.setText(model.getToMultiplicity());
		
		if(!model.getStereoType().equals("")){
			labelStereoType.setText("<<" + model.getStereoType() + ">>");
		} else {
			labelStereoType.setText("");
		}		
	}
	
	public Label getStereoTypeLabel() {
		return labelStereoType;
	}
	/* (non-Javadoc)
	 * @see net.java.amateras.uml.figure.PresentationFigure#updatePresentation(net.java.amateras.uml.model.AbstractUMLModel)
	 */
	public void updatePresentation(AbstractUMLModel model) {
		labelStereoType.setForegroundColor(model.getForegroundColor());
		labelFromMultiplicity.setForegroundColor(model.getForegroundColor());
		labelToMultiplicity.setForegroundColor(model.getForegroundColor());
		setForegroundColor(model.getForegroundColor());
	}

}
