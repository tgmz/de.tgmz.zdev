/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.UpperCaseListener;

/**
 * UpperCaseListenerTest. 
 */
public class UpperCaseListenerTest {
	@Test
	public void test() {
		KeyEvent ke = Mockito.mock(KeyEvent.class);
		ke.keyCode = SWT.F6;

		UpperCaseListener ucl = new UpperCaseListener();
		
		ucl.keyPressed(ke);
		ucl.keyReleased(ke);
		
		Event e = Mockito.mock(Event.class);
		e.widget = Mockito.mock(Widget.class);
		VerifyEvent ve = new VerifyEvent(e);
		ve.text = "";
		
		ucl.verifyText(ve);
	}
}
