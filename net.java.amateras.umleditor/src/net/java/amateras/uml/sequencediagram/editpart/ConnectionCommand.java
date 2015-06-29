package net.java.amateras.uml.sequencediagram.editpart;

import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.sequencediagram.model.MessageModel;

import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.commands.Command;

public class ConnectionCommand extends Command {

	private Point point;
	
	private MessageModel model;
	
	private AbstractUMLEntityModel source;
	
	public void setSource(AbstractUMLEntityModel source) {
		this.source = source;
	}
	
	public AbstractUMLEntityModel getSource() {
		return source;
	}
	
	public void setPoint(Point point) {
		this.point = point;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public void setModel(MessageModel model) {
		this.model = model;
	}
	
	public MessageModel getModel() {
		return model;
	}
	
}
