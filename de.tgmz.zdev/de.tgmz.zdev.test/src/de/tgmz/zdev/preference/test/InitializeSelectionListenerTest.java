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

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;

import de.tgmz.zdev.preferences.InitializeSelectionListener;

public class InitializeSelectionListenerTest {
	private static InitializeSelectionListener isl;
	
	@BeforeClass
	public static void setupOnce() {
		isl = new InitializeSelectionListener("foo");
	}
	
	@Test(expected = None.class)
	public void run() {
		Event event = new Event();
		event.widget = new Shell();
		
		SelectionEvent se = new SelectionEvent(event);
		
		isl.widgetSelected(se);
		isl.widgetDefaultSelected(se);
	}
}
