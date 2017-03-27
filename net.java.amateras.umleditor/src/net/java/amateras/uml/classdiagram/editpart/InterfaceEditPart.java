package net.java.amateras.uml.classdiagram.editpart;

import org.eclipse.draw2d.IFigure;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import net.java.amateras.uml.classdiagram.figure.ClassFigureFactory;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;

public class InterfaceEditPart extends CommonEntityEditPart {
	
	@Override
	protected IFigure createFigure() {
		UMLClassFigure figure = (UMLClassFigure) super.createFigure();

		Font font = ((AbstractGraphicalEditPart) getParent()).getFigure().getFont();
		FontData fontData = font.getFontData()[0];
		Font normal = new Font(null, fontData.getName(), fontData.getHeight(), SWT.NULL|SWT.BOLD);

		figure.setFont(normal);

		return figure;
	}

	@Override
	public UMLClassFigure getClassFigure() {
		return ClassFigureFactory.getInterfaceFigure();
	}
}
