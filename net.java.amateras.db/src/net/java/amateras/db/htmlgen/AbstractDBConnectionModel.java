package net.java.amateras.db.htmlgen;


public class AbstractDBConnectionModel {

	private AbstractDBEntityModel source;
	private AbstractDBEntityModel target;

	public void attachSource() {
		if (!source.getModelSourceConnections().contains(this)){
			source.addSourceConnection(this);
		}
	}

	public void attachTarget() {
		if (!target.getModelTargetConnections().contains(this)){
			target.addTargetConnection(this);
		}
	}

	public void detachSource() {
		if(source!=null){
			source.removeSourceConnection(this);
		}
	}

	public void detachTarget() {
		if(target!=null){
			target.removeTargetConnection(this);
		}
	}

	public AbstractDBEntityModel getSource() {
		return source;
	}

	public AbstractDBEntityModel getTarget() {
		return target;
	}

	public void setSource(AbstractDBEntityModel model) {
		source = model;
	}

	public void setTarget(AbstractDBEntityModel model) {
		target = model;
	}

}
