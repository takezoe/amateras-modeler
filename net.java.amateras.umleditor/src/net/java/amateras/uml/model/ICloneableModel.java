package net.java.amateras.uml.model;

/**
 * The interface for copy-able models.
 * 
 * @author Naoki Takezoe
 * @since 1.2.3
 */
public interface ICloneableModel extends Cloneable {
	
	public Object clone();
	
	public AbstractUMLEntityModel getParent();
	
}
