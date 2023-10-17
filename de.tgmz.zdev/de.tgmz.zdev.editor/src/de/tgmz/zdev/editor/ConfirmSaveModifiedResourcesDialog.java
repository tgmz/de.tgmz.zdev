/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.util.List;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.PlatformUI;

import com.ibm.cics.zos.model.IZOSObject;

/**
 * This dialog displays a list of <code>IFile</code> and asks
 * the user to confirm saving all of them.
 * <p>
 * This concrete dialog class can be instantiated as is.
 * It is not intended to be subclassed.
 * </p>
 */
public class ConfirmSaveModifiedResourcesDialog extends MessageDialog {
	private final List<IZOSObject> fUnsavedFiles;
	
	public ConfirmSaveModifiedResourcesDialog(List<IZOSObject> unsavedFiles) {
		super(
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
			Activator.getDefault().getString("ConfirmSaveModifiedResourcesDialog.Title"),
			null,
			Activator.getDefault().getString("ConfirmSaveModifiedResourcesDialog.Message"),
			org.eclipse.jface.dialogs.MessageDialog.QUESTION,
			new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL },
			0);
		
		fUnsavedFiles = unsavedFiles;
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		TableViewer fList = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		fList.setContentProvider(new ArrayContentProvider());
		
		for (IZOSObject dirtyObject : fUnsavedFiles) {
			fList.add(dirtyObject.getPath());
		}
		
		Control control= fList.getControl();
		GridData data= new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
		data.widthHint= convertWidthInCharsToPixels(20);
		data.heightHint= convertHeightInCharsToPixels(5);
		control.setLayoutData(data);
		applyDialogFont(control);
		return control;
	}
}
