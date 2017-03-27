package net.java.amateras.uml.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

public abstract class AbstractUMLEditPart extends AbstractGraphicalEditPart implements PropertyChangeListener {
	
	@Override
	public void activate() {
		super.activate();
		((AbstractUMLModel) getModel()).addPropertyChangeListener(this);
	}

	@Override
	public void deactivate() {
		super.deactivate();
		((AbstractUMLModel) getModel()).removePropertyChangeListener(this);
	}
	
	/*
	 *  (non-Javadoc)
	 * @see org.eclipse.gef.editparts.AbstractEditPart#refreshVisuals()
	 */
	@Override
	protected void refreshVisuals() {
		if (getFigure() instanceof PresentationFigure) {
			PresentationFigure figure = (PresentationFigure) getFigure();
			figure.updatePresentation((AbstractUMLModel) getModel());
		}
		super.refreshVisuals();
	}
	
	/*
	 *  (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(AbstractUMLModel.P_BACKGROUND_COLOR)) {
			refreshVisuals();
		} else if (evt.getPropertyName().equals(AbstractUMLModel.P_FOREGROUND_COLOR)) {
			refreshVisuals();
		} else if (evt.getPropertyName().equals(AbstractUMLModel.P_SHOW_ICON)) {
			refreshVisuals();
		}
	}
	
	public static Image addDecoratorTopLeft(Image originalIcon, Image decorator) {
		Image decoratedIcon = addDecorator(originalIcon, decorator, 0, 0);
		return decoratedIcon;
	}
	
	public static Image addDecoratorTopRight(Image originalIcon, Image decorator) {
		int xStart = originalIcon.getImageData().width - decorator.getImageData().width;
		Image decoratedIcon = addDecorator(originalIcon, decorator, xStart, 0);
		return decoratedIcon;
	}
	
	private static Image addDecorator(Image originalIcon, Image decorator, int xPos, int yPos) {
		
		ImageData iconData = originalIcon.getImageData();
		ImageData decoratorData = decorator.getImageData();
		
		if ((iconData.width < decoratorData.width) || (iconData.height < decoratorData.height)) {
			return originalIcon;
		}
		
		int[] decoratorPixels = new int[decoratorData.width];
		byte[] decoratorAlphas = new byte[decoratorData.width];
		for (int i = 0 ; i < decoratorData.height ; ++i) {
			decoratorData.getPixels(0, i, decoratorData.width, decoratorPixels, 0);
			decoratorData.getAlphas(0, i, decoratorData.width, decoratorAlphas, 0);
			// If alpha of decorator is opaque the final icon pixel is set opaque
			// The pixel value of decorator is also set
			for (int j = 0 ; j < decoratorData.width ; ++j) {
				if (decoratorAlphas[j] == -1) {
					iconData.setAlpha(xPos + j, yPos + i, 255);
					iconData.setPixel(xPos + j, yPos + i, decoratorPixels[j]);
				}
			}
		}
		
		Image decoratedIcon = new Image(null, iconData);
		return decoratedIcon;
	}
}
