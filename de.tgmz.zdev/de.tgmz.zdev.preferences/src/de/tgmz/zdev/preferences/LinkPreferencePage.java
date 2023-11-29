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

public class LinkPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public LinkPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	@Override
	public void createFieldEditors() {
		final Composite fieldEditorParent = getFieldEditorParent();
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.OBJLIB, 
				Activator.getDefault().getString("ZdevPreferencePage.OBJLIB"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.LOADLIB, 
				Activator.getDefault().getString("ZdevPreferencePage.LOADLIB"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.LINK_OPTIONS, 
				Activator.getDefault().getString("ZdevPreferencePage.LINK_OPTIONS"),
				fieldEditorParent));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Must implement this, even if it is empty
	}
}
