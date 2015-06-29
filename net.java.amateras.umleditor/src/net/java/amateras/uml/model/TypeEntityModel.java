package net.java.amateras.uml.model;

public interface TypeEntityModel extends EntityModel {
	
	String P_SIMPLE_ENTITY_NAME = "_simpleEntityName";
	
	void setSimpleName(String simpleName);
	
	String getSimpleName();

}
