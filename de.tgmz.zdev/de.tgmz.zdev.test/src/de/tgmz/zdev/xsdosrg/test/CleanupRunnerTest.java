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

import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IZOSConnectable;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.xsdosrg.CleanupRunner;

public class CleanupRunnerTest {
	private static IZOSConnectable origin;
	
	@BeforeClass
	public static void setupOnce() {
		origin = ZdevConnectable.getConnectable();
		
		ZdevConnectable.setConnectable(Mockito.mock(IZOSConnectable.class));
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test(expected = None.class)
	public void testCleanup() {
		CleanupRunner runner = new CleanupRunner(Mockito.mock(HFSFolder.class)); 
		
		runner.run(new NullProgressMonitor());
	}
}
