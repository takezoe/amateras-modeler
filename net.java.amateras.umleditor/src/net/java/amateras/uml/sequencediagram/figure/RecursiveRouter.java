/**
 * 
 */
package net.java.amateras.uml.sequencediagram.figure;

import net.java.amateras.uml.sequencediagram.model.ActivationModel;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;

/**
 * 再帰メッセージ用Polyline.
 * @author Takahiro Shida.
 *
 */
public class RecursiveRouter extends AbstractRouter {

	public static final int DELTA_Y = 20;
	
	public static final int DELTA_X = 30;
	
	public void route(Connection connection) {
		//開始点.Y = 終了点.Y - DELTA
		Point end = getEndPoint(connection).getCopy();
		Point start = getStartPoint(connection).getCopy();
		Point up = new Point(0,-DELTA_Y);
		start.y = end.getTranslated(up).y;
		//中継点
		Point right = new Point(DELTA_X, 0);
		Point first = start.getCopy().getTranslated(right);
		Point down = new Point(0,DELTA_Y);
		Point second = first.getCopy().getTranslated(down);
		//終了点
		Point delta = new Point(ActivationModel.DEFAULT_WIDTH, 0);
		end.translate(delta);
		PointList list = new PointList();
		connection.translateToRelative(start);
		connection.translateToRelative(first);
		connection.translateToRelative(second);
		connection.translateToRelative(end);
		list.addPoint(start);
		list.addPoint(first);
		list.addPoint(second);
		list.addPoint(end);
		connection.setPoints(list);
	}
	
}
