package net.java.amateras.db.visual.editor;

import java.util.List;

import net.java.amateras.db.visual.editpart.tree.VisualDBTreeEditPartFactory;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.AbstractInformationControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class VisualDBInformationControl extends AbstractInformationControl {

	private GraphicalViewer graphicalViewer;
	private TreeViewer viewer;
	private Text search;
	private ModelEditor modelEditor;

	public VisualDBInformationControl(Shell parentShell, GraphicalViewer graphicalViewer) {
		super(parentShell, true);
		this.graphicalViewer = graphicalViewer;
		this.modelEditor = new ModelEditor(graphicalViewer, false);

		VisualDBOutlinePage.setFilterText("");

		create();

		int width  = 300;
		int height = 300;

		Point loc  = graphicalViewer.getControl().getParent().toDisplay(0, 0);
		Point size = graphicalViewer.getControl().getParent().getSize();

		int x = (size.x - width)  / 2 + loc.x;
		int y = (size.y - height) / 2 + loc.y;

		setSize(width, height);
		setLocation(new Point(x, y));
		addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				dispose();
			}
		});
	}

	@Override
	protected void createContent(Composite parent) {
		Color foreground = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND);
		Color background = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND);

		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));
		composite.setForeground(foreground);
		composite.setBackground(background);


		search = new Text(composite, SWT.NONE);
		search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		search.setForeground(foreground);
		search.setBackground(background);

		new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL)
			.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Dialog.applyDialogFont(search);

		search.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		search.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				String filterText = search.getText();
				VisualDBOutlinePage.setFilterText(filterText);

				viewer.getRootEditPart().getContents().refresh();
				if(filterText.length() > 0){
					EditPart folder = (EditPart) viewer.
						getRootEditPart().getContents().getChildren().get(0);

					List<?> tables = folder.getChildren();

					if(tables.size() > 0){
						viewer.select((EditPart) tables.get(0));
					}
				}
			}
		});
		search.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR){
					selectAndDispose();
				}
				if(e.keyCode == SWT.ARROW_UP){
					viewer.getControl().setFocus();
				}
				if(e.keyCode == SWT.ARROW_DOWN){
					viewer.getControl().setFocus();
				}
			}
		});

		viewer = new TreeViewer();
		viewer.createControl(composite);
		viewer.setEditDomain(graphicalViewer.getEditDomain());
		viewer.setEditPartFactory(new VisualDBTreeEditPartFactory());
		viewer.setContents(graphicalViewer.getContents().getModel());
		viewer.getControl().setLayoutData(new GridData(GridData.FILL_BOTH));
		viewer.getControl().addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode == SWT.CR){
					selectAndDispose();
				}
			}
		});
		viewer.getControl().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				selectAndDispose();
			}
		});

		viewer.getControl().setForeground(foreground);
		viewer.getControl().setBackground(background);
	}

	private void selectAndDispose(){
		List<?> selected = viewer.getSelectedEditParts();
		dispose();

		if(!selected.isEmpty()){
			EditPart editPart = (EditPart) selected.get(0);
			Object model = editPart.getModel();
			modelEditor.editModel(model);
		}
	}

	@Override
	public void setVisible(boolean visible){
		super.setVisible(visible);
		search.setFocus();
	}

	public boolean hasContents() {
		return true;
	}

}
