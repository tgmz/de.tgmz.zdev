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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.preferences.Language;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

public class CicsSelectionAdapter extends SelectionAdapter {
	private Item item;
    private Button chkCics;
	private Text txtCics;

    public CicsSelectionAdapter(Item item, Button chkCics, Text txtCics) {
		super();
		this.item = item;
		this.txtCics = txtCics;
		this.chkCics = chkCics;
	}
    @Override
	public void widgetSelected(final SelectionEvent e) {
        txtCics.setEnabled(chkCics.getSelection());
        
        if (chkCics.getSelection() && "".equals(txtCics.getText())) {
        	String cics;

    		IPreferenceStore ps = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore();

        	switch (Language.fromDatasetName(item.getDsn())) {
        	case ASSEMBLER:
        		cics = ps.getString(ZdevPreferenceConstants.CICS_ASM);
        		break;
        	case C:
        		cics = ps.getString(ZdevPreferenceConstants.CICS_C);
        		break;
        	case CPP:
        		cics = ps.getString(ZdevPreferenceConstants.CICS_CPP);
        		break;
        	case COBOL:
        		cics = ps.getString(ZdevPreferenceConstants.CICS_COB);
        		break;
        	case PLI:
        		cics = ps.getString(ZdevPreferenceConstants.CICS_PLI);
        		break;
        	default:
        		cics = "";
        		break;
        	}
        	
        	txtCics.setText(cics);
        }
    }
	
}
