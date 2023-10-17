/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.view.MemberNameInputDialog;

/**
 * Test class for PreferncesEditor.
 */
public class MemberNameInputDialogTest {
	private static Shell shell;

	@BeforeClass
	public static void setup() {
		shell= new Shell((Display) null);
	}
	
	@Test
	public void testMemberNameInputDialog() {
		new MemberNameInputDialog(shell, "", "", "");
	}
}
