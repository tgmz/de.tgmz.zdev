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

public class Db2SelectionAdapter extends SelectionAdapter {
	private Item item;
    private Button chkDb2;
	private Text txtDb2;

    public Db2SelectionAdapter(Item item, Button chkDb2, Text txtDb2) {
		super();
		this.item = item;
		this.txtDb2 = txtDb2;
		this.chkDb2 = chkDb2;
	}
    @Override
	public void widgetSelected(final SelectionEvent e) {
        txtDb2.setEnabled(chkDb2.getSelection());
        
        if (chkDb2.getSelection() && "".equals(txtDb2.getText())) {
        	String db2;

    		IPreferenceStore ps = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore();

        	switch (Language.fromDatasetName(item.getDsn())) {
        	case ASSEMBLER:
        		db2 = ps.getString(ZdevPreferenceConstants.DB2_ASM);
        		break;
        	case C:
        		db2 = ps.getString(ZdevPreferenceConstants.DB2_C);
        		break;
        	case CPP:
        		db2 = ps.getString(ZdevPreferenceConstants.DB2_CPP);
        		break;
        	case COBOL:
        		db2 = ps.getString(ZdevPreferenceConstants.DB2_COB);
        		break;
        	case PLI:
        		db2 = ps.getString(ZdevPreferenceConstants.DB2_PLI);
        		break;
        	default:
        		db2 = "";
        		break;
        	}
        	
        	txtDb2.setText(db2);
        }
    }
	
}
