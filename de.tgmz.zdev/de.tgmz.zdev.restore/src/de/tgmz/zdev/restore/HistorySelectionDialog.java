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

import java.util.Comparator;

import org.eclipse.swt.widgets.Shell;

import de.tgmz.zdev.domain.id.HistoryItemId;
import de.tgmz.zdev.view.AbstractFilteredItemsSelectionDialog;

/**
 * Dialog for selecting a history item. 
 */
public class HistorySelectionDialog extends AbstractFilteredItemsSelectionDialog<HistoryItemId> {
	private static final String DIALOG_SETTINGS = "HistorySelectionDialog";

	private class HistoryItemIdFilter extends ItemsFilter {
		@Override
		public boolean matchItem(Object o) {
			if (o instanceof HistoryItemId)  {
				return matches(((HistoryItemId) o).getFqdn());
			}
			
			return false;
		}

		@Override
		public boolean isConsistentItem(Object o) {
			return o instanceof HistoryItemId;
		}
	}
	
	public HistorySelectionDialog(Shell shell, HistoryItemId... elements) {
		super(shell, false, elements);
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Restore.Title") : "");
	}

	@Override
	protected Comparator<HistoryItemId> getItemsComparator() {
		return (o1, o2) -> (int) (o2.getVersion() - o1.getVersion()); 
	}

	@Override
	public String getElementName(Object item) {
		return ((HistoryItemId) item).getFqdn();
	}

	@Override
	protected String getDialogSettingsId() {
		return DIALOG_SETTINGS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new HistoryItemIdFilter();
	}
}
