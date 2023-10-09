/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
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
 * Message dialog with options yes, no, all, cancel.
 */

public class YesNoAllNoneCancelMessageDialog extends MessageDialog {

	public YesNoAllNoneCancelMessageDialog(final Shell parentShell, String resourceName) {
		super(parentShell
			, Activator.getDefault().getString("YesNoAllCancel.Title")
			, null
			, Activator.getDefault().getString("YesNoAllCancel.Resource", resourceName)
			, MessageDialog.QUESTION_WITH_CANCEL
			, 0	// Default index
			, IDialogConstants.YES_LABEL
			, IDialogConstants.YES_TO_ALL_LABEL
			, IDialogConstants.NO_LABEL
			, IDialogConstants.NO_TO_ALL_LABEL
			, IDialogConstants.CANCEL_LABEL);
	}
	
	@Override
	protected int getShellStyle() {
		return super.getShellStyle() | SWT.SHEET;
	}
	
	@Override
	public int open() {
		switch (super.open()) {
		case 0:
			return IDialogConstants.YES_ID;
		case 1:
			return IDialogConstants.YES_TO_ALL_ID;
		case 2:
			return IDialogConstants.NO_ID;
		case 3:
			return IDialogConstants.NO_TO_ALL_ID;
		default:
			return IDialogConstants.CANCEL_ID;
		}
	}
}
