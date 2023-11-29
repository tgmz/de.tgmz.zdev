/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.preference.test;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;

import de.tgmz.zdev.preferences.EditorPreferencePage;
import de.tgmz.zdev.preferences.JobPreferencePage;
import de.tgmz.zdev.preferences.LinkPreferencePage;
import de.tgmz.zdev.preferences.ZdevPreferencePage;

/**
 * Test case for preference editors.
 */
public class PreferencesTest {
	private static Composite parent;
	private static IWorkbench workbench;
	
	@BeforeClass
	public static void setupOnce() {
		parent = new Shell((Display) null);
		workbench = PlatformUI.getWorkbench();
		
	}
	@Test(expected = None.class)
	public void testPreferencePage() {
		ZdevPreferencePage epp = new ZdevPreferencePage();
		epp.init(workbench);
		epp.createControl(parent);
	}
	@Test(expected = None.class)
	public void testEditorPreferencePage() {
		EditorPreferencePage epp = new EditorPreferencePage();
		epp.init(workbench);
		epp.createControl(parent);
		epp.createFieldEditors();
	}
	@Test(expected = None.class)
	public void testJobPreferencePage() {
		JobPreferencePage jpp = new JobPreferencePage();
		jpp.init(workbench);
		jpp.createControl(parent);
		jpp.createFieldEditors();
	}
	@Test(expected = None.class)
	public void testLinkPreferencePage() {
		LinkPreferencePage lpp = new LinkPreferencePage();
		lpp.init(workbench);
		lpp.createControl(parent);
		lpp.createFieldEditors();
	}
}
