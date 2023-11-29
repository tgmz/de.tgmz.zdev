/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile.test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import de.tgmz.zdev.compile.ItemOptionsDialog;
import de.tgmz.zdev.domain.Item;

public class ItemOptionsDialogDisplay {
	@Test
	public void test() {
		Shell shell = new Shell((Display) null);
		
		ItemOptionsDialog d = new ItemOptionsDialog(shell, new Item());
		
		d.open();
	}
}
