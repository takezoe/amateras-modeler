package net.java.amateras.uml.model;

import java.util.ArrayList;
import java.util.List;

import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.editpart.ConnectionBendpoint;

import org.eclipse.ui.views.properties.ColorPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertyDescriptor;

public abstract class AbstractUMLConnectionModel extends AbstractUMLModel {

	private AbstractUMLEntityModel source;

	private AbstractUMLEntityModel target;

	private List<ConnectionBendpoint> bendpoints = new ArrayList<ConnectionBendpoint>();

	public static final String P_BEND_POINT = "_bend_point";

	public void addBendpoint(int index, ConnectionBendpoint point) {
		bendpoints.add(index, point);
		firePropertyChange(P_BEND_POINT, null, null);
	}

//	public void addBendpoint(int index, Point point) {
//		if(point.x < 0){
//			point.x = 0;
//		}
//		if(point.y < 0){
//			point.y = 0;
//		}
//		bendpoints.add(index, point);
//		firePropertyChange(P_BEND_POINT, null, null);
//	}

	public List<ConnectionBendpoint> getBendpoints() {
		if (bendpoints == null) {
			bendpoints = new ArrayList<ConnectionBendpoint>();
		}
		return bendpoints;
	}

	public void removeBendpoint(int index) {
		bendpoints.remove(index);
		firePropertyChange(P_BEND_POINT, null, null);
	}

	public void removeBendpoint(ConnectionBendpoint point) {
		bendpoints.remove(point);
		firePropertyChange(P_BEND_POINT, null, null);
	}

	public void replaceBendpoint(int index, ConnectionBendpoint point) {
		bendpoints.set(index, point);
		firePropertyChange(P_BEND_POINT, null, null);
	}

//	// 既存のベンド・ポイントの置き換え(ベンド・ポイントの移動に使用)
//	public void replaceBendpoint(int index, Point point) {
//		if(point.x < 0){
//			point.x = 0;
//		}
//		if(point.y < 0){
//			point.y = 0;
//		}
//		bendpoints.set(index, point);
//		firePropertyChange(P_BEND_POINT, null, null);
//	}

	// このコネクションの根元をsourceに接続
	public void attachSource() {
		// このコネクションが既に接続されている場合は無視
		if (!source.getModelSourceConnections().contains(this)) {
			source.addSourceConnection(this);
		}
	}

	// このコネクションの先端をtargetに接続
	public void attachTarget() {
		if (!target.getModelTargetConnections().contains(this)) {
			target.addTargetConnection(this);
		}
	}

	// このコネクションの根元をsourceから取り外す
	public void detachSource() {
		if (source != null) {
			source.removeSourceConnection(this);
		}
	}

	// このコネクションの先端をtargetから取り外す
	public void detachTarget() {
		if (target != null) {
			target.removeTargetConnection(this);
		}
	}

	public AbstractUMLEntityModel getSource() {
		return source;
	}

	public AbstractUMLEntityModel getTarget() {
		return target;
	}

	public void setSource(AbstractUMLEntityModel model) {
		source = model;
	}

	public void setTarget(AbstractUMLEntityModel model) {
		target = model;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		return new IPropertyDescriptor[] {
				new ColorPropertyDescriptor(P_FOREGROUND_COLOR, UMLPlugin
						.getDefault().getResourceString("property.foreground"))
				};
	}

}
