/**
 * 
 */
package net.java.amateras.uml.usecasediagram.figure;

import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;

import org.eclipse.draw2d.BorderLayout;
import org.eclipse.draw2d.Ellipse;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

/**
 * @author Takahiro Shida.
 * 
 */
public class UsecaseFigure extends Ellipse implements EntityFigure,
		PresentationFigure {

	private Label name;

	public UsecaseFigure(UsecaseModel model) {
		name = new Label();
		setLayoutManager(new BorderLayout());
		add(name, BorderLayout.CENTER);
		setLink(model.getResource() != null && model.getFileResource().exists());
	}

	public void updatePresentation(AbstractUMLModel model) {
		name.setBackgroundColor(model.getBackgroundColor());
		name.setForegroundColor(model.getForegroundColor());
		setBackgroundColor(model.getBackgroundColor());
		setForegroundColor(model.getForegroundColor());
	}

	public Label getLabel() {
		return name;
	}

	public Rectangle getCellEditorRectangle() {
		return name.getBounds().getCopy();
	}

	public void setLink(boolean set) {
		if (set) {
			name.setIcon(PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_FILE));
		} else {
			name.setIcon(null);
		}
	}
}
