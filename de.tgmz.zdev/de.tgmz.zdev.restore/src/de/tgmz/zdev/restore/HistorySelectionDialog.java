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

import de.tgmz.zdev.history.HistoryIdentifyer;
import de.tgmz.zdev.view.AbstractFilteredItemsSelectionDialog;

/**
 * Dialog for selecting a history item. 
 */
public class HistorySelectionDialog extends AbstractFilteredItemsSelectionDialog<HistoryIdentifyer> {
	private static final String DIALOG_SETTINGS = "HistorySelectionDialog";

	private class HistoryIdentifyerFilter extends ItemsFilter {
		@Override
		public boolean matchItem(Object o) {
			if (o instanceof HistoryIdentifyer item) {
				return matches(item.getFqdn());
			}
			
			return false;
		}

		@Override
		public boolean isConsistentItem(Object o) {
			return o instanceof HistoryIdentifyer;
		}
	}
	
	public HistorySelectionDialog(Shell shell, HistoryIdentifyer... elements) {
		super(shell, false, elements);
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Restore.Title") : "");
	}

	@Override
	protected Comparator<HistoryIdentifyer> getItemsComparator() {
		return (o1, o2) -> (int) (o2.getId() - o1.getId()); 
	}

	@Override
	public String getElementName(Object item) {
		return ((HistoryIdentifyer) item).getFqdn();
	}

	@Override
	protected String getDialogSettingsId() {
		return DIALOG_SETTINGS;
	}

	@Override
	protected ItemsFilter createFilter() {
		return new HistoryIdentifyerFilter();
	}
}
