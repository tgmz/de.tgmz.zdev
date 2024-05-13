/*********************************************************************
* Copyright (c) 12.04.2024 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.zowe.test;

import org.junit.Test;
import org.junit.Test.None;

import de.tgmz.zdev.zowe.connection.ZoweConnection;

public class ZoweConnectionTest {
	@Test(expected = None.class)
	public void testConnection() {
		new ZoweConnection();
	}
}
