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

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Create a composite for selecting transfer types.
 */
public class TransferModeCompositeFactory {
	private static final TransferModeCompositeFactory INSTANCE = new TransferModeCompositeFactory();
	
	private TransferModeCompositeFactory() {
		// Disable instantiation
	}
	
	public Composite createComposite(Composite body, GridData parentGridData, SelectionAdapter... adapters) {
		//Add your controls here
        Label label = new Label(body, SWT.NONE);
        label.setText(Activator.getDefault().getString("TransferMode.Mode"));
        label.setLayoutData(parentGridData);
        
        GridData transferModeGridData = new GridData(SWT.HORIZONTAL | SWT.VERTICAL);
        
        Composite cTransferMode = new Composite(body, SWT.BORDER | SWT.RADIO);
        cTransferMode.setLayout(new RowLayout());
        cTransferMode.setLayoutData(transferModeGridData);
        
        Label lab = new Label(cTransferMode, SWT.NONE);
        lab.setText(Activator.getDefault().getString("TransferMode.Ebcdic"));
        Button chkEbcdic = new Button(cTransferMode, SWT.RADIO);
        chkEbcdic.setSelection(true);
        
        lab = new Label(cTransferMode, SWT.NONE);
        lab.setText(Activator.getDefault().getString("TransferMode.Binary"));
        Button chkBinary = new Button(cTransferMode, SWT.RADIO);
        chkBinary.setSelection(false);
        
        lab = new Label(cTransferMode, SWT.NONE);
        lab.setText(Activator.getDefault().getString("TransferMode.Ascii"));
        Button chkAscii = new Button(cTransferMode, SWT.RADIO);
        chkAscii.setSelection(false);
        
        chkEbcdic.addSelectionListener(adapters[0]);
        chkBinary.addSelectionListener(adapters[1]);
        chkAscii.addSelectionListener(adapters[2]);

		return body;
	}

	public static TransferModeCompositeFactory getInstance() {
		return INSTANCE;
	}
}
