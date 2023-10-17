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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;

import de.tgmz.zdev.view.ContainerSelectionDialogWithTransfermode;
import de.tgmz.zdev.view.DatasetSelectionDialog;
import de.tgmz.zdev.view.HFSSelectionDialog;

/**
 * Test class for SelectionDialogs.
 */
public class SelectionDialogTest {
	private static Shell shell;

	@BeforeClass
	public static void setup() {
		shell= new Shell((Display) null);
	}
	
	@Test
	public void testDataSetSelectionDialog() {
		DatasetSelectionDialog dsd = new DatasetSelectionDialog(shell);
		
		assertNull(dsd.getTarget());
	}
	
	@Test
	public void testHfsSelectionDialog() {
		HFSSelectionDialog hsd = new HFSSelectionDialog(shell);
		
		assertNull(hsd.getTarget());
	}
	
	@Test
	public void testContainerSelectionDialog() {
		ContainerSelectionDialogWithTransfermode csdwtm = new ContainerSelectionDialogWithTransfermode(shell, null, "");
		
		assertNotNull(csdwtm.createDialogArea(shell));
		
		assertNull(csdwtm.getResult());
	}
}
