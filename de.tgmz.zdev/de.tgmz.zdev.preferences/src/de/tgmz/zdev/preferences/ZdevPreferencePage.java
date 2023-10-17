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
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
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

public class ZdevPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	
	private ZdevInfoOnlyOnSubmitBooleanFieldEditor infoOnylOnSubmitFE;
	private IntegerFieldEditor waitTimeFE; 
	
	public ZdevPreferencePage() {
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
		addField(new BooleanFieldEditor(
					ZdevPreferenceConstants.JOB_SUFFIX,
					Activator.getDefault().getString("ZdevPreferencePage.JobSuffix"),
					BooleanFieldEditor.SEPARATE_LABEL,
					getFieldEditorParent()));

		addField(new ComboFieldEditor(
				ZdevPreferenceConstants.LOG_LEVEL, 
				Activator.getDefault().getString("ZdevPreferencePage.LogLevel"),
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
				getFieldEditorParent()));
		
		infoOnylOnSubmitFE = new ZdevInfoOnlyOnSubmitBooleanFieldEditor(
				ZdevPreferenceConstants.INFO_ONLY_ON_SUBMIT,
				Activator.getDefault().getString("ZdevPreferencePage.BlockOnSubmit"),
				BooleanFieldEditor.SEPARATE_LABEL,
				getFieldEditorParent());
		
		waitTimeFE = new IntegerFieldEditor(ZdevPreferenceConstants.INFO_TIME, 
				Activator.getDefault().getString("ZdevPreferencePage.BlockTime"),
				getFieldEditorParent());

		waitTimeFE.setEnabled(getPreferenceStore().getBoolean(ZdevPreferenceConstants.INFO_ONLY_ON_SUBMIT), getFieldEditorParent());
		
		infoOnylOnSubmitFE
			.getChangeControl(getFieldEditorParent())
			.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					waitTimeFE.setEnabled(infoOnylOnSubmitFE.getBooleanValue(), getFieldEditorParent());
				}
				@Override
				public void widgetDefaultSelected(final SelectionEvent e) {
					// Must implement this, even if it is empty
				}
			});
		
		addField(infoOnylOnSubmitFE);

		addField(waitTimeFE);
		
		addField(new TextAreaFieldEditor(
				ZdevPreferenceConstants.JOB_CARD,  
				Activator.getDefault().getString("ZdevPreferencePage.JobCard"),
				getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(
				ZdevPreferenceConstants.FILELOCK_AUTO,
				Activator.getDefault().getString("ZdevPreferencePage.FileLockAuto"),
				BooleanFieldEditor.SEPARATE_LABEL,
				getFieldEditorParent()));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_PLI, 
				Activator.getDefault().getString("ZdevPreferencePage.RegexpPli"),
				getFieldEditorParent()));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_JCL, 
				Activator.getDefault().getString("ZdevPreferencePage.RegexpJcl"),
				getFieldEditorParent()));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_COBOL, 
				Activator.getDefault().getString("ZdevPreferencePage.RegexpCob"),
				getFieldEditorParent()));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_ASSEMBLER, 
				Activator.getDefault().getString("ZdevPreferencePage.RegexpAsm"),
				getFieldEditorParent()));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_SQL, 
				Activator.getDefault().getString("ZdevPreferencePage.RegexpSql"),
				getFieldEditorParent()));
		
		addField(new StringFieldEditor(
				ZdevPreferenceConstants.REGEX_REXX, 
				Activator.getDefault().getString("ZdevPreferencePage.RegexpRexx"),
				getFieldEditorParent()));
		
		addField(new BooleanFieldEditor(
				ZdevPreferenceConstants.CAPS_ON,
				Activator.getDefault().getString("ZdevPreferencePage.CapsOn"),
				BooleanFieldEditor.SEPARATE_LABEL,
				getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(
				ZdevPreferenceConstants.HISTORY_MONTHS,
				Activator.getDefault().getString("ZdevPreferencePage.HistoryMonths"),
				getFieldEditorParent()));
		
		addField(new IntegerFieldEditor(
				ZdevPreferenceConstants.HISTORY_VERSIONS,
				Activator.getDefault().getString("ZdevPreferencePage.HistoryVersions"),
				getFieldEditorParent()));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {
		// Must implement this, even if it is empty
	}
}
