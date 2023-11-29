/*********************************************************************
* Copyright (c) 23.11.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile;

import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

public class BindSelectionAdapter extends SelectionAdapter {
    private Button chkBind;
	private Text txtBind;

    public BindSelectionAdapter(Item item, Button chkBind, Text txtBind) {
		super();
		this.txtBind = txtBind;
		this.chkBind = chkBind;
	}
    @Override
	public void widgetSelected(final SelectionEvent e) {
        txtBind.setEnabled(chkBind.getSelection());
        
        if (chkBind.getSelection() && "".equals(txtBind.getText())) {
        	String bind = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.LINK_OPTIONS);
        	
        	txtBind.setText(bind);
        }
    }
	
}
