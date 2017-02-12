package net.java.amateras.uml.classdiagram.editpart;

import net.java.amateras.uml.classdiagram.figure.ClassFigureFactory;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.classdiagram.model.ClassModel;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class ClassEditPart extends CommonEntityEditPart {

	private Font normal = null;

	private Font italic = null;

	@Override
	protected IFigure createFigure() {
		UMLClassFigure figure = (UMLClassFigure) super.createFigure();

		//Set abstract class name to italic style
		ClassModel model = (ClassModel) getModel();

		Font font = ((AbstractGraphicalEditPart) getParent()).getFigure().getFont();
		FontData fontData = font.getFontData()[0];
		this.normal = new Font(null, fontData.getName(), fontData.getHeight(), SWT.NULL|SWT.BOLD);
		this.italic = new Font(null, fontData.getName(), fontData.getHeight(), SWT.ITALIC|SWT.BOLD);

		if (model.isAbstract()) {
			figure.setFont(italic);
		} else {
			figure.setFont(normal);
		}

		return figure;
	}

	@Override
	public void deactivate() {
		super.deactivate();
		normal.dispose();
		italic.dispose();
	}

	@Override
	protected void refreshVisuals() {
		super.refreshVisuals();
		UMLClassFigure figure = (UMLClassFigure) getFigure();
		ClassModel model = (ClassModel) getModel();

		if (model.isAbstract()) {
			figure.setFont(italic);
		} else {
			figure.setFont(normal);
		}
	}

	@Override
	public UMLClassFigure getClassFigure() {
		ClassModel model = (ClassModel) getModel();
		return ClassFigureFactory.getClassFigure(model.isAbstract());
	}
}
