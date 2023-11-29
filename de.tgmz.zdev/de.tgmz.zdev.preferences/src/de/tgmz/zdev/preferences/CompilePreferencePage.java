/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * Compile preferences page.
 */
public class CompilePreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public CompilePreferencePage() {
		super(GRID);
		
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	@Override
	public void createFieldEditors() {
		final Composite fieldEditorParent = getFieldEditorParent();
		
		String[][] s = new String[4][2];
		
		// Getting the language by the pages id would be much better but as there seems to be no way to get it we use the title.
		switch (guess(getTitle())) {
		case COBOL:
			s[0][0] = ZdevPreferenceConstants.SYSLIB_COB;
			s[0][1] = "ZdevPreferencePage.SYSLIB_COB";
			s[1][0] = ZdevPreferenceConstants.COMP_COB;
			s[1][1] = "ZdevPreferencePage.COMP_COB";
			s[2][0] = ZdevPreferenceConstants.DB2_COB;
			s[2][1] = "ZdevPreferencePage.DB2_COB";
			s[3][0] = ZdevPreferenceConstants.CICS_COB;
			s[3][1] = "ZdevPreferencePage.CICS_COB";
			break;
		case ASSEMBLER:
			s[0][0] = ZdevPreferenceConstants.SYSLIB_ASM;
			s[0][1] = "ZdevPreferencePage.SYSLIB_ASM";
			s[1][0] = ZdevPreferenceConstants.COMP_ASM;
			s[1][1] = "ZdevPreferencePage.COMP_ASM";
			s[2][0] = ZdevPreferenceConstants.DB2_ASM;
			s[2][1] = "ZdevPreferencePage.DB2_ASM";
			s[3][0] = ZdevPreferenceConstants.CICS_ASM;
			s[3][1] = "ZdevPreferencePage.CICS_ASM";
			break;
		case PLI:
			s[0][0] = ZdevPreferenceConstants.SYSLIB_PLI;
			s[0][1] = "ZdevPreferencePage.SYSLIB_PLI";
			s[1][0] = ZdevPreferenceConstants.COMP_PLI;
			s[1][1] = "ZdevPreferencePage.COMP_PLI";
			s[2][0] = ZdevPreferenceConstants.DB2_PLI;
			s[2][1] = "ZdevPreferencePage.DB2_PLI";
			s[3][0] = ZdevPreferenceConstants.CICS_PLI;
			s[3][1] = "ZdevPreferencePage.CICS_PLI";
			break;
		case CPP:
			s[0][0] = ZdevPreferenceConstants.SYSLIB_CPP;
			s[0][1] = "ZdevPreferencePage.SYSLIB_CPP";
			s[1][0] = ZdevPreferenceConstants.COMP_CPP;
			s[1][1] = "ZdevPreferencePage.COMP_CPP";
			s[2][0] = ZdevPreferenceConstants.DB2_CPP;
			s[2][1] = "ZdevPreferencePage.DB2_CPP";
			s[3][0] = ZdevPreferenceConstants.CICS_CPP;
			s[3][1] = "ZdevPreferencePage.CICS_CPP";
			break;
		case C:
		default:
			s[0][0] = ZdevPreferenceConstants.SYSLIB_C;
			s[0][1] = "ZdevPreferencePage.SYSLIB_C";
			s[1][0] = ZdevPreferenceConstants.COMP_C;
			s[1][1] = "ZdevPreferencePage.COMP_C";
			s[2][0] = ZdevPreferenceConstants.DB2_C;
			s[2][1] = "ZdevPreferencePage.DB2_C";
			s[3][0] = ZdevPreferenceConstants.CICS_C;
			s[3][1] = "ZdevPreferencePage.CICS_C";
			break;
		}
		
		for (String[] id : s) {
			addField(new StringFieldEditor(
					id[0], 
					Activator.getDefault().getString(id[1]),
					fieldEditorParent));
		}
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Must implement this, even if it is empty
	}
	
	private Language guess(String title) {
		if (title.contains("COBOL")) {
			return Language.COBOL;
		}
		if (title.contains("Assembler")) {
			return Language.ASSEMBLER;
		}
		if (title.contains("C++")) {
			return Language.CPP;
		}
		if (title.contains("PL/I")) {
			return Language.PLI;
		}
		
		return Language.C;
	}
}
