package net.java.amateras.db.visual.action;

import net.java.amateras.db.DBPlugin;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.ImageTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 *
 * @author Naoki Takezoe
 */
public class CopyAsImageAction extends Action {

	private GraphicalViewer viewer;

	public CopyAsImageAction( GraphicalViewer viewer){
		super(DBPlugin.getResourceString("action.copyAsImage"));
		this.viewer = viewer;
	}

	public void update(IStructuredSelection sel){
	}

	public void run(){

		ScalableRootEditPart rootEditPart = (ScalableRootEditPart) viewer.getRootEditPart();
		double zoom = rootEditPart.getZoomManager().getZoom();

		try {
			IFigure figure = rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS);

			Rectangle rectangle = figure.getBounds();

			Image image = new Image(Display.getDefault(), rectangle.width + 50, rectangle.height + 50);
			GC gc = new GC(image);
			SWTGraphics graphics = new SWTGraphics(gc);
			figure.paint(graphics);

			Clipboard clipboard = new Clipboard(Display.getDefault());
			clipboard.setContents(new Object[]{ image.getImageData() }, new Transfer[]{ ImageTransfer.getInstance() });

			image.dispose();
			gc.dispose();

		} catch(Exception ex){
			//ex.printStackTrace();
		} finally {
			rootEditPart.getZoomManager().setZoom(zoom);
		}
	}

}
