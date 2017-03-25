package net.java.amateras.uml.classdiagram;

import net.java.amateras.uml.DiagramEditor;
import net.java.amateras.uml.UMLPlugin;
import net.java.amateras.uml.action.AbstractUMLEditorAction;
import net.java.amateras.uml.classdiagram.action.AddAttributeAction;
import net.java.amateras.uml.classdiagram.action.AddOperationAction;
import net.java.amateras.uml.classdiagram.action.AutoLayoutAction;
import net.java.amateras.uml.classdiagram.action.CopyAction;
import net.java.amateras.uml.classdiagram.action.DownAction;
import net.java.amateras.uml.classdiagram.action.PasteAction;
import net.java.amateras.uml.classdiagram.action.ShowAllAction;
import net.java.amateras.uml.classdiagram.action.ShowPublicAction;
import net.java.amateras.uml.classdiagram.action.ToggleAction;
import net.java.amateras.uml.classdiagram.action.UpAction;
import net.java.amateras.uml.classdiagram.editpart.UMLEditPartFactory;
import net.java.amateras.uml.classdiagram.figure.UMLClassFigure;
import net.java.amateras.uml.classdiagram.model.AggregationModel;
import net.java.amateras.uml.classdiagram.model.AssociationModel;
import net.java.amateras.uml.classdiagram.model.ClassModel;
import net.java.amateras.uml.classdiagram.model.CompositeModel;
import net.java.amateras.uml.classdiagram.model.DependencyModel;
import net.java.amateras.uml.classdiagram.model.EnumModel;
import net.java.amateras.uml.classdiagram.model.GeneralizationModel;
import net.java.amateras.uml.classdiagram.model.InterfaceModel;
import net.java.amateras.uml.classdiagram.model.RealizationModel;
import net.java.amateras.uml.classdiagram.model.Visibility;
import net.java.amateras.uml.model.AnchorModel;
import net.java.amateras.uml.model.NoteModel;
import net.java.amateras.uml.model.RootModel;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.gef.EditPartFactory;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.ui.actions.AlignmentAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPart;

/**
 * GEFを使用したUML（クラスダイアグラム）エディタ。
 * 
 * @author Naoki Takezoe
 */
public class ClassDiagramEditor extends DiagramEditor {
	
	private AbstractUMLEditorAction addAttributeAction = null;
	private AbstractUMLEditorAction addOperationAction = null;
	private UpAction upAction = null;
	private DownAction downAction = null;
	private CopyAction copyAction = null;
	private AutoLayoutAction autoLayoutAction = null;
	
	private PasteAction pasteAction = null;
	private ShowPublicAction showPublicAction = null;
	private ShowAllAction showAllAction = null;
	private ToggleAction togglePublicAttr = null;
	private ToggleAction toggleProtectedAttr = null;
	private ToggleAction togglePackageAttr = null;
	private ToggleAction togglePrivateAttr = null;
	private ToggleAction togglePublicOpe = null;
	private ToggleAction toggleProtectedOpe = null;
	private ToggleAction togglePackageOpe = null;
	private ToggleAction togglePrivateOpe = null;

	private AlignmentAction top;
	private AlignmentAction midlle;
	private AlignmentAction bottom;
	private AlignmentAction left;
	private AlignmentAction center;
	private AlignmentAction right;

//	public ClassDiagramEditor() {
//		super();
//	}

	@Override
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

		PaletteDrawer common = new PaletteDrawer(plugin.getResourceString("palette.common"));
		common.add(createEntityEntry(plugin.getResourceString("palette.common.note"), NoteModel.class,
				"icons/note.gif"));
		common.add(createConnectionEntry(plugin.getResourceString("palette.common.anchor"), AnchorModel.class,
				"icons/anchor.gif"));

		// モデルの作成を行うツールを格納するグループ
		PaletteDrawer entities = new PaletteDrawer(plugin.getResourceString("palette.entity"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.class"), ClassModel.class,
				"icons/class.gif"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.enum"), EnumModel.class,
				"icons/enum.gif"));
		entities.add(createEntityEntry(plugin.getResourceString("palette.entity.interface"),
				InterfaceModel.class, "icons/interface.gif"));

		PaletteDrawer relations = new PaletteDrawer(plugin.getResourceString("palette.relation"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.dependency"),
				DependencyModel.class, "icons/dependency.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.association"),
				AssociationModel.class, "icons/association.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.generalization"),
				GeneralizationModel.class, "icons/generalization.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.realization"),
				RealizationModel.class, "icons/realization.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.aggregation"),
				AggregationModel.class, "icons/aggregation.gif"));
		relations.add(createConnectionEntry(plugin.getResourceString("palette.relation.composition"),
				CompositeModel.class, "icons/composition.gif"));

		// 作成した3つのグループをルートに追加
		root.add(tools);
		root.add(common);
		root.add(entities);
		root.add(relations);

		return root;
	}

	@Override
	protected RootModel createInitializeModel() {
		RootModel model = new RootModel();
		model.setShowIcon(true);
		model.setBackgroundColor(UMLClassFigure.classColor.getRGB());
		model.setForegroundColor(ColorConstants.black.getRGB());
		return model;
	}

	@Override
	protected String getDiagramType() {
		return "class";
	}
	
	@Override
	protected void createActions() {
		super.createActions();
		pasteAction = new PasteAction(this);
		getActionRegistry().registerAction(pasteAction);
		getSelectionActions().add(pasteAction.getId());
		
		copyAction = new CopyAction(this, pasteAction);
		getActionRegistry().registerAction(copyAction);
		getSelectionActions().add(copyAction.getId());
	}

	@Override
	protected void createDiagramAction(GraphicalViewer viewer) {
		addAttributeAction = new AddAttributeAction(viewer.getEditDomain().getCommandStack(), viewer);
		addOperationAction = new AddOperationAction(viewer.getEditDomain().getCommandStack(), viewer);
		upAction = new UpAction(viewer.getEditDomain().getCommandStack(), viewer);
		downAction = new DownAction(viewer.getEditDomain().getCommandStack(), viewer);
		autoLayoutAction = new AutoLayoutAction(viewer);
		
		showPublicAction = new ShowPublicAction(viewer);
		showAllAction = new ShowAllAction(viewer);
		
		togglePublicAttr = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.attr.public"), viewer, 
				ToggleAction.ATTRIBUTE, Visibility.PUBLIC);
		toggleProtectedAttr = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.attr.protected"), viewer,
				ToggleAction.ATTRIBUTE, Visibility.PROTECTED);
		togglePackageAttr = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.attr.package"), viewer,
				ToggleAction.ATTRIBUTE, Visibility.PACKAGE);
		togglePrivateAttr = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.attr.private"), viewer,
				ToggleAction.ATTRIBUTE, Visibility.PRIVATE);
		togglePublicOpe = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.ope.public"), viewer,
				ToggleAction.OPERATION, Visibility.PUBLIC);
		toggleProtectedOpe = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.ope.protected"), viewer,
				ToggleAction.OPERATION, Visibility.PROTECTED);
		togglePackageOpe = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.ope.package"), viewer,
				ToggleAction.OPERATION, Visibility.PACKAGE);
		togglePrivateOpe = new ToggleAction(
				UMLPlugin.getDefault().getResourceString("filter.ope.private"), viewer,
				ToggleAction.OPERATION, Visibility.PRIVATE);
	}

	@Override
	protected void fillDiagramPopupMenu(MenuManager manager) {
		// TODO use ContextMenuProvider.
		manager.add(new Separator("align"));
		manager.add(autoLayoutAction);
		top = new AlignmentAction((IWorkbenchPart) this, PositionConstants.TOP);
		top.setSelectionProvider(getGraphicalViewer());
		midlle = new AlignmentAction((IWorkbenchPart) this, PositionConstants.MIDDLE);
		midlle.setSelectionProvider(getGraphicalViewer());
		bottom = new AlignmentAction((IWorkbenchPart) this, PositionConstants.BOTTOM);
		bottom.setSelectionProvider(getGraphicalViewer());
		left = new AlignmentAction((IWorkbenchPart) this, PositionConstants.LEFT);
		left.setSelectionProvider(getGraphicalViewer());
		center = new AlignmentAction((IWorkbenchPart) this, PositionConstants.CENTER);
		center.setSelectionProvider(getGraphicalViewer());
		right = new AlignmentAction((IWorkbenchPart) this, PositionConstants.RIGHT);
		right.setSelectionProvider(getGraphicalViewer());
		getActionRegistry().registerAction(top);
		getActionRegistry().registerAction(midlle);
		getActionRegistry().registerAction(bottom);
		getActionRegistry().registerAction(left);
		getActionRegistry().registerAction(center);
		getActionRegistry().registerAction(right);
		MenuManager alignmenu = new MenuManager(UMLPlugin.getDefault().getResourceString("menu.align"));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_TOP));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_MIDDLE));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_BOTTOM));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_LEFT));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_CENTER));
		alignmenu.add(getActionRegistry().getAction(GEFActionConstants.ALIGN_RIGHT));
		manager.add(alignmenu);
		
		MenuManager filtermenu = new MenuManager(UMLPlugin.getDefault().getResourceString("menu.filter"));
		filtermenu.add(showPublicAction);
		filtermenu.add(showAllAction);
		filtermenu.add(new Separator());
		filtermenu.add(togglePublicAttr);
		filtermenu.add(toggleProtectedAttr);
		filtermenu.add(togglePackageAttr);
		filtermenu.add(togglePrivateAttr);
		filtermenu.add(new Separator());
		filtermenu.add(togglePublicOpe);
		filtermenu.add(toggleProtectedOpe);
		filtermenu.add(togglePackageOpe);
		filtermenu.add(togglePrivateOpe);
		manager.add(filtermenu);
		
		manager.add(new Separator("add"));
		manager.add(addAttributeAction);
		manager.add(addOperationAction);
		manager.add(upAction);
		manager.add(downAction);
		
		manager.add(new Separator("copy"));
		manager.add(copyAction);
		manager.add(pasteAction);
	}

	@Override
	protected void updateDiagramAction(ISelection selection) {
		addAttributeAction.update((IStructuredSelection) selection);
		addOperationAction.update((IStructuredSelection) selection);
		upAction.update((IStructuredSelection) selection);
		downAction.update((IStructuredSelection) selection);
//		copyAction.update((IStructuredSelection) selection);
//		pasteAction.update((IStructuredSelection) selection);
		autoLayoutAction.update((IStructuredSelection) selection);
		top.update();
		midlle.update();
		bottom.update();
		left.update();
		center.update();
		right.update();
		showPublicAction.update((IStructuredSelection) selection);
		showAllAction.update((IStructuredSelection) selection);
		togglePackageAttr.update((IStructuredSelection) selection);
		togglePackageOpe.update((IStructuredSelection) selection);
		togglePrivateAttr.update((IStructuredSelection) selection);
		togglePrivateOpe.update((IStructuredSelection) selection);
		toggleProtectedAttr.update((IStructuredSelection) selection);
		toggleProtectedOpe.update((IStructuredSelection) selection);
		togglePublicAttr.update((IStructuredSelection) selection);
		togglePublicOpe.update((IStructuredSelection) selection);
	}

	@Override
	protected EditPartFactory createEditPartFactory() {
		return new UMLEditPartFactory();
	}

}
