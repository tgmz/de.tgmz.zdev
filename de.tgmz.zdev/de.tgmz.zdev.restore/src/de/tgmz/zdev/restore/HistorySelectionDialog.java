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

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.DataEntry;

import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.history.model.HistoryIdentifyer;
import de.tgmz.zdev.restore.compare.HistoryItemComparator;

/**
 * Dialog for selecting a history item. 
 */
public class HistorySelectionDialog extends ElementListSelectionDialog {
	private static final Logger LOG = LoggerFactory.getLogger(HistorySelectionDialog.class);
	private List<HistoryIdentifyer> history = new ArrayList<>();
	
    protected static final ThreadLocal<NumberFormat> NF = ThreadLocal.withInitial(NumberFormat::getNumberInstance);
    
	private static final Comparator<String> COMP = new HistoryItemComparator();
		
	public HistorySelectionDialog(Shell shell, DataEntry de) {
		super(shell, new LabelProvider());
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Restore.Title") : "");
		
		try {
			history = LocalHistory.getInstance().getVersions(de.toDisplayName());
		} catch (HistoryException e) {
			LOG.error("Cannot access history, reason:" , e);
		}
		
		setElements(getSelectionList());
	}
	private String[] getSelectionList() {
		List<String> l = new ArrayList<>();
		
		for (HistoryIdentifyer hi : history) {
			l.add(HistoryItemComparator.fromTime(hi.getId()) + " (" + NF.get().format(hi.getSize()) + " bytes)");
		}
		
		l.sort(COMP);
		
		return l.toArray(new String[l.size()]);
	}
}
