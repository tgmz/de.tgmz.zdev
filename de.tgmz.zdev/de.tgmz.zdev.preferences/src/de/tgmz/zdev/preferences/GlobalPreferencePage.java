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

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class GlobalPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	public GlobalPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	@Override
	public void createFieldEditors() {
		final Composite fieldEditorParent = getFieldEditorParent();
		
		addField(new BooleanFieldEditor(
					ZdevPreferenceConstants.JOB_SUFFIX,
					Activator.getDefault().getString("ZdevPreferencePage.JOB_SUFFIX"),
					BooleanFieldEditor.SEPARATE_LABEL,
					fieldEditorParent));

		addField(new TextAreaFieldEditor(
				ZdevPreferenceConstants.JOB_CARD,  
				Activator.getDefault().getString("ZdevPreferencePage.JOB_CARD"),
				fieldEditorParent));
		
		addField(new TextAreaFieldEditor(
				ZdevPreferenceConstants.USS_HOME,  
				Activator.getDefault().getString("ZdevPreferencePage.USS_HOME"),
				fieldEditorParent));
		
		addField(new ComboFieldEditor(
				ZdevPreferenceConstants.LOG_LEVEL, 
				Activator.getDefault().getString("ZdevPreferencePage.LOG_LEVEL"),
				new String[][] {
								{"OFF", "OFF"},
								{"FATAL", "FATAL"},
								{"ERROR", "ERROR"},
								{"WARN", "WARN"},
								{"INFO", "INFO"},
								{"DEBUG", "DEBUG"},
								{"TRACE", "TRACE"},
								{"ALL", "ALL"},
				},
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
