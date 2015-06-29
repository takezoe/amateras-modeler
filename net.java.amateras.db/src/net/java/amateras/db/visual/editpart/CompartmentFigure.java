package net.java.amateras.db.visual.editpart;

import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.ToolbarLayout;

public class CompartmentFigure extends Figure {
	
	public CompartmentFigure() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
		layout.setStretchMinorAxis(false);
		layout.setSpacing(2);
		setLayoutManager(layout);
	}

}
