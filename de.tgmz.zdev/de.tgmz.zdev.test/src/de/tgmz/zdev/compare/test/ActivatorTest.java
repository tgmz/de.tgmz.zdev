/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compare.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;

public class ActivatorTest {
	@Test
	public void testActivator() throws Exception {
		de.tgmz.zdev.compare.Activator activator = de.tgmz.zdev.compare.Activator.getDefault();
		assertNotNull(activator);
		
		BundleContext mock = Mockito.mock(BundleContext.class);
		
		activator.start(mock);
		activator.stop(mock);
	}
}
