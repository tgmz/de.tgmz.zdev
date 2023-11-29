/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile.test;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.zdev.compile.ItemOptionsDialog;
import de.tgmz.zdev.domain.Item;

@RunWith(value = Parameterized.class)
public class ItemOptionsDialogTest {
	private String compOption;
	private String db2Option;
	private String cicsOption;
	private String bindOption;

	public ItemOptionsDialogTest(String comp, String db2, String cics, String bind) {
		super();
		this.compOption = comp;
		this.db2Option = db2;
		this.cicsOption = cics;
		this.bindOption = bind;
	}
	@Test(expected = None.class)
	public void executeTest() {
		Item item = new Item();
		
		item.getOption().setComp(compOption != null);
		item.getOption().setCompOption(compOption);
		item.getOption().setDb2(db2Option != null);
		item.getOption().setDb2Option(db2Option);
		item.getOption().setCics(cicsOption != null);
		item.getOption().setCicsOption(cicsOption);
		item.getOption().setBind(bindOption != null);
		item.getOption().setBindOption(bindOption);
		
		new ItemOptionsDialog(new Shell ((Display) null), item);
	}
	@Parameters(name = "{index}: {0} {1}, {2}")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
			{ "SYSTEM(MVS)", "PP(DB2)", "PP(CICS)", "REUS" },
			{ null, null, null, null},
			};
		return Arrays.asList(data);
	}
}
