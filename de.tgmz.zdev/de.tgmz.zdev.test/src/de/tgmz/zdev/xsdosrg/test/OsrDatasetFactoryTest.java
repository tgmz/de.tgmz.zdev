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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionConfiguration;
import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.core.comm.IConnection;
import com.ibm.cics.zos.model.DataSetBuilder;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.UnsupportedOperationException;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.xsdosrg.OsrDataSetFactory;

public class OsrDatasetFactoryTest {
	private static IZOSConnectable origin;
	private static IZOSConnectable connectable;
	
	@BeforeClass
	public static void setupOnce() throws IOException {
		origin = ZdevConnectable.getConnectable();
		
		ConnectionConfiguration cc = Mockito.mock(ConnectionConfiguration.class);
		Mockito.when(cc.getUserID()).thenReturn("userid");

		IConnection connection = Mockito.mock(IConnection.class);
		Mockito.when(connection.getConfiguration()).thenReturn(cc);
		
		connectable = Mockito.mock(IZOSConnectable.class);
		Mockito.when(connectable.getDataSet(anyString())).thenThrow(FileNotFoundException.class);
		Mockito.when(connectable.create(any(DataSetBuilder.class))).thenReturn(Mockito.mock(PartitionedDataSet.class));
		Mockito.when(connectable.getConnection()).thenReturn(connection);
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test(expected = None.class)
	public void testAddAnnotation() throws UpdateFailedException, PermissionDeniedException, UnsupportedOperationException, ConnectionException {
		OsrDataSetFactory dsf = OsrDataSetFactory.getInstance();
		
		dsf.create("MBR");
	}
}
