/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;

/**
 * @author Takahiro Shida.
 *
 */
public class InstanceFigure extends Figure implements EntityFigure , PresentationFigure {

	public static final Color INSTANCE_COLOR = new Color(null, 255, 255, 206);

	private UnderLineLabel name = null;
	
	private Label type = null;
	
	public InstanceFigure() {
		super();
		setBorder(new CompoundBorder(new LineBorder(ColorConstants.black, 1), new MarginBorder(2)));
	}

	public InstanceFigure build() {
		this.name = new UnderLineLabel();
		this.type = new Label();
		this.name.setBorder(new MarginBorder(4));
		ToolbarLayout layout = new ToolbarLayout();
		layout.setStretchMinorAxis(false);
		layout.setSpacing(2);
		layout.setMinorAlignment(ToolbarLayout.ALIGN_CENTER);
		setLayoutManager(layout);
		setBackgroundColor(INSTANCE_COLOR);
		setOpaque(true);
		
		add(this.name);
		add(this.type);
		return this;
	}
	
	protected void setName(UnderLineLabel name) {
		this.name = name;
	}
	
	public void setInstanceName(String instanceName) {
		this.name.setText(instanceName);
	}

	public void setTypeName(String type) {
		this.type.setText(type);
		if (!"".equals(type)) {
			this.type.setIcon(UMLPlugin.getImageDescriptor("icons/class.gif").createImage());
		} else {
			this.type.setIcon(null);
		}
	}
	public Label getLabel() {
		return this.name;
	}
	
	public Rectangle getCellEditorRectangle() {
		Rectangle rect = name.getBounds().getCopy();
		if (rect.width > 100) {
			return rect;
		} else {
			return new Rectangle(rect.x, rect.y, 100, rect.height);
		}
	}

	public void updatePresentation(AbstractUMLModel model) {
		if (this.type != null) {
			if (model.isShowIcon() && !"".equals(type.getText())) {
				this.type.setIcon(UMLPlugin.getImageDescriptor("icons/class.gif").createImage());
			} else {
				this.type.setIcon(null);
			}			
			type.setBackgroundColor(model.getBackgroundColor());
			type.setForegroundColor(model.getForegroundColor());
		}
		name.setBackgroundColor(model.getBackgroundColor());
		name.setForegroundColor(model.getForegroundColor());
		setBackgroundColor(model.getBackgroundColor());
		setForegroundColor(model.getForegroundColor());		
	}
	
	public void paint(Graphics graphics) {
		if (UMLPlugin.getDefault().getPreferenceStore().getBoolean(UMLPlugin.PREF_ANTI_ALIAS)) {
			graphics.setAntialias(SWT.ON);
			graphics.setTextAntialias(SWT.ON);
		}
		super.paint(graphics);
	}
}
