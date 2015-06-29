package net.java.amateras.db.htmlgen;

import java.util.ArrayList;
import java.util.List;

public class AbstractDBEntityModel extends AbstractDBModel {

	private List<AbstractDBConnectionModel> sourceConnections = new ArrayList<AbstractDBConnectionModel>();
	private List<AbstractDBConnectionModel> targetConnections = new ArrayList<AbstractDBConnectionModel>();

	public boolean canSource(AbstractDBConnectionModel conn){
		return true;
	}

	public boolean canTarget(AbstractDBConnectionModel conn){
		return true;
	}

	public void addSourceConnection(AbstractDBConnectionModel connx) {
		sourceConnections.add(connx);
	}

	public void addTargetConnection(AbstractDBConnectionModel connx) {
		targetConnections.add(connx);
	}

	public List<AbstractDBConnectionModel> getModelSourceConnections() {
		return sourceConnections;
	}

	public List<AbstractDBConnectionModel> getModelTargetConnections() {
		return targetConnections;
	}

	public void removeSourceConnection(Object connx) {
		sourceConnections.remove(connx);
	}

	public void removeTargetConnection(Object connx) {
		targetConnections.remove(connx);
	}
}
