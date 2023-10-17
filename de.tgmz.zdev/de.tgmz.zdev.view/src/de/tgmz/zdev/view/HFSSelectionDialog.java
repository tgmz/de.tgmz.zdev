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
import org.eclipse.swt.widgets.Shell;

import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.ui.HFSSelectionTree;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Class for selecting a {@link HFSFolder}.
 */

public class HFSSelectionDialog extends AbstractSelectionDialog {
    /** HFS Selection Tree */
    private HFSSelectionTree hst;
    private IZOSConstants.FileType transferMode = FileType.EBCDIC;
    
    public HFSSelectionDialog(final Shell parentShell) {
    	super(parentShell);
    }

    @Override
    protected void insertControls(GridData gridData) {
        // PDS only, single selection only
        hst = new HFSSelectionTree(shell, ZdevConnectable.getConnectable(), "/u", false);
        hst.addTreeSelectionListener(this);
        
		TransferModeCompositeFactory.getInstance().createComposite(shell
				, gridData 
				, createSelectionAdapter(FileType.EBCDIC)
				, createSelectionAdapter(FileType.BINARY)
				, createSelectionAdapter(FileType.ASCII));
	}
    
    public HFSFolder getTarget() {
    	return (HFSFolder) hst.getSelection();
    }

	@Override
	public void widgetSelected(SelectionEvent event) {
		this.btnOk.setEnabled(hst.getSelection() != null);
	}

	public HFSSelectionTree getHst() {
		return hst;
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
