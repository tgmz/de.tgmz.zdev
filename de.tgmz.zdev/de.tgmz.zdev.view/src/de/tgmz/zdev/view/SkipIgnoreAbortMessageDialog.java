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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;

/**
 * Message dialog with options skip, abort ignore.
 */

public class SkipIgnoreAbortMessageDialog extends MessageDialog {

	public SkipIgnoreAbortMessageDialog(final Shell parentShell, String resourceName) {
		super(parentShell
			, Activator.getDefault().getString("SkipIgnoreAbort.Title")
			, null
			, Activator.getDefault().getString("SkipIgnoreAbort.Resource", resourceName)
			, MessageDialog.QUESTION_WITH_CANCEL
			, 0	// Default index
			, IDialogConstants.SKIP_LABEL
			, IDialogConstants.ABORT_LABEL
			, IDialogConstants.IGNORE_LABEL);
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.SHEET;
	}
	
	@Override
	public int open() {
		switch (super.open()) {
		case 0:
			return IDialogConstants.SKIP_ID;
		case 1:
			return IDialogConstants.ABORT_ID;
		default:
			return IDialogConstants.IGNORE_ID;
		}
	}
}
