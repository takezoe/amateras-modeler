/**
 * 
 */
package net.java.amateras.db.visual.editpart;



import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.CompoundBorder;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.text.BlockFlow;
import org.eclipse.draw2d.text.FlowPage;
import org.eclipse.draw2d.text.ParagraphTextLayout;
import org.eclipse.draw2d.text.TextFlow;

/**
 * This class has been ported from AmaterasUML.
 * 
 * @author Takahiro Shida
 * @since 1.0.6
 */
public class NoteFigure extends Figure {

	TextFlow nameFlow;
		
	public NoteFigure() {
		super();
		setBorder(new CompoundBorder(new LineBorder() {
			
			public void paint(IFigure figure, Graphics graphics, Insets insets) {
				tempRect.setBounds(getPaintRectangle(figure, insets));
				if (getWidth() % 2 == 1) {
					tempRect.width--;
					tempRect.height--;
				}
				tempRect.shrink(getWidth() / 2, getWidth() / 2);
				graphics.setLineWidth(getWidth());

				if (getColor() != null)
					graphics.setForegroundColor(getColor());
				
				PointList list = new PointList();
				list.addPoint(tempRect.x , tempRect.y);
				list.addPoint(tempRect.x + tempRect.width - 2, tempRect.y);
				list.addPoint(tempRect.x + tempRect.width - 2, tempRect.y + tempRect.height - 12);
				list.addPoint(tempRect.x + tempRect.width - 12, tempRect.y + tempRect.height - 2);
				list.addPoint(tempRect.x, tempRect.y + tempRect.height - 2);
				graphics.drawPolygon(list);
			}
		},new MarginBorder(3)));
        ToolbarLayout layout = new ToolbarLayout();
        setLayoutManager(layout);
        
        FlowPage page = new FlowPage();
        page.setForegroundColor(ColorConstants.black);
        setBackgroundColor(ColorConstants.white);
        BlockFlow block = new BlockFlow();
        nameFlow = new TextFlow();
        nameFlow.setLayoutManager(new ParagraphTextLayout(nameFlow,ParagraphTextLayout.WORD_WRAP_SOFT));
        block.add(nameFlow);
        page.add(block);
        setOpaque(true);
        add(page);
	}		
	
	protected void paintFigure(Graphics graphics) {
		org.eclipse.draw2d.geometry.Rectangle tempRect = getBounds().getCopy();
		PointList list = new PointList();
		graphics.setBackgroundColor(ColorConstants.gray);
		list.addPoint(tempRect.x , tempRect.y);
		list.addPoint(tempRect.x + tempRect.width, tempRect.y + 2);
		list.addPoint(tempRect.x + tempRect.width, tempRect.y + tempRect.height - 12);
		list.addPoint(tempRect.x + tempRect.width - 12, tempRect.y + tempRect.height);
		list.addPoint(tempRect.x + 2, tempRect.y + tempRect.height);
		graphics.fillPolygon(list);
		list.removeAllPoints();
		list.addPoint(tempRect.x , tempRect.y);
		list.addPoint(tempRect.x + tempRect.width - 3, tempRect.y);
		list.addPoint(tempRect.x + tempRect.width - 3, tempRect.y + tempRect.height - 13);
		list.addPoint(tempRect.x + tempRect.width - 13, tempRect.y + tempRect.height - 3);
		list.addPoint(tempRect.x, tempRect.y + tempRect.height - 3);
		graphics.setBackgroundColor(ColorConstants.white);
		graphics.fillPolygon(list);		
	}
	
	public void setText(String text) {
		nameFlow.setText(text);
	}

}
