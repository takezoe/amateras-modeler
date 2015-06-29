package net.java.amateras.db.htmlgen;

import net.java.amateras.db.dialect.IColumnType;

/**
 *
 * @author Naoki Takezoe
 */
public class DommainModel implements Cloneable {

	private String id;
	private String name;
	private IColumnType type;
	private String size;

	public String getId(){
		return this.id;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public IColumnType getType() {
		return type;
	}

	public void setType(IColumnType type) {
		this.type = type;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	@Override
	public DommainModel clone() {
		DommainModel model = new DommainModel();
		model.setId(getId());
		model.setName(getName());
		model.setType(getType());
		model.setSize(getSize());
		return model;
	}

	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(" - ");
		sb.append(getType().getName());
		if(getType().supportSize()){
			sb.append("(");
			sb.append(getSize());
			sb.append(")");
		}
		return sb.toString();
	}

}
