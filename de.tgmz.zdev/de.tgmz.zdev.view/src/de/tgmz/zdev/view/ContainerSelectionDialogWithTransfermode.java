/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;

/**
 * Extension of {@link org.eclipse.ui.dialogs.ContainerSelectionDialog} to add a transfer mode radio button.
 * 
 * Credits to https://stackoverflow.com/users/2670892/greg-449 for
 * https://stackoverflow.com/questions/30500789/jface-tabs-inside-containerselectiondialog-in-eclipse-rcp
 */
public class ContainerSelectionDialogWithTransfermode extends ContainerSelectionDialog {
    private IZOSConstants.FileType transferMode = FileType.EBCDIC;
    
	public ContainerSelectionDialogWithTransfermode(final Shell parentShell, final IContainer initialRoot, final String message) {
		super(parentShell, initialRoot, false, message); 
	}

	@Override
	public Control createDialogArea(final Composite parent) {
		Composite body = (Composite) super.createDialogArea(parent);

		// Bug in ContainerSelectionDialog is returning null for the body!

		if (body == null) {
			// body is the last control added to the parent

			final Control[] children = parent.getChildren();

			if (children[children.length - 1] instanceof Composite kids) {
				body = kids;
			}
		}

		body = TransferModeCompositeFactory.getInstance().createComposite(body
				, new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER)
				, createSelectionAdapter(FileType.EBCDIC)
				, createSelectionAdapter(FileType.BINARY)
				, createSelectionAdapter(FileType.ASCII));

		return body;
	}

	public IZOSConstants.FileType getTransferMode() {
		return transferMode;
	}
	
	private SelectionAdapter createSelectionAdapter(FileType target) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				transferMode = target;
			}
		};
	}
}
