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

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import de.tgmz.zdev.view.copypaste.MemberNameValidator;

/**
 * Input dialog for member name.
 */
public class MemberNameInputDialog extends InputDialog {

	public MemberNameInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue, IInputValidator validator) {
		super(parentShell, dialogTitle, dialogMessage, initialValue, validator);
	}

	public MemberNameInputDialog(Shell parentShell, String dialogTitle, String dialogMessage, String initialValue) {
		this(parentShell, dialogTitle, dialogMessage, initialValue, new MemberNameValidator());
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		
		VerifyListener vl = e -> {
	        if (Character.isLowerCase(e.character)) {
	            e.character = Character.toUpperCase(e.character);
	            e.text = e.text.toUpperCase();
	        }
		};
		
		this.getText().addVerifyListener(vl);
		
		return composite;
	}	
	
}
