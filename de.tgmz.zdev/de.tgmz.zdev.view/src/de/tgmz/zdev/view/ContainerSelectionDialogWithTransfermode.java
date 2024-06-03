/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
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
 */
public class ContainerSelectionDialogWithTransfermode extends ContainerSelectionDialog {
    private IZOSConstants.FileType transferMode = FileType.EBCDIC;
    
	public ContainerSelectionDialogWithTransfermode(final Shell parentShell, final IContainer initialRoot, final String message) {
		super(parentShell, initialRoot, false, message); 
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		TransferModeCompositeFactory.getInstance().createComposite(parent
				, new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_CENTER)
				, createSelectionAdapter(FileType.EBCDIC)
				, createSelectionAdapter(FileType.BINARY)
				, createSelectionAdapter(FileType.ASCII));
		
		return super.createButtonBar(parent);
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
