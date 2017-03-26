/**
 * 
 */
package net.java.amateras.uml.synchronizer;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

import net.java.amateras.uml.DiagramSerializer;
import net.java.amateras.uml.action.AsyncSyncAction;
import net.java.amateras.uml.classdiagram.ClassDiagramEditor;
import net.java.amateras.uml.classdiagram.model.CommonEntityModel;
import net.java.amateras.uml.model.AbstractUMLEntityModel;
import net.java.amateras.uml.model.AbstractUMLModel;
import net.java.amateras.uml.model.RootModel;

/**
 * Do automatic synchronization between class diagram and Java src files.
 * 
 * This class is watching for file modification events within the current workspace. It also keeps
 * track of every class diagram where some class have been produced from an existing java file. As
 * needed it automatically updates a class diagram when the originated java files evolve.
 * 
 * @author jdelarbre
 *
 */
public class JavaClassSynchronizer {
	
	private IResourceChangeListener listener;
	private List<IFile> cldList; // class diagram list
	private Map<String, List<IFile>> javaSrcSynchro; // Java file <=> class diagram list
	
	// Memorize active editor before synchronize => allow to reselect it after all operations have ended
	private IEditorPart activeEditorBeforeSynchronization;
	// Updating a class diagram needs to have the class diagram editor opened. If it is not opened, we opened it
	// and also close it after
	private List<IEditorPart> editorsOpenedDuringSynchronization = new ArrayList<IEditorPart>();

	public JavaClassSynchronizer() {}
	
	public class EventRunnable implements Runnable {
		
		private final IResourceChangeEvent event;
		
		public EventRunnable (IResourceChangeEvent event) {
			this.event = event;
		}
		
		@Override
		public void run() {
			registerActiveEditor();
			editorsOpenedDuringSynchronization.clear();
			IResourceDelta rootDelta = event.getDelta();
			
			updateClassDiagramChanges(rootDelta);
			
			List<String> moveFromList = new ArrayList<String>();
			List<IPath>  moveToList = new ArrayList<IPath>();
			//Search if a delta occurs among the files for which we keep track
			for (String synchronizedJavaSrcPath : javaSrcSynchro.keySet()) {
				IPath iJavaSrcPath = new Path(synchronizedJavaSrcPath);
				// get the delta if any
				IResourceDelta javaSrcDelta = rootDelta.findMember(iJavaSrcPath);
				if (javaSrcDelta == null) {
					continue; // No changes for current tracked src file
				}
				
				// Loop on each cld (class diagram) file where the java class (synchronizedJavaSrcPath) exist
				for (IFile cldFile : javaSrcSynchro.get(synchronizedJavaSrcPath)) {
					if (cldFile.exists() == false){
						continue;
					}
					int deltaKind = javaSrcDelta.getKind();
					int deltaFlags = javaSrcDelta.getFlags();
					switch (deltaKind) {
					case IResourceDelta.CHANGED:
						switch (deltaFlags) {
						case (IResourceDelta.CONTENT | IResourceDelta.MARKERS):
						case IResourceDelta.CONTENT:
							//A java src file has been modified => update corresponding class in class diagram
							updateClassDiagOnJavaSrcChanges(cldFile, synchronizedJavaSrcPath);
							break;
						}
						break;
					case IResourceDelta.REMOVED:
						switch (deltaFlags) {
						case (IResourceDelta.MOVED_TO | IResourceDelta.MARKERS):
						case IResourceDelta.MOVED_TO:
							IPath movedToPath = javaSrcDelta.getMovedToPath();
							updateClassDiagOnJavaSrcMove(cldFile, synchronizedJavaSrcPath, movedToPath);
							moveFromList.add(synchronizedJavaSrcPath);
							moveToList.add(movedToPath);
							break;
						default:
							//A java src file has been removed => remove corresponding class from class diagram
							updateClassDiagOnJavaSrcDelete(cldFile, synchronizedJavaSrcPath);
							break;
						}
						break;
					case IResourceDelta.ADDED:
						//Normally could not happened, watch only Java src files referred by existing UML diagram
						switch (deltaFlags) {
						case IResourceDelta.MOVED_FROM:
							//file renamed or moved
							break;
						}
						break;
					}
				}
			}
			Iterator<String> iteratorFrom = moveFromList.iterator();
			Iterator<IPath> iteratorTo = moveToList.iterator();
			while (iteratorFrom.hasNext() && iteratorTo.hasNext()) {
				updateJavaSynchroAfterMove(iteratorFrom.next(), iteratorTo.next());
			}
		}
	}
	
	public class ResourceChangeListener implements IResourceChangeListener {
		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			if (event.getType() != IResourceChangeEvent.POST_CHANGE) {
				return; // Keep only post change event
			}
			
			Display.getDefault().asyncExec(new EventRunnable(event));
		}
	}
	
	/**
	 * @param rootDelta
	 */
	private void updateClassDiagramChanges(IResourceDelta rootDelta) {
		List<IResourceDelta> addedClassDiag = searchClassDiagram(rootDelta, IResourceDelta.ADDED);
		registerAddedClassDiag(addedClassDiag);
		List<IResourceDelta> removedClassDiag = searchClassDiagram(rootDelta, IResourceDelta.REMOVED);
		unregisterRemovedClassDiag(removedClassDiag);
		List<IResourceDelta> modifiedClassDiag = searchClassDiagram(rootDelta, IResourceDelta.CHANGED);
		updateClassDiagRegistration(modifiedClassDiag);
	}
	
	private void updateClassDiagOnJavaSrcMove(IFile cldFile, String javaFromPath, IPath javaToPath) {
		ClassDiagramEditor classDiagEditor = getClassDiagramEditor(cldFile);
		
		if (classDiagEditor != null) {
			RootModel rootModel = classDiagEditor.getRootModel();
			
			List<AbstractUMLEntityModel> targetClasses = extractClassDiagElmtForModif(javaFromPath, rootModel);
			
			AsyncMoveAction asyncSyncAction = new AsyncMoveAction(targetClasses, javaToPath);
			classDiagEditor.appendAsyncAction(asyncSyncAction);
			//Request to save on disk
			classDiagEditor.need2Serialize(activeEditorBeforeSynchronization, editorsOpenedDuringSynchronization);
		}
	}

	private void updateJavaSynchroAfterMove(String javaFromPath, IPath javaToPath) {
		List<IFile> listCldFile = javaSrcSynchro.get(javaFromPath);
		javaSrcSynchro.remove(javaFromPath);
		javaSrcSynchro.put(javaToPath.toString(), listCldFile);
	}
	
	/**
	 * @param synchronizedJavaSrcPath	Java src file which has been deleted
	 */
	private void updateClassDiagOnJavaSrcDelete(IFile cldFile, String synchronizedJavaSrcPath) {
		ClassDiagramEditor classDiagEditor = getClassDiagramEditor(cldFile);
		
		if (classDiagEditor != null) {
			RootModel rootModel = classDiagEditor.getRootModel();
			
			List<AbstractUMLEntityModel> targetClasses = extractClassDiagElmtForModif(synchronizedJavaSrcPath, rootModel);
			
			AsyncSyncAction asyncSyncAction = new AsyncDeleteAction(targetClasses, classDiagEditor);
			classDiagEditor.appendAsyncAction(asyncSyncAction);
			//Request to save on disk
			classDiagEditor.need2Serialize(activeEditorBeforeSynchronization, editorsOpenedDuringSynchronization);
		}
	}

	/**
	 * @param synchronizedJavaSrcPath	Java src file that caused the need for changes
	 * @param rootModel root of the model element present in class diagram
	 * @return 		elements in root model that have to be modified (multiple elements possible
	 * 				in case of nested class since we take elements from only one java src file:
	 * 				synchronizedJavaSrcPath
	 */
	private List<AbstractUMLEntityModel> extractClassDiagElmtForModif(String synchronizedJavaSrcPath, RootModel rootModel) {
		//Create list of target (java class) to update in class diagram
		List<AbstractUMLEntityModel> targetClasses = new ArrayList<AbstractUMLEntityModel>();
		List<AbstractUMLModel> childrenClassDiag = rootModel.getChildren();
		for (AbstractUMLModel childClass : childrenClassDiag) {
			if (childClass instanceof CommonEntityModel) {
				String javaPathOfClassDiagElmt = ((CommonEntityModel)childClass).getPath();
				if (javaPathOfClassDiagElmt.compareTo(synchronizedJavaSrcPath) == 0) {
					targetClasses.add((AbstractUMLEntityModel)childClass);
				}
			}
		}
		return targetClasses;
	}
	
	private void registerActiveEditor() {
		activeEditorBeforeSynchronization = null;
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page;
		if (workbenchWindow == null) {
			IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
			if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0) {
				workbenchWindow = workbenchWindows[0];
			}
		}
		page = workbenchWindow.getActivePage();
		
		IEditorPart activeEditor = page.getActiveEditor();
		if (activeEditor == null) {
			return;
		}
//		if (activeEditor instanceof JavaEditor) {
		if (	activeEditor.getClass().getSimpleName().equals("CompilationUnitEditor")
			||	activeEditor.getClass().getSimpleName().equals("JavaEditor")) {
			activeEditorBeforeSynchronization = activeEditor;
		}
	}
	
	private ClassDiagramEditor getClassDiagramEditor(IFile cldFile) {
		//Get editor of class diagram where "synchronizedJavaSrcPath" has been modified
		IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page;
		// getActiveWorkbenchWindow() could returned null, if main eclipse window is not active
		// It could happened if a popup window (to confirm file deletion) has been raised and keep the focus
		if (workbenchWindow == null) {
			IWorkbenchWindow[] workbenchWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
			// You can have a second workbench window in eclipse by clicking:
			// menu "Window" -- "New Window"
			// so getWorkbenchWindowCount() is increased by 1
			if (PlatformUI.getWorkbench().getWorkbenchWindowCount() > 0) {
				workbenchWindow = workbenchWindows[0];
			}
			else {
				return null;	// KO
			}
		}
		page = workbenchWindow.getActivePage();
		
		IEditorInput editorInput = new FileEditorInput(cldFile);
		IEditorPart editorPart = page.findEditor(editorInput);
		
		if (editorPart == null) {
			// The class diagram editor is not already opened
			try {
				IEditorDescriptor cldEditDesc = PlatformUI.getWorkbench().
						getEditorRegistry().getDefaultEditor(cldFile.getName());
				editorPart = page.openEditor(editorInput, cldEditDesc.getId(), false);
				if (activeEditorBeforeSynchronization != null) {
					page.activate(activeEditorBeforeSynchronization);
				}
				editorsOpenedDuringSynchronization.add(editorPart);
			}
			catch (PartInitException e) {
				e.printStackTrace();
				System.err.println("Can not open class diagram editor: " + cldFile.getName());
			}
		}
		ClassDiagramEditor classDiagEditor = null;
		if ((editorPart != null) && (editorPart instanceof ClassDiagramEditor)) {
			classDiagEditor = (ClassDiagramEditor)editorPart;
		}
		return classDiagEditor;
	}
	
	/**
	 * @param synchronizedJavaSrcPath	Java src file which has been modified
	 */
	private void updateClassDiagOnJavaSrcChanges(IFile cldFile, String synchronizedJavaSrcPath) {
		ClassDiagramEditor classDiagEditor = getClassDiagramEditor(cldFile);
		
		if (classDiagEditor != null) {
			RootModel rootModel = classDiagEditor.getRootModel();
//				try {
//					root = DiagramSerializer.deserialize(file.getContents());
//					DiagramEditor.validateModel(root);
//				}
//				catch(Exception ex){
//					UMLPlugin.logException(ex);
//				}
			
			List<AbstractUMLEntityModel> targetClasses = extractClassDiagElmtForModif(synchronizedJavaSrcPath, rootModel);
			
			AsyncSyncAction asyncSyncAction = new AsyncUpdateAction(targetClasses, classDiagEditor);
			classDiagEditor.appendAsyncAction(asyncSyncAction);
			//Request to save on disk
			classDiagEditor.need2Serialize(activeEditorBeforeSynchronization, editorsOpenedDuringSynchronization);
		}
	}
	
	/**
	 * Build and initialize JavaClassSynchronizer
	 */
	public static JavaClassSynchronizer buildJCSynchro() {
		JavaClassSynchronizer jCSynchro = new JavaClassSynchronizer();
		IResourceChangeListener listener = jCSynchro.new ResourceChangeListener();
		
		jCSynchro.init(listener);
		
		return jCSynchro;
	}
	
	private void init(IResourceChangeListener listener) {
		searchElmt2Synchronize();
		
		this.listener = listener;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(this.listener);
	}
	
	/**
	 * Stop synchronizer and free listener registered in workspace
	 */
	public void stop() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.removeResourceChangeListener(listener);
	}
	
	/**
	 * Search if changes have occurred on some class diagram files, on changes into workspace
	 * 
	 * @param rootDelta list root of changes in workspace
	 * @param deltaKind	ADDED or REMOVED choice
	 * @return	class diagram list selection according to deltaKind
	 */
	private List<IResourceDelta> searchClassDiagram(IResourceDelta rootDelta, int deltaKind) {
		List<IResourceDelta> classDiagList = new ArrayList<IResourceDelta>();
		
		for (IResourceDelta resDelta : rootDelta.getAffectedChildren()) {
			if (resDelta.getAffectedChildren().length > 0) {
				List<IResourceDelta> newElmts = searchClassDiagram(resDelta, deltaKind);
				classDiagList.addAll(newElmts);
			}
			else {
				if (resDelta.getFullPath().toString().endsWith(".cld")) {
					if (resDelta.getKind() == deltaKind) {
						classDiagList.add(resDelta);
					}
				}
			}
		}
		return classDiagList;
	}
	
	/**
	 * Search associated java files related to the new added class diagram and add them
	 * to the list of java files to watch {@link javaSrcSynchro}
	 * 
	 * @param addedClassDiagList
	 */
	private void registerAddedClassDiag(List<IResourceDelta> addedClassDiagList) {
		List<IFile> cldListTmp = convertDelta2IFile(addedClassDiagList);
		
		extractMergeAllRelatedJavaFiles(javaSrcSynchro, cldListTmp);
		cldList.addAll(cldListTmp);
	}

	private static List<IFile> convertDelta2IFile(List<IResourceDelta> classDiagList) {
		List<IFile> cldListTmp = new ArrayList<IFile>();
		
		//Transform list of IResourceDelta in IFile list
		for (IResourceDelta addedClassDiag : classDiagList) {
			IResource resource = addedClassDiag.getResource();
			if (resource instanceof IFile) {
				IFile iFile = (IFile)resource;
				if (iFile.getFileExtension().compareToIgnoreCase("cld") == 0) {
					cldListTmp.add(iFile);
				}
				else {
					//Should not happened
				}
			}
			else {
				//Should not happened
			}
		}
		return cldListTmp;
	}
	
	/**
	 * Search associated java files related to the removed class diagram and remove them
	 * to the list of java files to watch {@link javaSrcSynchro}
	 * 
	 * @param removedClassDiagList
	 */
	private void unregisterRemovedClassDiag(List<IResourceDelta> removedClassDiagList) {
		List<IFile> cldListTmp = convertDelta2IFile(removedClassDiagList);
		
		purgeClassDiagFromJavaSrcMap(javaSrcSynchro, cldListTmp);
		cldList.removeAll(cldListTmp);
	}
	
	/**
	 * When class diagram is modified, we look for elements (class, interface...) added or
	 * removed. And thus we update {@link javaSrcSynchro} map
	 * 
	 * @param modifiedClassDiagList
	 */
	private void updateClassDiagRegistration(List<IResourceDelta> modifiedClassDiagList) {
		List<IFile> cldListTmp = convertDelta2IFile(modifiedClassDiagList);
		extractMergeAllRelatedJavaFiles(javaSrcSynchro, cldListTmp);
	}
	
	private static void purgeClassDiagFromJavaSrcMap(Map<String, List<IFile>> javaSrcSynchro, List<IFile> cldList) {
		List<String> keys2Remove = new ArrayList<String>();
		
		//Purge cld files from List<IFile>: values of javaSrcSynchro
		for (String curJavaSrc : javaSrcSynchro.keySet()) {
			List<IFile> curCldList = javaSrcSynchro.get(curJavaSrc);
			for (IFile cld2Remove : cldList) {
				if (curCldList.contains(cld2Remove)) {
					curCldList.remove(cld2Remove);
				}
			}
			if (curCldList.isEmpty()) {
				keys2Remove.add(curJavaSrc);
			}
		}
		
		//Remove Entry in javaSrcSynchro where java src are not referrenced by any class diagram
		for (String key : keys2Remove) {
			javaSrcSynchro.remove(key);
		}
	}
	
	/**
	 * At startup, search all java source files related to some cld file (class diagram)
	 * in workspace and register them to watch future modifications on them and so
	 * synchronize related class diagram
	 */
	private void searchElmt2Synchronize() {
		cldList = findAllWorkspaceClassDiagram();
		
		javaSrcSynchro = new HashMap<String, List<IFile>>();// Map between java files and list of dependent cld files (class diagram)
		extractMergeAllRelatedJavaFiles(javaSrcSynchro, cldList);
	}

	/**
	 * Extract all java files referenced in class diagram of cldList and merge them with existing one
	 * in javaSrcSynchro
	 * 
	 * @param javaSrcSynchro	Map where relationship between java files and class diagram is stored
	 * @param cldList			List of class diagram files (cld) used to update {@link javaSrcSynchro}
	 */
	private static void extractMergeAllRelatedJavaFiles(Map<String, List<IFile>> javaSrcSynchro, List<IFile> cldList) {
		// Search Java file(s) linked with class diagram
		// A java file could be linked with many class diagram
		for (IFile cldFile : cldList) {
			List<String> registerJavaSrcList = getJavaSrcRegisterWithCldFile(javaSrcSynchro, cldFile);
			List<String> classDiagJavaSrcList = new ArrayList<String>();
			try {
				RootModel rootModel = DiagramSerializer.deserialize(cldFile.getContents());
				List<AbstractUMLModel> childrenModel = rootModel.getChildren();
				
				//Merge new Java src file
				for (AbstractUMLModel childModel : childrenModel) {
					if (childModel instanceof CommonEntityModel) {
						CommonEntityModel diagElmnt = (CommonEntityModel) childModel;
						String pathJavaClass = diagElmnt.getPath();
						if (pathJavaClass == null) {
							// The class in class diagram is not associated with a Java source file
							continue;
						}
						if (pathJavaClass.isEmpty() == false) {
							if (classDiagJavaSrcList.contains(pathJavaClass) == false) {
								classDiagJavaSrcList.add(pathJavaClass);
							}
							if (javaSrcSynchro.containsKey(pathJavaClass) == false) {
								List<IFile> fileList = new ArrayList<IFile>();
								fileList.add(cldFile);
								javaSrcSynchro.put(pathJavaClass, fileList);
							}
							else {
								List<IFile> fileList = javaSrcSynchro.get(pathJavaClass);
								if (fileList.contains(cldFile) == false) {
									fileList.add(cldFile);
								}
							}
						}
					}
				}
				//Remove deleted java src
				registerJavaSrcList.removeAll(classDiagJavaSrcList);
				for (String elmtToRemoved : registerJavaSrcList) {
					List<IFile> cldFileList = javaSrcSynchro.get(elmtToRemoved);
					if (cldFileList.contains(cldFile)) {
						cldFileList.remove(cldFile);
						if (cldFileList.size() == 0) {
							javaSrcSynchro.remove(elmtToRemoved);
						}
					}
				}
			}
			catch (UnsupportedEncodingException e) {
				System.err.println("Unable to process: " + cldFile.getFullPath());
				e.printStackTrace();
			}
			catch (CoreException e) {
				System.err.println("Unable to process (probably out of sync): " + cldFile.getFullPath());
				e.printStackTrace();
			}
		}
	}
	
	private static List<String> getJavaSrcRegisterWithCldFile(Map<String, List<IFile>> javaSrcSynchro, IFile cldFile) {
		List<String> javaSrcList = new ArrayList<String>();
		
		for (String curJavaSrcFile : javaSrcSynchro.keySet()) {
			List<IFile> cldList = javaSrcSynchro.get(curJavaSrcFile);
			if (cldList.contains(cldFile)) {
				javaSrcList.add(curJavaSrcFile);
			}
		}
		
		return javaSrcList;
	}

	/**
	 * Look for all class diagram cld files in opened projects of current workspace
	 */
	private static List<IFile> findAllWorkspaceClassDiagram() {
		// Search class diagram (cld files) to synchronize
		List<IFile> cldList = new ArrayList<IFile>();
		
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject[] projects = workspace.getRoot().getProjects();
		for (IProject project : projects) {
			if (project.isOpen() == true) {
				searchCLD(project, cldList);
			}
		}
		
		return cldList;
	}
	
	/**
	 * Search cld files in given location (project, directory...) at startup, to build
	 * a list of elements to keep track
	 * 
	 * @param container	in		Location where to search cld (class diagram) files
	 * @param cldList	in/out	list to fill with cld files found
	 */
	private static void searchCLD(IContainer container, List<IFile> cldList) {
		IResource[] members = null;
		try {
			members = container.members();
		}
		catch (CoreException e) {
			System.err.println("Error while getting members of: " + container.getName());
			e.printStackTrace();
		}
		for (IResource member : members) {
			if (member.isDerived() == false) { // We discard generated elmts (e.g compiled files...)
				if (member instanceof IContainer) {
					searchCLD((IContainer)member, cldList);
				}
				else if (member instanceof IFile) {
					IFile iFile = (IFile)member;
					if (iFile.getFileExtension().compareToIgnoreCase("cld") == 0) {
						cldList.add(iFile);
					}
				}
			}
		}
	}
}
