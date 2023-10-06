/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.connection.test;

import static org.junit.Assert.assertFalse;

import org.eclipse.core.resources.IFolder;
import org.junit.Test;
import org.mockito.Mockito;

import de.tgmz.zdev.connection.ConnectedTester;

public class ConnectedTesterTest {
	@Test
	public void test() {
		ConnectedTester tester = new ConnectedTester();
		
		assertFalse(tester.test(Mockito.mock(IFolder.class), "isConnected", null, null));
	}
}
