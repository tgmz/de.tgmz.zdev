/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.connection.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import de.tgmz.zdev.connection.ZdevConnectable;

public class ConnectionTest {
	@Test
	public void getTest() {
		assertNotNull(ZdevConnectable.getConnectable());
	}
}
