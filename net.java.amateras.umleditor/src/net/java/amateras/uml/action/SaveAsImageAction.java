package net.java.amateras.uml.action;

import net.java.amateras.uml.UMLPlugin;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;

public class SaveAsImageAction extends AbstractUMLEditorAction {
	
	public SaveAsImageAction( GraphicalViewer viewer){
		super(UMLPlugin.getDefault().getResourceString("menu.saveAsImage"), viewer);
		setImageDescriptor(UMLPlugin.getImageDescriptor("icons/save_as_image.gif"));
	}
	
	public void update(IStructuredSelection sel){
	}
	
	public void run(){
		
		ScalableRootEditPart rootEditPart = (ScalableRootEditPart)getViewer().getRootEditPart();
		double zoom = rootEditPart.getZoomManager().getZoom();
		
		try {
			FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
			dialog.setFileName(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor().getTitle() + ".jpg");
			String file = dialog.open();
			if(file!=null){
				IFigure figure = rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS);
				
				Rectangle rectangle = figure.getBounds();
				
				Image image = new Image(Display.getDefault(), rectangle.width + 50, rectangle.height + 50);
				GC gc = new GC(image);
				SWTGraphics graphics = new SWTGraphics(gc);
				figure.paint(graphics);
				
				ImageLoader loader = new ImageLoader();
				loader.data = new ImageData[]{image.getImageData()};
				
				if(file.endsWith(".bmp")){
					loader.save(file, SWT.IMAGE_BMP);
				} else if(file.endsWith(".gif")){
					loader.save(file, SWT.IMAGE_GIF);
				} else if(file.endsWith(".jpg") || file.endsWith(".jpeg")){
					loader.save(file, SWT.IMAGE_JPEG);
				} else if(file.endsWith(".png")){
					loader.save(file, SWT.IMAGE_PNG);
				} else if(file.endsWith(".tiff")){
					loader.save(file, SWT.IMAGE_TIFF);
				} else {
					file = file + ".bmp";
					loader.save(file, SWT.IMAGE_BMP);
				}
				
				image.dispose();
				gc.dispose();
			}
		} catch(Exception ex){
			//ex.printStackTrace();
		} finally {
			rootEditPart.getZoomManager().setZoom(zoom);
		}
	}
	
}
