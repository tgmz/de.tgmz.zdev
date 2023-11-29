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
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <em>FieldEditorPreferencePage</em>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class EditorPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public EditorPreferencePage() {
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
				ZdevPreferenceConstants.FILELOCK_AUTO,
				Activator.getDefault().getString("ZdevPreferencePage.FILELOCK_AUTO"),
				BooleanFieldEditor.SEPARATE_LABEL,
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_PLI, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_PLI"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_JCL, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_JCL"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_COB, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_COB"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_ASM, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_ASM"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_C, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_C"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_CPP, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_CPP"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_SQL, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_SQL"),
				fieldEditorParent));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_REX, 
				Activator.getDefault().getString("ZdevPreferencePage.REGEX_REX"),
				fieldEditorParent));
		
		addField(new BooleanFieldEditor(
				ZdevPreferenceConstants.CAPS_ON,
				Activator.getDefault().getString("ZdevPreferencePage.CAPS_ON"),
				BooleanFieldEditor.SEPARATE_LABEL,
				fieldEditorParent));
		
		addField(new IntegerFieldEditor(
				ZdevPreferenceConstants.HISTORY_MONTHS,
				Activator.getDefault().getString("ZdevPreferencePage.HISTORY_MONTHS"),
				fieldEditorParent));
		
		addField(new IntegerFieldEditor(
				ZdevPreferenceConstants.HISTORY_VERSIONS,
				Activator.getDefault().getString("ZdevPreferencePage.HISTORY_VERSIONS"),
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
