package net.java.amateras.db.visual.editor;

import java.util.List;

import net.java.amateras.db.visual.editpart.tree.AbstractDBTreeEditPart;
import net.java.amateras.db.visual.editpart.tree.VisualDBTreeEditPartFactory;
import net.java.amateras.db.visual.model.RootModel;

import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.EditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.ScalableRootEditPart;
import org.eclipse.gef.ui.parts.ContentOutlinePage;
import org.eclipse.gef.ui.parts.SelectionSynchronizer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

/**
 * The outline page of the {@link VisualDBEditor}.
 *
 * @author Naoki Takezoe
 */
public class VisualDBOutlinePage extends ContentOutlinePage {

	private GraphicalViewer graphicalViewer;
	private EditDomain editDomain;
	private RootModel rootModel;
	private SelectionSynchronizer selectionSynchronizer;

	private Composite composite;
	private SashForm sashForm;
	private DisposeListener disposeListener;
	private ScrollableThumbnail thumbnail;
	private Text search;

	private ModelEditor modelEditor;

	private static String filterText = "";

	public VisualDBOutlinePage(GraphicalViewer viewer, EditDomain domain, RootModel root,
			SelectionSynchronizer selectionSynchronizer) {
		super(new TreeViewer());
		this.graphicalViewer = viewer;
		this.editDomain = domain;
		this.rootModel = root;
		this.selectionSynchronizer = selectionSynchronizer;
		this.modelEditor = new ModelEditor(graphicalViewer, true);
	}

	/**
	 * Returns the incremental search text.
	 *
	 * @return the incremental search text
	 */
	public static String getFilterText(){
		return filterText;
	}

	public static void setFilterText(String filterText){
		VisualDBOutlinePage.filterText = filterText;
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		search = new Text(composite, SWT.BORDER);
		search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		search.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				filterText = search.getText();
				getViewer().getRootEditPart().getContents().refresh();
				if(filterText.length() > 0){
					EditPart folder = (EditPart) getViewer().
						getRootEditPart().getContents().getChildren().get(0);

					List<?> tables = folder.getChildren();

					if(tables.size() > 0){
						getViewer().select((EditPart) tables.get(0));
					}
				}
			}
		});

		sashForm = new SashForm(composite, SWT.VERTICAL);
		sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));

		EditPartViewer viewer = getViewer();
		viewer.createControl(sashForm);
		viewer.setEditDomain(editDomain);
		viewer.setEditPartFactory(new VisualDBTreeEditPartFactory());
		viewer.setContents(rootModel);
		selectionSynchronizer.addViewer(viewer);
		viewer.getControl().addMouseListener(new MouseAdapter(){
			@Override public void mouseDoubleClick(MouseEvent e) {
				IStructuredSelection sel = (IStructuredSelection) getViewer().getSelection();
				Object obj = sel.getFirstElement();
				if(obj != null){
					AbstractDBTreeEditPart editPart = (AbstractDBTreeEditPart) obj;
					Object model = editPart.getModel();
					modelEditor.editModel(model);
				}
			}
		});

		Canvas canvas = new Canvas(sashForm, SWT.BORDER);

		LightweightSystem lws = new LightweightSystem(canvas);

		ScalableRootEditPart rootEditPart
			= (ScalableRootEditPart) graphicalViewer.getRootEditPart();
		thumbnail = new ScrollableThumbnail((Viewport) (rootEditPart).getFigure());
		thumbnail.setSource(rootEditPart.getLayer(LayerConstants.PRINTABLE_LAYERS));
		lws.setContents(thumbnail);

		disposeListener = new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				if (thumbnail != null) {
					thumbnail.deactivate();
					thumbnail = null;
				}
			}
		};
		graphicalViewer.getControl().addDisposeListener(disposeListener);
		getSite().setSelectionProvider(getViewer());
	}

	public Control getControl() {
		return composite;
	}

	public void dispose() {
		selectionSynchronizer.removeViewer(getViewer());
		if ((graphicalViewer.getControl() != null) && !graphicalViewer.getControl().isDisposed()) {
			graphicalViewer.getControl().removeDisposeListener(disposeListener);
		}
		super.dispose();
	}

}
