package net.java.amateras.uml.activitydiagram.editpart;

import net.java.amateras.uml.editpart.AbstractUMLEntityEditPart;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class DecisionEditPart extends AbstractUMLEntityEditPart {

	protected IFigure createFigure() {
		return new DecisionFigure();
	}

	private static class DecisionFigure extends Figure {
		
		public DecisionFigure(){
			//setLayoutManager(new XYLayout());
			setSize(30, 20);
		}
		
		public void paintFigure(Graphics graphics) {
			Point loc = getLocation();
			graphics.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
			graphics.fillPolygon(new int[]{
					loc.x +  0, loc.y +  9, loc.x + 14, loc.y +  0,
					loc.x + 15, loc.y +  0, loc.x + 29, loc.y +  9,
					loc.x + 29, loc.y + 10, loc.x + 15, loc.y + 19,
					loc.x + 14, loc.y + 19, loc.x +  0, loc.y + 10
			});
			graphics.setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			graphics.drawLine(loc.x +  0, loc.y +  9, loc.x + 14, loc.y +  0);
			graphics.drawLine(loc.x + 15, loc.y +  0, loc.x + 29, loc.y +  9);
			graphics.drawLine(loc.x + 29, loc.y + 10, loc.x + 15, loc.y + 19);
			graphics.drawLine(loc.x + 14, loc.y + 19, loc.x +  0, loc.y + 10);
		}
	}

}
