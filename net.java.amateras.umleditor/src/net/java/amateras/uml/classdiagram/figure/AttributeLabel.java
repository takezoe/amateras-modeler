package net.java.amateras.uml.classdiagram.figure;

import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Image;

/**
 * クラスまたはインターフェース内にアトリビュートを表示するためのラベル。
 * 
 * @author Naoki Takezoe
 */
public class AttributeLabel extends Label implements PresentationFigure {
	
	private boolean underline = false;
	
	private Image icon;
	
	public AttributeLabel(){
		setBorder(new MarginBorder(0,2,0,2));
		setForegroundColor(ColorConstants.darkGray);
	}
	
	public void setIcon(Image image) {
		this.icon = image;
		super.setIcon(image);
	}
	public void setUnderline(boolean underline){
		this.underline = underline;
	}
	
	public boolean isUnderline(){
		return this.underline;
	}
	
	protected void paintFigure(Graphics graphics) {
		super.paintFigure(graphics);
		
		if(underline){
			Rectangle bounds = getBounds();
			int x = getIcon() != null ? 16 : 6;
			graphics.drawLine(
					bounds.x + x, bounds.y+bounds.height - 2,
					bounds.x+bounds.width - 2, bounds.y+bounds.height - 2);
		}
	}

	public void updatePresentation(AbstractUMLModel model) {
		if (model.isShowIcon()) {
			setIcon(icon);
		} else {
			setIcon(null);
		}
//		setBackgroundColor(model.getBackgroundColor());
//		setForegroundColor(model.getForegroundColor());
	}
	
}
