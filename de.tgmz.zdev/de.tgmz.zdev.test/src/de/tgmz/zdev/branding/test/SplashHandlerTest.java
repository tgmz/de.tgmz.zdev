/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.branding.test;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Before;
import org.junit.Test;

import de.tgmz.zdev.branding.CustomSplashHandler;

public class SplashHandlerTest {
	private Shell shell;
	
	@Before
	public void setup() {
		shell = new Shell((Display) null);
	}
	
	@Test
	public void testSplash() {
		new CustomSplashHandler().init(shell);
		
		shell.dispose();
	}
}
