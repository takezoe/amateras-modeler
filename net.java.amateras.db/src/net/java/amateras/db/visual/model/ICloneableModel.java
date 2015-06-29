package net.java.amateras.db.visual.model;

/**
 * The interface for copy-able models.
 * 
 * @author Naoki Takezoe
 */
public interface ICloneableModel extends Cloneable {
	
	public ICloneableModel clone();
	
//	public RootModel getParent();
	
}
