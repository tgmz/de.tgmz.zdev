/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.syntaxcheck.test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionConfiguration;
import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.core.comm.IConnection;
import com.ibm.cics.zos.model.DataSet;
import com.ibm.cics.zos.model.DataSetBuilder;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.model.UnsupportedOperationException;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.syntaxcheck.TempDataSetFactory;

public class TempDatasetFactoryTest {
	private static IZOSConnectable origin;
	private static IZOSConnectable connectable;
	
	@BeforeClass
	@SuppressWarnings("deprecation")
	public static void setupOnce() throws UpdateFailedException {
		origin = ZdevConnectable.getConnectable();

		IConnection connection = Mockito.mock(IConnection.class);
		Mockito.when(connection.getConfiguration()).thenReturn(Mockito.mock(ConnectionConfiguration.class));
		Mockito.when(connection.getUserID()).thenReturn("userId");
		
		connectable = Mockito.mock(IZOSConnectable.class);
		Mockito.when(connectable.getConnection()).thenReturn(connection);
		Mockito.when(connectable.create(any(DataSetBuilder.class))).thenReturn(Mockito.mock(SequentialDataSet.class));
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test
	public void testAddAnnotation() throws UpdateFailedException, PermissionDeniedException, UnsupportedOperationException, ConnectionException {
		TempDataSetFactory dsf = TempDataSetFactory.getInstance();
		
		DataSet ds = dsf.createErrorFeedback();
		
		assertNotNull(ds);
		
		dsf.delete(ds);
		
		assertNotNull(dsf.createTempSrc("MBR"));
	}
}
