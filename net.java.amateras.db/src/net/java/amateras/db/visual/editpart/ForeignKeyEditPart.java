package net.java.amateras.db.visual.editpart;

import net.java.amateras.db.visual.model.AbstractDBConnectionModel;
import net.java.amateras.db.visual.model.ForeignKeyMapping;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.draw2d.ConnectionLocator;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Display;

public class ForeignKeyEditPart extends AbstractDBConnectionEditPart {

	private Label label;
	private ManhattanConnectionRouter router;
	private Font font;

	protected IFigure createFigure() {
		PolylineConnection connection = new PolylineConnection();
		router = new ManhattanConnectionRouter();

		connection.setConnectionRouter(router);

		PolygonDecoration decoration = new PolygonDecoration();
		connection.setTargetDecoration(decoration);

		label = new Label();
		label.setLabelAlignment(PositionConstants.CENTER);
		label.setOpaque(true);
		label.setBackgroundColor(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
		updateConnection(connection);
		connection.add(label, new ConnectionLocator(connection, ConnectionLocator.MIDDLE));

		return connection;
	}

	private void updateConnection(PolylineConnection connection){
		RootModel root = (RootModel) getRoot().getContents().getModel();
		ForeignKeyModel model = (ForeignKeyModel) getModel();
		ForeignKeyMapping[] mapping = model.getMapping();
		StringBuffer sb = new StringBuffer();
		for(int i=0;i<mapping.length;i++){
			try {
				if(i != 0){
					sb.append("\n");
				}
				sb.append(mapping[i].getDisplayString(root.getLogicalMode()));
			} catch(Exception ex){
				ex.printStackTrace();
			}
		}
		label.setText(sb.toString());

		connection.setLineStyle(Graphics.LINE_DASH);
		for(int i=0;i<mapping.length;i++){
			if(mapping[i].getRefer() != null && mapping[i].getRefer().isPrimaryKey()){
				connection.setLineStyle(Graphics.LINE_SOLID);
				break;
			}
		}


		int count = 0;

		for(AbstractDBConnectionModel conn: model.getSource().getModelSourceConnections()){
			if(conn == model){
				break;
			}
			if(conn.getTarget() == model.getTarget()){
				count++;
			}
		}

		router.setDuplicationCount(count);
	}

	protected void refreshVisuals() {
		super.refreshVisuals();

		if(font != null){
			font.dispose();
		}

		RootModel root = (RootModel) getRoot().getContents().getModel();
		FontData[] fontData = root.getFontData();
		font = new Font(Display.getDefault(), fontData);
		figure.setFont(font);

		updateConnection((PolylineConnection) getFigure());
	}

	@Override
	public void deactivate() {
		super.deactivate();
		if(font != null){
			font.dispose();
		}
	}

	public void doubleClicked(){
		ForeignKeyModel model = (ForeignKeyModel)getModel();
		TableModel source = (TableModel) model.getSource();
//		if(source.isLinkedTable()){
//			UIUtils.openAlertDialog(DBPlugin.getResourceString("error.edit.linkedIndex"));
//			return;
//		}

		ForeignKeyEditDialog dialog = new ForeignKeyEditDialog(
				getViewer().getControl().getShell(),
				model.getForeignKeyName(), model.getMapping(), source.getColumns(),
				((RootModel)getRoot().getContents().getModel()).getLogicalMode());

		if(dialog.open()==Dialog.OK){
			model.setForeignKeyName(dialog.getForeignKeyName());
			model.setMapping(dialog.getMapping());
		}
	}



}
