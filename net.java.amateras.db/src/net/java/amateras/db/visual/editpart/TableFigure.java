package net.java.amateras.db.visual.editpart;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.validator.DiagramErrors;

import org.eclipse.draw2d.AbstractBorder;
import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

public class TableFigure extends Figure {

//	private static Color COLOR_BACKGROUND = new Color(Display.getDefault(), 255, 255, 206);
//	private static Color COLOR_LINKED = new Color(Display.getDefault(), 230, 230, 230);

	private Label name;
	private ColumnLayoutFigure columnFigure;
	private CompartmentFigure columnNameFigure;
	private CompartmentFigure columnTypeFigure;
	private CompartmentFigure notNullFigure;
	private int flag = 0;

	public TableFigure(){
		this.name = new Label();
		this.name.setBorder(new MarginBorder(2, 2, 0, 2));

		this.columnNameFigure = new CompartmentFigure();
		this.columnTypeFigure = new CompartmentFigure();
		this.notNullFigure = new CompartmentFigure();
		this.columnFigure = new ColumnLayoutFigure();

		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
//		setBackgroundColor(new Color(Display.getDefault(), 255, 255, 206));
		setBorder(new LineBorder(ColorConstants.black, 1));
		setOpaque(true);

		add(this.name);
		add(this.columnFigure);

		this.columnFigure.add(columnNameFigure);
		this.columnFigure.add(columnTypeFigure);
		this.columnFigure.add(notNullFigure);
	}

	public void setTableName(String tableName){
		this.name.setText(tableName);
	}

	public void setErrorMessage(String message){
		if(message == null || message.length()==0){
			this.name.setIcon(null);
			this.name.setToolTip(null);
		} else {
			if(message.indexOf(DiagramErrors.ERROR_PREFIX) >= 0){
				this.name.setIcon(DBPlugin.getImage(DBPlugin.ICON_ERROR));
			} else {
				this.name.setIcon(DBPlugin.getImage(DBPlugin.ICON_WARNING));
			}
			this.name.setToolTip(new Label(message));
		}
	}

	public void setLinkedTable(boolean linked){
		if(linked){
			setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
			((LineBorder) getBorder()).setColor(Display.getDefault().getSystemColor(SWT.COLOR_GRAY));
		} else {
			setForegroundColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
			((LineBorder) getBorder()).setColor(Display.getDefault().getSystemColor(SWT.COLOR_BLACK));
		}
	}

	public void add(IFigure figure, Object constraint, int index) {
		if(figure instanceof ColumnFigure){
			if(flag == 0){
				columnNameFigure.add(figure);
				flag = 1;
			} else if(flag == 1){
				columnTypeFigure.add(figure);
				flag = 2;
			} else {
				notNullFigure.add(figure);
				flag = 0;
			}
		} else {
			super.add(figure, constraint, index);
		}
	}

	public void remove(IFigure figure) {
		if(figure instanceof  ColumnFigure){
			columnNameFigure.remove(figure);
			columnTypeFigure.remove(figure);
			notNullFigure.remove(figure);
		} else {
			super.remove(figure);
		}
	}

	public void removeAllColumns(){
		columnNameFigure.removeAll();
		columnTypeFigure.removeAll();
		notNullFigure.removeAll();
	}

	public Label getLabel(){
		return name;
	}

	private class ColumnLayoutFigure extends Figure {
		public ColumnLayoutFigure(){
			ToolbarLayout layout = new ToolbarLayout(true);
			layout.setMinorAlignment(ToolbarLayout.ALIGN_TOPLEFT);
			layout.setStretchMinorAxis(false);
			layout.setSpacing(2);
			setLayoutManager(layout);
			setBorder(new CompartmentFigureBorder());
		}
	}

	public class CompartmentFigureBorder extends AbstractBorder {
		public Insets getInsets(IFigure figure) {
			return new Insets(1, 0, 2, 0);
		}
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			graphics.drawLine(getPaintRectangle(figure, insets).getTopLeft(), tempRect.getTopRight());
		}
	}
}
