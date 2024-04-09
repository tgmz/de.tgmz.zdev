/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.plicomp.test;

import static org.junit.Assert.assertEquals;

import java.io.InputStream;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionConfiguration;
import com.ibm.cics.core.comm.IConnection;
import com.ibm.cics.zos.model.IZOSConnectable;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.plicomp.PlicompConfigurationException;
import de.tgmz.zdev.plicomp.PlicompException;
import de.tgmz.zdev.plicomp.PlicompFactory;
import de.tgmz.zdev.xinfo.generated.PACKAGE;


public class PlicompFactoryTest {
	private static PlicompFactory pf;
	private static IZOSConnectable origin;

	@BeforeClass
	public static void setupOnce() throws PlicompConfigurationException {
		pf = PlicompFactory.getInstance();
		
		origin = ZdevConnectable.getConnectable();
		
		ConnectionConfiguration config = Mockito.mock(ConnectionConfiguration.class);
				
		IConnection connection = Mockito.mock(IConnection.class);
		Mockito.when(connection.getConfiguration()).thenReturn(config);
		
		IZOSConnectable connectable = Mockito.mock(IZOSConnectable.class);
		Mockito.when(connectable.getConnection()).thenReturn(connection);
		
		ZdevConnectable.setConnectable(connectable);
	}
	
	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test
	public void test() throws PlicompException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("testresources/HELLOW.xml");
		
		assertEquals(1, pf.getPlicomp(is, "").getMESSAGE().size()); // Der Datasetname interessiert nicht
	}
	@Test
	public void testSysevent() throws PlicompException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("testresources/HELLOW.sysevent");
		
		PACKAGE plicomp = pf.getPlicomp(is, "");
		
		assertEquals(1, plicomp.getMESSAGE().size());
		assertEquals("9", plicomp.getFILEREFERENCETABLE().getFILECOUNT());
	}
	@Test(expected=PlicompException.class)
	public void fail() throws PlicompException {
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("testresources/empty.xml");
		
		pf.getPlicomp(is, "");
	}
}
