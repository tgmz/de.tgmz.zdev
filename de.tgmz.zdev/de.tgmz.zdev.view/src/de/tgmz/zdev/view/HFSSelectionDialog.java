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

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;

import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.ui.HFSLabelProvider;

/**
 * Class for selecting a {@link HFSFolder}.
 */

public class HFSSelectionDialog extends ElementTreeSelectionDialog {
    private IZOSConstants.FileType transferMode = FileType.EBCDIC;
    
    public HFSSelectionDialog(final Shell parentShell, IZOSConnectable connectable, String root) {
    	super(parentShell, new HFSLabelProvider(), new HfsTreeContentProvider(connectable));
    	
    	setInput(new HFSFolder(connectable, root));
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

	public FileType getTransferMode() {
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
