/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.restore;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.dialogs.FilteredItemsSelectionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;

/**
 * Dialog for selecting a history item from a filtered list. 
 */
public class HistorySelectionDialog extends FilteredItemsSelectionDialog {
	private static final Logger LOG = LoggerFactory.getLogger(HistorySelectionDialog.class);
	private static final String DIALOG_SETTINGS = "HistoryEntrySelectionDialog";
	private List<Long> history = new ArrayList<>();
	
    public static final ThreadLocal<DateFormat> DF = ThreadLocal.withInitial(() -> DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG));
    
	private static final Comparator<String> COMP = (o1, o2) -> {
			try {
				return fromDisplay(o2) - fromDisplay(o1) > 0 ? 1 : -1; // fromKey(o2) - fromKey(o1) yields overflow!
			} catch (ParseException e) {
				LOG.error("Cannot compare {} and {}, reason:" , o1, o2, e);
				
				return 0;
			}
		};
		
	/**
	 * Filter. Case-insensitiv.
	 */
	private class HistoryItemFilter extends ItemsFilter {
		@Override
		public boolean matchItem(Object o) {
			return true;
		}

		@Override
		public boolean isConsistentItem(Object o) {
			return o instanceof String;
		}

		@Override
		protected boolean matches(String text) {
			return true;
		}
	}
	/**
	 * Dummy-Historie. We need one so we construct an empty one.
	 */
	private class ResourceSelectionHistory extends SelectionHistory {
		@Override
		protected Object restoreItemFromMemento(IMemento element) {
			return null;
		}

		@Override
		protected void storeItemToMemento(Object item, IMemento element) {
	    	// Must implement
		}
	}
	
	public HistorySelectionDialog(Shell shell, String m) {
		super(shell, false);
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Restore.Title") : "");
		setSelectionHistory(new ResourceSelectionHistory());
		
		setListLabelProvider(new LabelProvider());
		
		try {
			history = LocalHistory.getInstance().getVersions(m);
		} catch (HistoryException e) {
			LOG.error("Cannot access history, reason:" , e);
		}
		
		setInitialElementSelections(history);
	}

	@Override
	protected Control createExtendedContentArea(Composite parent) {
    	// Must implement
		return null;
	}

	@Override
	protected IDialogSettings getDialogSettings() {
		IDialogSettings settings = Activator.getDefault().getDialogSettings().getSection(DIALOG_SETTINGS);
		if (settings == null) {
			settings = Activator.getDefault().getDialogSettings().addNewSection(DIALOG_SETTINGS);
		}
		
		return settings;
	}

	@Override
	protected IStatus validateItem(Object o) {
		return Status.OK_STATUS;
	}
	
	
	@Override
	protected ItemsFilter createFilter() {
		return new HistoryItemFilter();
	}

	@Override
	protected Comparator<String> getItemsComparator() {
		return COMP;
	}

	@Override
	protected void fillContentProvider(AbstractContentProvider contentProvider
									, ItemsFilter itemsFilter
									, IProgressMonitor progressMonitor) throws CoreException {

		IProgressMonitor subMonitor = SubMonitor.convert(progressMonitor, 100);
    	
		subMonitor.beginTask("Searching", history.size());
		
		for (Long item : history) {
			contentProvider.add(fromTime(item), itemsFilter);
			subMonitor.worked(1);
		}
		
		subMonitor.done();
	}

	@Override
	public String getElementName(Object o) {
		return String.valueOf(o);
	}
	public static long fromDisplay(String display) throws ParseException {
		return DF.get().parse(display).getTime();
	}
	private static String fromTime(long time) {
		return DF.get().format(new Date(time));
	}
}
