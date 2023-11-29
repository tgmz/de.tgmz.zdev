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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import de.tgmz.zdev.compile.BindSelectionAdapter;
import de.tgmz.zdev.compile.CicsSelectionAdapter;
import de.tgmz.zdev.compile.CompileSelectionAdapter;
import de.tgmz.zdev.compile.Db2SelectionAdapter;
import de.tgmz.zdev.domain.Item;

@RunWith(value = Parameterized.class)
public class SelectionAdapterTest {
	private static final String HLQ_PLI = "HLQ.PLI";
	private static final String HLQ_COB = "HLQ.COB";
	private static final String HLQ_ASM = "HLQ.ASSEMBLE";
	private static final String HLQ_C = "HLQ.C";
	private static final String HLQ_CPP = "HLQ.CPP";
	private static final String HLQ_DUMMY = "HLQ.DUMMY";
	private String dsn;
	private Class<SelectionAdapter> clz;
	
	public SelectionAdapterTest(String dsn, Class<SelectionAdapter> clz) {
		super();
		this.dsn = dsn;
		this.clz = clz;
	}
	@Test(expected = None.class)
	public void test() throws NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Shell shell = new Shell((Display) null);
		
		Item item = new Item(dsn, "");
		
		Button b = new Button(shell, SWT.CHECK);
		b.setSelection(true);
		
		Text t = new Text(shell, SWT.None);
		
		Constructor<SelectionAdapter> ctr = clz.getConstructor(Item.class, Button.class, Text.class);
		
		SelectionAdapter sa = ctr.newInstance(item, b, t);
		
		sa.widgetSelected(null);
	}
	@Parameters(name = "{index}: {0} {1}")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
			{ HLQ_PLI, CompileSelectionAdapter.class },
			{ HLQ_COB, CompileSelectionAdapter.class },
			{ HLQ_ASM, CompileSelectionAdapter.class },
			{ HLQ_C, CompileSelectionAdapter.class },
			{ HLQ_CPP, CompileSelectionAdapter.class },
			{ HLQ_DUMMY, CompileSelectionAdapter.class },
			{ HLQ_PLI, Db2SelectionAdapter.class },
			{ HLQ_COB, Db2SelectionAdapter.class },
			{ HLQ_ASM, Db2SelectionAdapter.class },
			{ HLQ_C, Db2SelectionAdapter.class },
			{ HLQ_CPP, Db2SelectionAdapter.class },
			{ HLQ_DUMMY, Db2SelectionAdapter.class },
			{ HLQ_PLI, CicsSelectionAdapter.class },
			{ HLQ_COB, CicsSelectionAdapter.class },
			{ HLQ_ASM, CicsSelectionAdapter.class },
			{ HLQ_C, CicsSelectionAdapter.class },
			{ HLQ_CPP, CicsSelectionAdapter.class },
			{ HLQ_DUMMY, CicsSelectionAdapter.class },
			{ "", BindSelectionAdapter.class },
			};
		return Arrays.asList(data);
	}
}
