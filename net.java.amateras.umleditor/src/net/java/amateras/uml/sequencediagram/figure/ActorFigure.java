/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;

/**
 * @author Takahiro Shida.
 *
 */
public class ActorFigure extends InstanceFigure {

	
	private UnderLineLabel name;

	public ActorFigure() {
		super();
		setBorder(new MarginBorder(2));
	}
	
	public InstanceFigure build() {
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(false);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		setLayoutManager(layout);
		setOpaque(false);

		name = new UnderLineLabel();
		name.setOpaque(false);
		setName(name);
		
		Label image = new Label();
		image.setIcon(UMLPlugin.getImageDescriptor("icons/actor.gif").createImage());
		image.setOpaque(false);
		add(image);
		add(getLabel());
		return this;
	}

	public void setTypeName(String type) {
	}
	
	public void updatePresentation(AbstractUMLModel model) {
	}
}
