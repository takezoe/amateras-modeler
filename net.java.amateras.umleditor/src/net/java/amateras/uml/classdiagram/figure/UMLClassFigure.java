package net.java.amateras.uml.classdiagram.figure;

import java.util.List;

import net.java.amateras.uml.figure.EntityFigure;
import net.java.amateras.uml.figure.PresentationFigure;
import net.java.amateras.uml.model.AbstractUMLModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.ToolbarLayout;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;

public class UMLClassFigure extends Figure implements EntityFigure, PresentationFigure {

	public static Color classColor = new Color(null, 255, 255, 206);

	private Label name;

	private Label stereoType;

	private Image icon;

	private CompartmentFigure attributeFigure = new CompartmentFigure();
	private CompartmentFigure methodFigure = new CompartmentFigure();
	private Figure nameFigure;

	public UMLClassFigure(Image icon, Figure nameFigure) {
		this.nameFigure = nameFigure;
		this.name = new Label();
		this.name.setForegroundColor(ColorConstants.darkGray);
		this.name.setBorder(new MarginBorder(5));
		this.name.setIcon(icon);
		this.icon = icon;
		this.stereoType = new Label();
		this.stereoType.setTextAlignment(PositionConstants.CENTER);
		ToolbarLayout layout = new ToolbarLayout();
		setLayoutManager(layout);
		setBorder(new LineBorder(1));
		setBackgroundColor(classColor);
		setOpaque(true);

		nameFigure.setLayoutManager(new ToolbarLayout());
//
//		nameFigure.add(this.stereoType);
		nameFigure.add(this.name);
		add(nameFigure);
		add(attributeFigure);
		add(methodFigure);
	}

	public void setFont(Font font){
		this.name.setFont(font);
	}

	public void setClassName(String className){
		this.name.setText(className);
	}

	public void add(IFigure figure, Object constraint, int index) {
		if(figure instanceof AttributeLabel){
			attributeFigure.add(figure);
		} else if(figure instanceof OperationLabel){
			methodFigure.add(figure);
		} else {
			super.add(figure,constraint,index);
		}
	}

	public void moveAttribute(int index, boolean up){
		@SuppressWarnings("rawtypes")
		List children = attributeFigure.getChildren();
		IFigure obj = (IFigure)children.get(index);
		attributeFigure.remove(obj);
		if(up){
			attributeFigure.add(obj, index - 1);
		} else {
			attributeFigure.add(obj, index + 1);
		}
	}

	public void moveOperation(int index, boolean up){
		@SuppressWarnings("rawtypes")
		List children = methodFigure.getChildren();
		IFigure obj = (IFigure)children.get(index);
		methodFigure.remove(obj);
		if(up){
			methodFigure.add(obj, index - 1);
		} else {
			methodFigure.add(obj, index + 1);
		}
	}

	public void remove(IFigure figure) {
		if(figure instanceof  AttributeLabel){
			attributeFigure.remove(figure);
		} else if(figure instanceof OperationLabel){
			methodFigure.remove(figure);
		} else {
			super.remove(figure);
		}
	}

	public Label getLabel(){
		return name;
	}

	public Rectangle getCellEditorRectangle() {
		Rectangle rect = name.getBounds().getCopy();
		if (name.getIcon() != null) {
			return new Rectangle(rect.x + 16, rect.y, rect.width - 16, rect.height);
		}
		return new Rectangle(rect.x, rect.y, rect.width, rect.height);
	}

	public void updatePresentation(AbstractUMLModel model) {
		if (model.isShowIcon()) {
			name.setIcon(icon);
		} else {
			name.setIcon(null);
		}
//		setBackgroundColor(model.getBackgroundColor());
//		setForegroundColor(model.getForegroundColor());
	}

	public void setStereoType(String stereoType) {
		if (stereoType == null || "".equals(stereoType)) {
			if (nameFigure.getChildren().contains(this.stereoType)) {
				nameFigure.remove(this.stereoType);
			}
		} else {
			this.stereoType.setText("<<" + stereoType + ">>");
			if (!nameFigure.getChildren().contains(this.stereoType)) {
				nameFigure.add(this.stereoType, 0);
			}
		}
	}

//	public void setMethods(List methods){
//		methodFigure.removeAll();
//		for(int i=0;i<methods.size();i++){
//			Label label = new Label();
//			label.setText((String)methods.get(i));
//			label.setIcon(UMLPlugin.getImageDescriptor("icons/method.gif").createImage());
//			methodFigure.add(label);
//		}
//	}
//
//	public void setAttributes(List attributes){
//		attributeFigure.removeAll();
//		for(int i=0;i<attributes.size();i++){
//			Label label = new Label();
//			label.setText((String)attributes.get(i));
//			label.setIcon(UMLPlugin.getImageDescriptor("icons/field.gif").createImage());
//			attributeFigure.add(label);
//		}
//	}
}
