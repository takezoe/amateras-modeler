package net.java.amateras.uml.usecasediagram;

import net.java.amateras.uml.DiagramEditor;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.model.AnchorModel;
import net.java.amateras.uml.model.NoteModel;
import net.java.amateras.uml.model.RootModel;
import net.java.amateras.uml.usecasediagram.action.CopyAction;
import net.java.amateras.uml.usecasediagram.action.PasteAction;
import net.java.amateras.uml.usecasediagram.edit.UsecaseEditPartFactory;
import net.java.amateras.uml.usecasediagram.model.SystemModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseActorModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseExtendModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseGeneralizationModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseIncludeModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseRelationModel;
import net.java.amateras.uml.usecasediagram.model.UsecaseRootModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;

/**
 * GEFを使用したUML（ユースケースダイアグラム）エディタ。
 * 
 * @author Takahiro Shida.
 */
public class UsecaseDiagramEditor extends DiagramEditor {
	
	private CopyAction copyAction;
	private PasteAction pasteAction;
	
	protected RootModel createInitializeModel() {
		UsecaseRootModel model = new UsecaseRootModel();
		model.setShowIcon(true);
		model.setBackgroundColor(UMLClassFigure.classColor.getRGB());
		model.setForegroundColor(ColorConstants.black.getRGB());
		return model;
	}

	protected String getDiagramType() {
		return "usecase";
	}

	protected EditPartFactory createEditPartFactory() {
		return new UsecaseEditPartFactory();
	}

	protected void createDiagramAction(GraphicalViewer viewer) {
		
	}

	protected void fillDiagramPopupMenu(MenuManager manager) {
		manager.add(new Separator("copy"));
		manager.add(copyAction);
		manager.add(pasteAction);
	}

	protected void updateDiagramAction(ISelection selection) {
		
	}

	protected void createActions() {
		super.createActions();
		pasteAction = new PasteAction(this);
		getActionRegistry().registerAction(pasteAction);
		getSelectionActions().add(pasteAction.getId());
		
		copyAction = new CopyAction(this, pasteAction);
		getActionRegistry().registerAction(copyAction);
		getSelectionActions().add(copyAction.getId());
	}
	
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		UMLPlugin plugin = UMLPlugin.getDefault();

		// モデル作成以外のツールを格納するグループ
		PaletteGroup tools = new PaletteGroup(plugin
				.getResourceString("palette.tool"));
		// '選択' ツールの作成と追加
		ToolEntry tool = new SelectionToolEntry();
		tools.add(tool);
		root.setDefaultEntry(tool);
		// '囲み枠' ツールの作成と追加
		tool = new MarqueeToolEntry();
		tools.add(tool);

		PaletteDrawer common = new PaletteDrawer(plugin
				.getResourceString("palette.common"));
		common.add(createEntityEntry(plugin
				.getResourceString("palette.common.note"), NoteModel.class,
				"icons/note.gif"));
		common.add(createConnectionEntry(plugin
				.getResourceString("palette.common.anchor"), AnchorModel.class,
				"icons/anchor.gif"));

		// モデルの作成を行うツールを格納するグループ
		PaletteDrawer entities = new PaletteDrawer(plugin
				.getResourceString("palette.entity"));
		entities.add(createEntityEntry(plugin
				.getResourceString("palette.usecase.actor"), UsecaseActorModel.class,
				"icons/actor16.gif"));
		entities.add(createEntityEntry(plugin
				.getResourceString("palette.usecase.usecase"),
				UsecaseModel.class, "icons/usecase.gif"));

		entities.add(createEntityEntry(plugin
				.getResourceString("palette.usecase.system"),
				SystemModel.class, "icons/system.gif"));

		PaletteDrawer relations = new PaletteDrawer(plugin
				.getResourceString("palette.relation"));
		relations.add(createConnectionEntry(plugin
				.getResourceString("palette.usecase.relation"),
				UsecaseRelationModel.class, "icons/relation.gif"));
		relations.add(createConnectionEntry(plugin
				.getResourceString("palette.usecase.generalization"),
				UsecaseGeneralizationModel.class, "icons/generalization.gif"));
		relations.add(createConnectionEntry(plugin
				.getResourceString("palette.usecase.include"),
				UsecaseIncludeModel.class, "icons/dependency.gif"));
		relations.add(createConnectionEntry(plugin
				.getResourceString("palette.usecase.extend"),
				UsecaseExtendModel.class, "icons/dependency.gif"));

		// 作成した3つのグループをルートに追加
		root.add(tools);
		root.add(common);
		root.add(entities);
		root.add(relations);

		return root;
	}
	


}
