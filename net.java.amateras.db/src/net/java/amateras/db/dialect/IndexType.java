package net.java.amateras.db.dialect;

import java.io.Serializable;

public class IndexType implements IIndexType, Serializable {
	private String name;
	
	public IndexType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
