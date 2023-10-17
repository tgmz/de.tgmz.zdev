/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class ActivatorTest {
	@Test
	public void testActivator() {
		de.tgmz.zdev.xsdosrg.Activator activator = de.tgmz.zdev.xsdosrg.Activator.getDefault();
		
		assertNotNull(activator);
		assertNotNull(activator.getString("qwertz"));
	}
}
