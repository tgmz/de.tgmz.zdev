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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.zdev.preferences.CompilePreferencePage;

/**
 * Test case for preference editors.
 */
@RunWith(value = Parameterized.class)
public class CompilePreferencesTest {
	private static Composite parent;
	private static IWorkbench workbench;
	
	private String title;
	
	public CompilePreferencesTest(String title) {
		super();
		this.title = title;
	}
	
	@BeforeClass
	public static void setupOnce() {
		parent = new Shell((Display) null);
		workbench = PlatformUI.getWorkbench();
		
	}
	@Test(expected = None.class)
	public void testCompilePreferencePage() {
		CompilePreferencePage cpp = new CompilePreferencePage();
		cpp.setTitle(title);
		cpp.init(workbench);
		cpp.createControl(parent);
	}
	@Parameters(name = "{index}: Title [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "COBOL compile preferences"},
				{ "Assembler compile preferences"},
				{ "PL/I compile preferences"},
				{ "C++ compile preferences"},
				{ "C compile preferences"},
				{ "Fortran compile preferences"}, // Not implemented
				};
		return Arrays.asList(data);
	}
}
