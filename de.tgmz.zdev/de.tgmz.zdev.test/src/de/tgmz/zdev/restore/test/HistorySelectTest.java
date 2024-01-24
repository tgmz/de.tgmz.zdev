/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.test;

import static org.junit.Assert.assertEquals;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.history.HistoryIdentifyer;
import de.tgmz.zdev.restore.HistorySelectionDialog;

public class HistorySelectTest {
	private static Shell shell;
	
	@Test
	public void testDialog() {
		HistorySelectionDialog hsd = new HistorySelectionDialog(shell, new HistoryIdentifyer("HLQ.PLI(MYMEMBER)", 0L, 1));
		
		hsd.create();
		hsd.setBlockOnOpen(false);
		
		assertEquals(0, hsd.open());
		
		hsd.close();
	}
	
	@BeforeClass
	public static void setup() {
		shell = new Shell((Display) null);
	}
}
