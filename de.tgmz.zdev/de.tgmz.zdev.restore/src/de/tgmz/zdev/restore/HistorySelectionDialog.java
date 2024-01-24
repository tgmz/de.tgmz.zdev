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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.DataEntry;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.HistoryIdentifyer;
import de.tgmz.zdev.history.LocalHistory;

/**
 * Dialog for selecting a history item. 
 */
public class HistorySelectionDialog extends ElementListSelectionDialog {
	private static final Logger LOG = LoggerFactory.getLogger(HistorySelectionDialog.class);
    
	public HistorySelectionDialog(Shell shell, DataEntry de) {
		super(shell, new LabelProvider());
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Restore.Title") : "");
		
		try {
			List<HistoryIdentifyer> history = LocalHistory.getInstance().getVersions(de.toDisplayName());
			
			HistoryIdentifyer[] elements = history.toArray(new HistoryIdentifyer[history.size()]);
			
			Arrays.sort(elements, (o1, o2) -> o1.getId() < o2.getId() ? 1 : -1);
			
			setElements(elements);
		} catch (HistoryException e) {
			LOG.error("Cannot access history, reason:", e);
		}
	}
}
