package net.java.amateras.db.visual.generate;

import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.gef.GraphicalViewer;

public interface IGenerator {

	public String getGeneratorName();

	public void execute(IFile erdFile, RootModel root, GraphicalViewer viewer);

}
