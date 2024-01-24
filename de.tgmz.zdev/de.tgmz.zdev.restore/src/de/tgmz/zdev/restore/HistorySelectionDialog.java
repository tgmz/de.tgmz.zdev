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

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.history.HistoryIdentifyer;

/**
 * Dialog for selecting a history item. 
 */
public class HistorySelectionDialog extends ElementListSelectionDialog {
	private static final Logger LOG = LoggerFactory.getLogger(HistorySelectionDialog.class);
    
	public HistorySelectionDialog(Shell shell, HistoryIdentifyer... elements) {
		super(shell, new LabelProvider());
		
		LOG.info("Init new HistorySelectionDialog");
		
		Activator act = Activator.getDefault();
		
		setTitle(act != null ? act.getString("Restore.Title") : "");
		
		setElements(elements);
	}
}
