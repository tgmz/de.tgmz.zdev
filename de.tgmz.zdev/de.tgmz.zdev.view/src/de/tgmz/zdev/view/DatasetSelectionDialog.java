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

import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Shell;

import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.ui.DataSetSelectionTree;

/**
 * Class for selecting a {@link PartitionedDataSet}.
 */

public class DatasetSelectionDialog extends AbstractSelectionDialog {
    /** Dataset Selection Tree */
    private DataSetSelectionTree dsst;
    private List<AllowedTypes> allowedTypes;
    
    /**
     * Possible types to select.
     */
    public enum AllowedTypes {
    	PDS, PS, MEMBER;
    }
    
    public DatasetSelectionDialog(final Shell parentShell, AllowedTypes... allowedTypes) {
    	super(parentShell);
    	this.allowedTypes = Arrays.asList(allowedTypes);
    	
    	Arrays.sort(allowedTypes);
    }
    
    public DatasetSelectionDialog(final Shell parentShell) {
    	this(parentShell, AllowedTypes.values());
    }
    
    @Override
    protected void insertControls(GridData gridData) {
        // PDS only, single selection only
        dsst = new DataSetSelectionTree(shell, false, false);
        dsst.addTreeSelectionListener(this);
	}
	
    public DataEntry getTarget() {
    	return dsst.getSelections() != null && !dsst.getSelections().isEmpty() ? dsst.getSelections().get(0) : null;
    }
    
	@Override
	public void widgetSelected(SelectionEvent event) {
		//CHECKSTYLE DISABLE BooleanExpressionComplexity
		this.btnOk.setEnabled(dsst.getSelections() != null 
				&& !dsst.getSelections().isEmpty()
				&& (dsst.getSelections().get(0) instanceof PartitionedDataSet && allowedTypes.contains(AllowedTypes.PDS)
					|| dsst.getSelections().get(0) instanceof SequentialDataSet && allowedTypes.contains(AllowedTypes.PS)
					|| dsst.getSelections().get(0) instanceof Member && allowedTypes.contains(AllowedTypes.MEMBER)));
	}
}
