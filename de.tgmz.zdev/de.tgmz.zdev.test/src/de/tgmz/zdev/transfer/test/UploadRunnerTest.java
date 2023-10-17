/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.transfer.test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.PartitionedDataSet;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.transfer.upload.UploadRunner;

public class UploadRunnerTest {
	private static IZOSConnectable origin;
	private static PartitionedDataSet pds;
	private static IFile file;
	
	@BeforeClass
	public static void setupOnce() throws IOException, ConnectionException {
		origin = ZdevConnectable.getConnectable();
		
		file = Mockito.mock(IFile.class);
		Mockito.when(file.getName()).thenReturn("HELLOW.pli");
		Mockito.when(file.getFileExtension()).thenReturn("pli");
		
		pds = Mockito.mock(PartitionedDataSet.class);
		
		IZOSConnectable connectable = Mockito.mock(IZOSConnectable.class);
		Mockito.when(connectable.getDataSetMember(any(), anyString())).thenThrow(FileNotFoundException.class);
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test
	public void run() {
		new UploadRunner(pds, Collections.singletonList(file)).run(new NullProgressMonitor());
	}
}
