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

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.core.connections.ConnectionUtilities;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.DataPath;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.ui.views.DataEntryLabelProvider;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Class for selecting a {@link PartitionedDataSet}.
 */

public class DatasetSelectionDialog extends AbstractFilteredItemsSelectionDialog<DataEntry> {
	private static final String DIALOG_SETTINGS = "DatasetSelectionDialog";
	
	private class DataEntryFilter extends ItemsFilter {
		@Override
		public boolean matchItem(Object o) {
			// isConsistent() ensures that o is a DataEntry
			return matches(((DataEntry) o).getPath());
		}

		@Override
		public boolean isConsistentItem(Object o) {
			return o instanceof PartitionedDataSet || o instanceof SequentialDataSet;
		}

		@Override
		protected boolean matches(String text) {
			return super.matches(text.toUpperCase(Locale.ROOT));
		}
	}

	
	public DatasetSelectionDialog(final Shell parentShell) {
    	super(parentShell, false);
    	
    	String filter;
    	
    	ZdevDataSetsExplorer view = getZdevDataSetsExplorerView();
    	
    	if (view != null) {
    		filter = view.getFilter();
    	} else {
    		filter = String.format("%s.*", ConnectionUtilities.getUserID(ZdevConnectable.getConnectable()));
    	}
   		
    	if (!filter.endsWith("*")) {
    		filter += "*";
    	}
    	
    	setInitialPattern(filter);
    	
    	try {
			List<DataEntry> dataSetEntries = ZdevConnectable.getConnectable().getDataSetEntries(new DataPath(filter));
			
			setElements(dataSetEntries.toArray(new DataEntry[dataSetEntries.size()]));
		} catch (PermissionDeniedException | ConnectionException e) {
			LOG.error("Error getting entries", e);
		}

		setListLabelProvider(new DataEntryLabelProvider());
    }

	@Override
	protected String getDialogSettingsId() {
		return DIALOG_SETTINGS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new DataEntryFilter();
	}


	@Override
	protected Comparator<DataEntry> getItemsComparator() {
		return (o1,o2) -> o1.getPath().compareTo(o2.getPath());
	}

	@Override
	public String getElementName(Object o) {
		if (o instanceof DataEntry item) {
			return item.getPath();
		}
		
		return o.toString();
	}
	private ZdevDataSetsExplorer getZdevDataSetsExplorerView() {
   		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
   		ZdevDataSetsExplorer view = (ZdevDataSetsExplorer) page.findView(ZdevDataSetsExplorer.VIEW_ID);

   		if (view == null) {
   			try {
   				page.showView(ZdevDataSetsExplorer.VIEW_ID);
   				view = (ZdevDataSetsExplorer) page.findView(ZdevDataSetsExplorer.VIEW_ID);
   			} catch (PartInitException e) {
   				LOG.error("findDataSetExplorer", e);
   		    }
   		}
   		
   		return view;
	}
}
