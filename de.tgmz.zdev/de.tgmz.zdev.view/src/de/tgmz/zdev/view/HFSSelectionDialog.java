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
<<<<<<< Upstream, based on develop
=======
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
>>>>>>> e96cb40 Add transfer mode
import org.eclipse.swt.widgets.Shell;

import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.HFSFolder;
<<<<<<< Upstream, based on develop
import com.ibm.cics.zos.ui.HFSSelectionTree;
=======
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.ui.HFSLabelProvider;
>>>>>>> e96cb40 Add transfer mode

<<<<<<< Upstream, based on develop
import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

=======
>>>>>>> e96cb40 Add transfer mode
/**
 * Class for selecting a {@link HFSFolder}.
 */

<<<<<<< Upstream, based on develop
public class HFSSelectionDialog extends AbstractSelectionDialog {
    /** HFS Selection Tree */
    private HFSSelectionTree hst;
    private IZOSConstants.FileType transferMode = FileType.EBCDIC;
    
    public HFSSelectionDialog(final Shell parentShell) {
    	super(parentShell);
=======
public class HFSSelectionDialog extends ElementTreeSelectionDialog {
    private IZOSConstants.FileType transferMode = FileType.EBCDIC;
    
    public HFSSelectionDialog(final Shell parentShell, IZOSConnectable connectable, String root) {
    	super(parentShell, new HFSLabelProvider(), new HfsTreeContentProvider(connectable));
    	
    	setInput(new HFSFolder(connectable, root));
>>>>>>> e96cb40 Add transfer mode
    }

<<<<<<< Upstream, based on develop
    @Override
    protected void insertControls(GridData gridData) {
        // PDS only, single selection only
        hst = new HFSSelectionTree(shell
        		, ZdevConnectable.getConnectable()
        		, de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.USS_HOME)
        		, false);
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
=======
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
	
>>>>>>> e96cb40 Add transfer mode
	private SelectionAdapter createSelectionAdapter(FileType target) {
		return new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				transferMode = target;
			}
		};
	}
}
