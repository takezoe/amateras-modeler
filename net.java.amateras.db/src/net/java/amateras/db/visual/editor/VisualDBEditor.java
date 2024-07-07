package net.java.amateras.db.visual.editor;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import net.java.amateras.db.DBPlugin;
import net.java.amateras.db.dialect.DialectProvider;
import net.java.amateras.db.validator.DiagramErrors;
import net.java.amateras.db.validator.DiagramErrors.DiagramError;
import net.java.amateras.db.validator.DiagramValidator;
import net.java.amateras.db.visual.action.AutoLayoutAction;
import net.java.amateras.db.visual.action.ChangeDBTypeAction;
import net.java.amateras.db.visual.action.CopyAction;
import net.java.amateras.db.visual.action.CopyAsImageAction;
import net.java.amateras.db.visual.action.DeleteMarkerAction;
import net.java.amateras.db.visual.action.DommainEditAction;
import net.java.amateras.db.visual.action.GenerateAction;
import net.java.amateras.db.visual.action.ImportFromDiagramAction;
import net.java.amateras.db.visual.action.ImportFromJDBCAction;
import net.java.amateras.db.visual.action.Logical2PhysicalAction;
import net.java.amateras.db.visual.action.LowercaseAction;
import net.java.amateras.db.visual.action.PasteAction;
import net.java.amateras.db.visual.action.Physical2LogicalAction;
import net.java.amateras.db.visual.action.QuickOutlineAction;
import net.java.amateras.db.visual.action.RefreshLinkedTablesAction;
import net.java.amateras.db.visual.action.SelectedTablesDDLAction;
import net.java.amateras.db.visual.action.ToggleModelAction;
import net.java.amateras.db.visual.action.UppercaseAction;
import net.java.amateras.db.visual.action.ValidateAction;
import net.java.amateras.db.visual.editpart.DBEditPartFactory;
import net.java.amateras.db.visual.editpart.IDoubleClickSupport;
import net.java.amateras.db.visual.editpart.RootEditPart;
import net.java.amateras.db.visual.generate.GeneratorProvider;
import net.java.amateras.db.visual.generate.IGenerator;
import net.java.amateras.db.visual.model.AnchorModel;
import net.java.amateras.db.visual.model.ForeignKeyModel;
import net.java.amateras.db.visual.model.NoteModel;
import net.java.amateras.db.visual.model.RootModel;
import net.java.amateras.db.visual.model.TableModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.MouseWheelHandler;
import org.eclipse.gef.MouseWheelZoomHandler;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.ConnectionCreationToolEntry;
import org.eclipse.gef.palette.CreationToolEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteDrawer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.DeleteAction;
import org.eclipse.gef.ui.actions.DeleteRetargetAction;
import org.eclipse.gef.ui.actions.GEFActionConstants;
import org.eclipse.gef.ui.actions.PrintAction;
import org.eclipse.gef.ui.actions.RedoRetargetAction;
import org.eclipse.gef.ui.actions.UndoRetargetAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.parts.GraphicalEditorWithPalette;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class VisualDBEditor extends GraphicalEditorWithPalette
	implements IResourceChangeListener, org.eclipse.jface.util.IPropertyChangeListener  {

	private boolean savePreviouslyNeeded = false;
	private VisualDBOutlinePage outlinePage;
	private boolean needViewerRefreshFlag = true;

	public VisualDBEditor(){
		super();
		setEditDomain(new DefaultEditDomain(this));
		getActionRegistry().registerAction(new UndoRetargetAction());
		getActionRegistry().registerAction(new RedoRetargetAction());
		getActionRegistry().registerAction(new DeleteRetargetAction());

		ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
		DBPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(this);
	}



	protected PaletteRoot getPaletteRoot() {
		PaletteRoot root = new PaletteRoot();

		PaletteGroup tools = new PaletteGroup(DBPlugin.getResourceString("palette.tools"));
		tools.add(new SelectionToolEntry());
		tools.add(new MarqueeToolEntry());

		PaletteDrawer drawer = new PaletteDrawer(DBPlugin.getResourceString("palette.db"));
		drawer.add(createEntityEntry(
				DBPlugin.getResourceString("palette.db.table"),TableModel.class,"icons/table.gif"));
		drawer.add(createConnectionEntry(
				DBPlugin.getResourceString("palette.db.reference"),ForeignKeyModel.class,"icons/reference.gif"));
		drawer.add(createEntityEntry(
				DBPlugin.getResourceString("palette.db.note"),NoteModel.class,"icons/note.gif"));
		drawer.add(createConnectionEntry(
				DBPlugin.getResourceString("palette.db.anchor"),AnchorModel.class,"icons/anchor.gif"));

		root.add(tools);
		root.add(drawer);

		return root;
	}

	protected void setInput(IEditorInput input) {
		super.setInput(input);
		setPartName(input.getName());
	}

	protected void initializeGraphicalViewer() {
		GraphicalViewer viewer = getGraphicalViewer();

		ScalableRootEditPart rootEditPart = new ScalableRootEditPart();
		viewer.setEditPartFactory(new DBEditPartFactory());
		viewer.setRootEditPart(rootEditPart);

	    // ZoomManager
	    ZoomManager manager = rootEditPart.getZoomManager();

	    // zoom level
	    double[] zoomLevels = new double[] {
	      0.25,0.5,0.75,1.0,1.5,2.0,2.5,3.0,4.0,5.0,10.0,20.0
	    };
	    manager.setZoomLevels(zoomLevels);

	    // zoom level contribution
	    List<String> zoomContributions = new ArrayList<String>();
	    zoomContributions.add(ZoomManager.FIT_ALL);
	    zoomContributions.add(ZoomManager.FIT_HEIGHT);
	    zoomContributions.add(ZoomManager.FIT_WIDTH);
	    manager.setZoomLevelContributions(zoomContributions);

	    getActionRegistry().registerAction(new ZoomInAction(manager));
	    getActionRegistry().registerAction(new ZoomOutAction(manager));

	    PrintAction printAction = new PrintAction(this);
	    printAction.setText(DBPlugin.getResourceString("action.print"));
	    printAction.setImageDescriptor(DBPlugin.getImageDescriptor("icons/print.gif"));
	    getActionRegistry().registerAction(printAction);

		IFile file = ((IFileEditorInput)getEditorInput()).getFile();

		RootModel root = null;
		// desirialize
		try {
			root = VisualDBSerializer.deserialize(file.getContents());
		} catch(Exception ex){
			DBPlugin.logException(ex);
			root = new RootModel();
			root.setDialectName(DialectProvider.getDialectNames()[0]);
		}
		viewer.setContents(root);

		final DeleteAction deleteAction = new DeleteAction((IWorkbenchPart) this);
		deleteAction.setSelectionProvider(getGraphicalViewer());
		getActionRegistry().registerAction(deleteAction);
		getGraphicalViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				deleteAction.update();
			}
		});

//		// actions
//		ImportFromJDBCAction importAction = new ImportFromJDBCAction(viewer);
//		dbActions.add(importAction);

		// creates context menu
		MenuManager menuMgr = new MenuManager();

        menuMgr.add(new QuickOutlineAction());
        menuMgr.add(new Separator());

		menuMgr.add(getActionRegistry().getAction(ActionFactory.UNDO.getId()));
		menuMgr.add(getActionRegistry().getAction(ActionFactory.REDO.getId()));

        menuMgr.add(new Separator());

        PasteAction pasteAction = new PasteAction(this);
        getActionRegistry().registerAction(pasteAction);
        getSelectionActions().add(pasteAction.getId());
        menuMgr.add(pasteAction);

        CopyAction copyAction = new CopyAction(this, pasteAction);
        getActionRegistry().registerAction(copyAction);
        getSelectionActions().add(copyAction.getId());
        menuMgr.add(copyAction);

		menuMgr.add(getActionRegistry().getAction(ActionFactory.DELETE.getId()));
		menuMgr.add(new Separator());
		menuMgr.add(new AutoLayoutAction(viewer));
		menuMgr.add(new DommainEditAction(viewer));

		// Convert Actions
		MenuManager convertMenu = new MenuManager(DBPlugin.getResourceString("action.convert"));
		menuMgr.add(convertMenu);

		UppercaseAction uppercaseAction = new UppercaseAction(this);
		convertMenu.add(uppercaseAction);
        getActionRegistry().registerAction(uppercaseAction);
        getSelectionActions().add(uppercaseAction.getId());

        LowercaseAction lowercaseAction = new LowercaseAction(this);
		convertMenu.add(lowercaseAction);
        getActionRegistry().registerAction(lowercaseAction);
        getSelectionActions().add(lowercaseAction.getId());

		Physical2LogicalAction physical2logicalAction = new Physical2LogicalAction(this);
		convertMenu.add(physical2logicalAction);
        getActionRegistry().registerAction(physical2logicalAction);
        getSelectionActions().add(physical2logicalAction.getId());

        Logical2PhysicalAction logical2physicalAction = new Logical2PhysicalAction(this);
		convertMenu.add(logical2physicalAction);
        getActionRegistry().registerAction(logical2physicalAction);
        getSelectionActions().add(logical2physicalAction.getId());

//		menuMgr.add(new Separator());
		menuMgr.add(new ToggleModelAction(viewer));
		menuMgr.add(new ChangeDBTypeAction(viewer));
		menuMgr.add(new Separator());
		menuMgr.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_IN));
		menuMgr.add(getActionRegistry().getAction(GEFActionConstants.ZOOM_OUT));
		menuMgr.add(new Separator());
		menuMgr.add(new CopyAsImageAction(viewer));
		menuMgr.add(getActionRegistry().getAction(ActionFactory.PRINT.getId()));
		menuMgr.add(new Separator());

		// Validation Actions
		MenuManager validation = new MenuManager(DBPlugin.getResourceString("action.validation"));
        validation.add(new ValidateAction(viewer));
		validation.add(new DeleteMarkerAction(viewer));
		menuMgr.add(validation);

		// Import Actions
		MenuManager importMenu = new MenuManager(DBPlugin.getResourceString("action.import"));
		importMenu.add(new ImportFromJDBCAction(viewer));
        importMenu.add(new ImportFromDiagramAction(viewer));
        importMenu.add(new RefreshLinkedTablesAction(viewer));
        menuMgr.add(importMenu);

        // Export Actions
		MenuManager generate = new MenuManager(DBPlugin.getResourceString("action.export"));
		IGenerator[] generaters = GeneratorProvider.getGeneraters();
		for(int i=0;i<generaters.length;i++){
			generate.add(new GenerateAction(generaters[i], viewer, this));
		}
		menuMgr.add(generate);
        menuMgr.add(new SelectedTablesDDLAction(viewer));

		viewer.setContextMenu(menuMgr);

		viewer.getControl().addMouseListener(new MouseAdapter(){
			public void mouseDoubleClick(MouseEvent e){
				IStructuredSelection selection = (IStructuredSelection)getGraphicalViewer().getSelection();
				Object obj = selection.getFirstElement();
				if(obj!=null && obj instanceof IDoubleClickSupport){
					((IDoubleClickSupport)obj).doubleClicked();
				}
			}
		});

		outlinePage = new VisualDBOutlinePage(
				viewer, getEditDomain(), root, getSelectionSynchronizer());

		applyPreferences();

		// TODO いまいちすぎるけど取りあえず…
		viewer.getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.stateMask == SWT.CTRL && e.keyCode == 'o'){
					new QuickOutlineAction().run();
				}
				if(e.stateMask == SWT.CTRL && e.keyCode == 'd'){
					new ToggleModelAction(getGraphicalViewer()).run();
				}
			}
		});
	}

	public void doSave(IProgressMonitor monitor) {
		RootModel model = (RootModel)getGraphicalViewer().getContents().getModel();
		IFile file = ((IFileEditorInput)getEditorInput()).getFile();

        // Validate models
        if(DBPlugin.getDefault().getPreferenceStore().getBoolean(DBPlugin.PREF_VALIDATE_ON_SAVE)){
            try {
                file.deleteMarkers(IMarker.PROBLEM, false, 0);
                DiagramErrors errors = new DiagramValidator(model).doValidate();
                for(DiagramError error: errors.getErrors()){
                    error.addMarker(file);
                }
            } catch(CoreException ex){
                DBPlugin.logException(ex);
            }
        }

		// Save editing models using XStream
		try {
			needViewerRefreshFlag = false;
			file.setContents(VisualDBSerializer.serialize(model), true, true, monitor);
		} catch(Exception ex){
			DBPlugin.logException(ex);
			throw new RuntimeException(ex);
		}
		getCommandStack().markSaveLocation();
	}

	public void doSaveAs() {
		doSave(new NullProgressMonitor());
	}

	public boolean isSaveAsAllowed() {
		return true;
	}

	public void commandStackChanged(EventObject event) {
		if (isDirty()) {
			if (!savePreviouslyNeeded()) {
				setSavePreviouslyNeeded(true);
				firePropertyChange(IEditorPart.PROP_DIRTY);
			}
		} else {
			setSavePreviouslyNeeded(false);
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		super.commandStackChanged(event);
	}

	private void setSavePreviouslyNeeded(boolean value) {
		this.savePreviouslyNeeded = value;
	}

	private boolean savePreviouslyNeeded() {
		return this.savePreviouslyNeeded;
	}

	/**
	 * Creates <code>PaletteEntry</code> for the connection.
	 *
	 * @param itemName the display name
	 * @param clazz the model class
	 * @param icon the icon path
	 * @return created <code>PaletteEntry</code>
	 */
	private <T> PaletteEntry createConnectionEntry(String itemName, Class<T> clazz, String icon){
		ImageDescriptor image = null;
		if(icon!=null){
			image = DBPlugin.getImageDescriptor(icon);
		}
		ConnectionCreationToolEntry entry = new ConnectionCreationToolEntry(
				itemName, itemName, new SimpleFactory<T>(clazz), image, image);
		return entry;
	}

	/**
	 * Creates <code>PaletteEntry</code> for the entity.
	 *
	 * @param itemName the display name
	 * @param clazz the model class
	 * @param icon the icon path
	 * @return created <code>PaletteEntry</code>
	 */
	private <T> PaletteEntry createEntityEntry(String itemName,Class<T> clazz,String icon){
		ImageDescriptor image = null;
		if(icon!=null){
			image = DBPlugin.getImageDescriptor(icon);
		}
		CreationToolEntry entry = new CreationToolEntry(
				itemName, itemName, new SimpleFactory<T>(clazz), image, image);

		return entry;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAdapter(Class<T> type){
		if(type == IContentOutlinePage.class){
			return (T) this.outlinePage;
		}
		return super.getAdapter(type);
	}

	private void refreshGraphicalViewer() {
		IEditorInput input = getEditorInput();
		if (input instanceof IFileEditorInput) {
			try {
				IFile file = ((IFileEditorInput) input).getFile();
				GraphicalViewer viewer = getGraphicalViewer();

				// deserialize
				RootModel newRoot = null;
				try {
					newRoot = VisualDBSerializer.deserialize(file.getContents());
				} catch(Exception ex){
					DBPlugin.logException(ex);
					return;
				}

				// copy to editing model
				RootModel root = (RootModel) viewer.getContents().getModel();
				root.copyFrom(newRoot);

			} catch (Exception ex) {
				DBPlugin.logException(ex);
			}
		}
	}

	public void resourceChanged(final IResourceChangeEvent event) {
		if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
			final IEditorInput input = getEditorInput();

			// Do not refresh for changes of markers.
			IMarkerDelta[] deltas = event.findMarkerDeltas(IMarker.PROBLEM, true);
			if(deltas.length > 0){
				return;
			}

			if (input instanceof IFileEditorInput) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						IFile file = ((IFileEditorInput) input).getFile();
						if (!file.exists()) {
							IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							page.closeEditor(VisualDBEditor.this, false);
						} else {
							if (!getPartName().equals(file.getName())) {
								setPartName(file.getName());
							}
							if(needViewerRefreshFlag){
								refreshGraphicalViewer();
							} else {
								needViewerRefreshFlag = true;
							}
						}
					}
				});
			}
		}
	}

	protected void applyPreferences(){
		IPreferenceStore store = DBPlugin.getDefault().getPreferenceStore();

		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
		                store.getBoolean(DBPlugin.PREF_SHOW_GRID));
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
		                store.getBoolean(DBPlugin.PREF_SHOW_GRID));

		int gridSize = store.getInt(DBPlugin.PREF_GRID_SIZE);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
		                new Dimension(gridSize, gridSize));

		getGraphicalViewer().setProperty(SnapToGeometry.PROPERTY_SNAP_ENABLED,
		                store.getBoolean(DBPlugin.PREF_SNAP_GEOMETRY));

		boolean isZoomableWithCtrlAndScroll = store.getBoolean(DBPlugin.PREF_ZOOMABLE_WITH_CTRL_AND_SCROLL);
		String mouseWheelHandlerKey = MouseWheelHandler.KeyGenerator.getKey(SWT.CTRL);
		if (isZoomableWithCtrlAndScroll) {
			getGraphicalViewer().setProperty(mouseWheelHandlerKey,
					MouseWheelZoomHandler.SINGLETON);
		} else {
			getGraphicalViewer().setProperty(mouseWheelHandlerKey, null);
		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		applyPreferences();

		RootEditPart root = (RootEditPart) getGraphicalViewer().getRootEditPart().getContents();
		root.propertyChange(
				new java.beans.PropertyChangeEvent(this, RootModel.P_MODE, null, null));
	}

	@Override
	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
		DBPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(this);
		super.dispose();
	}


}
