/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure;

import net.java.amateras.uml.UMLImageRegistry;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseActorModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * @author Takahiro Shida.
 * 
 */
public class UsecaseActorFigure extends Figure implements EntityFigure,
		PresentationFigure {

	private Label name;

	private Label image;

	public UsecaseActorFigure(UsecaseActorModel model) {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(false);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		setLayoutManager(layout);
		setOpaque(false);

		name = new Label();
		name.setOpaque(true);

		image = new Label();
		if (model.getImagePath() != null) {
			setImage(model);
		} else {
			image.setIcon(UMLPlugin.getImageDescriptor("icons/actor.gif")
					.createImage());
		}
		image.setOpaque(true);
		add(image);
		add(getLabel());
	}

	public void updatePresentation(AbstractUMLModel model) {
		setBackgroundColor(ColorConstants.white);
		name.setBackgroundColor(ColorConstants.white);
	}

	public Label getLabel() {
		return name;
	}

	public Rectangle getCellEditorRectangle() {
		return name.getBounds().getCopy();
	}

	public void setImage(UsecaseActorModel model) {
		if (image != null && model.getImageFile() != null) {
			Image img = UMLImageRegistry.getImage(model.getImageFile()
					.getLocation().toFile().toURI().toString());
			this.image.setIcon(img);
		} else {
			storeDefaultImage();
		}
	}

	public void storeDefaultImage() {
		image.setIcon(UMLPlugin.getImageDescriptor("icons/actor.gif")
				.createImage());
	}
}
