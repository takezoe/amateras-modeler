/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package net.java.amateras.uml.xmi.importWizards;

import net.java.amateras.uml.xmi.Activator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;


public class ImportWizardPage extends WizardNewFileCreationPage {
	
	protected FileFieldEditor editor;

	public ImportWizardPage(String pageName, IStructuredSelection selection) {
		super(pageName, selection);
		setTitle(pageName);
		setDescription(Activator.getDefault().getResourceString("wizard.creation.description"));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createAdvancedControls(org.eclipse.swt.widgets.Composite)
	 */	
	protected void createAdvancedControls(Composite parent) {
//		Composite fileSelectionArea = new Composite(parent, SWT.NONE);
//		GridData fileSelectionData = new GridData(GridData.GRAB_HORIZONTAL
//				| GridData.FILL_HORIZONTAL);
//		fileSelectionArea.setLayoutData(fileSelectionData);
//
//		GridLayout fileSelectionLayout = new GridLayout();
//		fileSelectionLayout.numColumns = 3;
//		fileSelectionLayout.makeColumnsEqualWidth = false;
//		fileSelectionLayout.marginWidth = 0;
//		fileSelectionLayout.marginHeight = 0;
//		fileSelectionArea.setLayout(fileSelectionLayout);
//		
//		editor = new FileFieldEditor("fileSelect","Select File: ",fileSelectionArea); //NON-NLS-1 //NON-NLS-2
//		editor.getTextControl(fileSelectionArea).addModifyListener(new ModifyListener(){
//			public void modifyText(ModifyEvent e) {
//				IPath path = new Path(ImportWizardPage.this.editor.getStringValue());
//				setFileName(path.lastSegment());
//			}
//		});
//		String[] extensions = new String[] { "*.*" }; //NON-NLS-1
//		editor.setFileExtensions(extensions);
//		fileSelectionArea.moveAbove(null);

	}
	
	 /* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#createLinkTarget()
	 */
	protected void createLinkTarget() {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getInitialContents()
	 */
//	protected InputStream getInitialContents() {
//		try {
//			return new FileInputStream(new File(editor.getStringValue()));
//		} catch (FileNotFoundException e) {
//			return null;
//		}
//	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#getNewFileLabel()
	 */
	protected String getNewFileLabel() {
		return Activator.getDefault().getResourceString("wizard.creation.filelabel");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.dialogs.WizardNewFileCreationPage#validateLinkedResource()
	 */
	protected IStatus validateLinkedResource() {
		return new Status(IStatus.OK, "net.java.amateras.umleditor.xmi", IStatus.OK, "", null); //NON-NLS-1 //NON-NLS-2
	}
}
