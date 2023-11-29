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

public class CompileSelectionAdapter extends SelectionAdapter {
	private Item item;
    private Button chkComp;
	private Text txtComp;

    public CompileSelectionAdapter(Item item, Button chkComp, Text txtComp) {
		super();
		this.item = item;
		this.txtComp = txtComp;
		this.chkComp = chkComp;
	}
    @Override
	public void widgetSelected(final SelectionEvent e) {
        txtComp.setEnabled(chkComp.getSelection());
        
        if (chkComp.getSelection() && "".equals(txtComp.getText())) {
        	String comp;

    		IPreferenceStore ps = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore();

        	switch (Language.fromDatasetName(item.getDsn())) {
        	case ASSEMBLER:
        		comp = ps.getString(ZdevPreferenceConstants.COMP_ASM);
        		break;
        	case C:
        		comp = ps.getString(ZdevPreferenceConstants.COMP_C);
        		break;
        	case CPP:
        		comp = ps.getString(ZdevPreferenceConstants.COMP_CPP);
        		break;
        	case COBOL:
        		comp = ps.getString(ZdevPreferenceConstants.COMP_COB);
        		break;
        	case PLI:
        		comp = ps.getString(ZdevPreferenceConstants.COMP_PLI);
        		break;
        	default:
        		comp = "";
        		break;
        	}
        	
        	txtComp.setText(comp);
        }
    }
	
}
