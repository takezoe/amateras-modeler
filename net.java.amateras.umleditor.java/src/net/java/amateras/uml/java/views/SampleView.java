package net.java.amateras.uml.java.views;


import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.java.amateras.uml.sequencediagram.model.InstanceModel;
import net.java.amateras.uml.sequencediagram.model.SequenceModelBuilder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new
 * workbench view. The view shows data obtained from the
 * model. The sample creates a dummy model on the fly,
 * but a real implementation would connect to the model
 * available either in this or another plug-in (e.g. the workspace).
 * The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model
 * objects should be presented in the view. Each
 * view can present the same model objects using
 * different labels and icons, if needed. Alternatively,
 * a single label provider can be shared between views
 * in order to ensure that objects of the same type are
 * presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

	private Action action1;
	private Text text;

	/*
	 * The content provider class is responsible for
	 * providing objects to the view. It can wrap
	 * existing objects in adapters or simply return
	 * objects as-is. These objects may be sensitive
	 * to the current input of the view, or ignore
	 * it and always show the same content 
	 * (like Task List, for example).
	 */
	 
	class ViewContentProvider implements IStructuredContentProvider {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		public void dispose() {
		}
		public Object[] getElements(Object parent) {
			return new String[] { "One", "Two", "Three" };
		}
	}
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}
		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
					getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	/**
	 * The constructor.
	 */
	public SampleView() {
	}

	/**
	 * This is a callback that will allow us
	 * to create the viewer and initialize it.
	 */
	public void createPartControl(Composite parent) {
		text = new Text(parent, SWT.MULTI | SWT.VERTICAL);
		text.setToolTipText("Paste Stack Trace here and execute action!");
		makeActions();
		contributeToActionBars();
	}


	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(action1);
		manager.add(new Separator());
	}

	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(action1);
	}

	private void makeActions() {
		action1 = new Action() {
			public void run() {
				String content = convertSequenceDiagram();
				if (content != null) {
					ContainerSelectionDialog dialog = new ContainerSelectionDialog(text.getShell(), ResourcesPlugin.getWorkspace().getRoot(), false, "Choose destination of new sequence");
					if (dialog.open() == ContainerSelectionDialog.OK) {
						Object[] objects = dialog.getResult();
						IPath container = (IPath) objects[0];
						IPath path = container.append(new Path("stacktrace.sqd"));
						IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
						try {
							if (file.exists()) {
								file.setContents(new ByteArrayInputStream(
										content.getBytes()), true, true,
										new NullProgressMonitor());
							} else {
								file.create(new ByteArrayInputStream(content
										.getBytes()), true,
										new NullProgressMonitor());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		action1.setText("Generate");
		action1.setToolTipText("Generate Sequence Diagram from Stack Trace.");
		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
		
	}

	private String convertSequenceDiagram() {
		String string = text.getText();
		String[] strings = string.split("\n");
		List<String> stacks = new ArrayList<String>();
		for (int i = strings.length -1; i > 0; i--) {
			if (strings[i].trim().startsWith("at")) {
				stacks.add(strings[i].trim());				
			}
		}	
		SequenceModelBuilder builder = new SequenceModelBuilder();
		builder.init(builder.createActor("User"));
		Map<String, InstanceModel> instances = new HashMap<String, InstanceModel>();
		String prev = null;
		for (Iterator<String> iter = stacks.iterator(); iter.hasNext();) {
			String element = iter.next();
			String name = getClassName(element);
			String method = getMethodName(element);
			if (name == null || method == null) {
				continue;
			}
			if (method.equals("<<init>>")) {
				InstanceModel model = builder.createInstance(name);
				builder.createCreationMessage("create", model);
				instances.put(name, model);
			} else {
				if (name.equals(prev)) {
					builder.createSelfCallMessage(method);
				} else {
					if (instances.get(name) == null) {
						InstanceModel model = builder.createInstance(name);
						builder.createMessage(method, model);
						instances.put(name, model);
					} else {
						InstanceModel model = (InstanceModel) instances.get(name);
						builder.createMessage(method, model);
					}					
				}
			}
			prev = name;
		}
		return builder.toXML();
	}

	private String getMethodName(String line) {
		String fqn = getFQN(line);
		if (fqn == null) {
			return null;
		}
		return fqn.substring(fqn.lastIndexOf('.') + 1);
	}
	
	private String getClassName(String line) {
		String fqn = getFQN(line);
		if (fqn == null) {
			return null;
		}
		String newFqn = fqn.substring(0, fqn.lastIndexOf('.'));
		if (newFqn.lastIndexOf('.') > 0) {
			return newFqn.substring(newFqn.lastIndexOf('.') + 1);			
		}
		return newFqn;
	}
	
	private String getFQN(String line) {
		if (line.indexOf('(') > 0 && line.length() > 4) {
			return line.substring(3, line.lastIndexOf('('));
		}
		return null;
	}
	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
	}
}