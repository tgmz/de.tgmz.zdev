/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.view.SkipIgnoreAbortMessageDialog;
import de.tgmz.zdev.view.YesNoAllNoneCancelMessageDialog;

/**
 * Testclass for die MessageDialogs.
 */
public class MessageDialogTest {
	private static Shell shell;

	@BeforeClass
	public static void setup() {
		shell= new Shell((Display) null);
	}
	
	@Test
	public void testSkipIgnoreAbortMessageDialog() {
		SkipIgnoreAbortMessageDialog siamd = new SkipIgnoreAbortMessageDialog(shell, "");
		
		assertEquals(0, siamd.getReturnCode());
	}
	
	@Test
	public void testYesNoAllNoneCancelMessageDialog() {
		YesNoAllNoneCancelMessageDialog ynancmd = new YesNoAllNoneCancelMessageDialog(shell, "");
		
		assertEquals(0, ynancmd.getReturnCode());
	}
}
