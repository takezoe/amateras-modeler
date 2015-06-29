package net.java.amateras.uml.action;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;

public abstract class AbstractUMLEditorAction extends Action {
	
	private GraphicalViewer viewer;
	
	public AbstractUMLEditorAction(String name, GraphicalViewer viewer){
		super(name);
		this.viewer = viewer;
	}
	
	public AbstractUMLEditorAction(String name, int style, GraphicalViewer viewer){
		super(name, style);
		this.viewer = viewer;
	}
	protected GraphicalViewer getViewer(){
		return viewer;
	}
	
	public abstract void update(IStructuredSelection sel);
	
}
