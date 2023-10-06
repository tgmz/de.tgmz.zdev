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

import static org.junit.Assert.assertSame;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.IZOSConnectable;

import de.tgmz.zdev.connection.ZdevConnectable;

public class MockConnectableTest {
	private static IZOSConnectable mock = Mockito.mock(IZOSConnectable.class);
	private static IZOSConnectable origin;

	@BeforeClass
	public static void setupOnce() {
		origin = ZdevConnectable.getConnectable();
		ZdevConnectable.setConnectable(mock);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test
	public void testMock() {
		assertSame(mock, ZdevConnectable.getConnectable());
	}
}
