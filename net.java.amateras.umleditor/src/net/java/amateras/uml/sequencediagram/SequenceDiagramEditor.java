package net.java.amateras.uml.sequencediagram;

import net.java.amateras.uml.DiagramEditor;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.model.NoteModel;
import net.java.amateras.uml.model.RootModel;
import net.java.amateras.uml.sequencediagram.action.AddReturnMessageAction;
import net.java.amateras.uml.sequencediagram.action.ImportClassModelAction;
import net.java.amateras.uml.sequencediagram.editpart.SequenceEditPartFactory;
import net.java.amateras.uml.sequencediagram.figure.InstanceFigure;
import net.java.amateras.uml.sequencediagram.model.ActorModel;
import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.InteractionModel;
import net.java.amateras.uml.sequencediagram.model.SyncMessageModel;

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
import org.eclipse.jface.viewers.IStructuredSelection;

/**
 * GEFを使用したUML（シーケンスダイアグラム）エディタ。
 * 
 * @author Takahiro Shida.
 */
public class SequenceDiagramEditor extends DiagramEditor {
	
	private AddReturnMessageAction returnMessageAction;
	private ImportClassModelAction importClassModelAction;
	
	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();
		UMLPlugin plugin = UMLPlugin.getDefault();
		
		// モデル作成以外のツールを格納するグループ
		PaletteGroup tools = new PaletteGroup(plugin.getResourceString("palette.tool"));
		// '選択' ツールの作成と追加
		ToolEntry tool = new SelectionToolEntry();
		tools.add(tool);
		root.setDefaultEntry(tool);
		// '囲み枠' ツールの作成と追加
		tool = new MarqueeToolEntry();
		tools.add(tool);
		
		PaletteDrawer common = new PaletteDrawer(plugin.getResourceString("palette.common"));
		common.add(createEntityEntry(plugin.getResourceString("palette.common.note"),
				NoteModel.class,"icons/note.gif"));
//		common.add(createConnectionEntry(plugin.getResourceString("palette.common.anchor"),
//				AnchorModel.class,"icons/dependency.gif"));
		
		// モデルの作成を行うツールを格納するグループ
		PaletteDrawer entities = new PaletteDrawer(plugin.getResourceString("palette.entity"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.instance"),
				InstanceModel.class,"icons/class.gif"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.actor"),
				ActorModel.class,"icons/actor16.gif"));

		PaletteDrawer relations = new PaletteDrawer(plugin.getResourceString("palette.message"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.message"),
				SyncMessageModel.class,"icons/dependency.gif"));
		
		// 作成した2つのグループをルートに追加
		root.add(tools);
		root.add(common);
		root.add(entities);
		root.add(relations);
		return root;
	}


	protected RootModel createInitializeModel() {
		InteractionModel model = new InteractionModel();
		model.setBackgroundColor(InstanceFigure.INSTANCE_COLOR.getRGB());
		model.setForegroundColor(ColorConstants.black.getRGB());
		model.setShowIcon(true);
		return model;
	}

	protected String getDiagramType() {
		return "sequence";
	}

	protected void createDiagramAction(GraphicalViewer viewer) {
		returnMessageAction = new AddReturnMessageAction(getCommandStack(), viewer);
		importClassModelAction = new ImportClassModelAction(getCommandStack(), viewer);
	}

	protected void fillDiagramPopupMenu(MenuManager manager) {
		manager.add(new Separator("generate"));
		manager.add(returnMessageAction);
		manager.add(importClassModelAction);
	}

	protected void updateDiagramAction(ISelection selection) {
		returnMessageAction.update((IStructuredSelection)selection);
		importClassModelAction.update((IStructuredSelection)selection);
	}

	protected EditPartFactory createEditPartFactory() {
		return new SequenceEditPartFactory();
	}
	

}
