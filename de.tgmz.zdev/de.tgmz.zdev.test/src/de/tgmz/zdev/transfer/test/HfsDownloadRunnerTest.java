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

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.model.HFSEntry;
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IZOSConnectable;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.transfer.download.HfsDownloadRunner;

public class HfsDownloadRunnerTest {
	private static IZOSConnectable origin;
	private static IZOSConnectable connectable;
	private static final IZOSConstants.FileType transfermode = IZOSConstants.FileType.ASCII;
	
	@BeforeClass
	public static void setupOnce() throws IOException, ConnectionException {
		origin = ZdevConnectable.getConnectable();
		
		connectable = Mockito.mock(IZOSConnectable.class);
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test()
	public void downloadFile() throws IOException, InvocationTargetException, InterruptedException {
		HFSFile hfsFile = Mockito.mock(HFSFile.class);
		
		Mockito.when(connectable.getContents(hfsFile, transfermode)).thenReturn(new ByteArrayOutputStream());
		
		IFolder iFolder = Mockito.mock(IFolder.class);
		Mockito.when(iFolder.getFile((String) any())).thenReturn(Mockito.mock(IFile.class));
		
		HfsDownloadRunner downloadRunner = new HfsDownloadRunner(iFolder, Collections.singletonList(hfsFile), transfermode);
		
		downloadRunner.run(new NullProgressMonitor());
		
		assertNotNull(downloadRunner.getDestination());
	}
	
	@Test()
	public void downloadFolder() throws IOException, InvocationTargetException, InterruptedException, ConnectionException {
		HFSFolder hfsFolder = Mockito.mock(HFSFolder.class);

		Mockito.when(connectable.getChildren(hfsFolder, true)).thenReturn(Collections.singletonList(Mockito.mock(HFSEntry.class)));

		IFolder iFolder = Mockito.mock(IFolder.class);
		Mockito.when(iFolder.getFolder((String) any())).thenReturn(Mockito.mock(IFolder.class));
		
		HfsDownloadRunner downloadRunner = new HfsDownloadRunner(iFolder, Collections.singletonList(hfsFolder), transfermode);
		
		downloadRunner.run(new NullProgressMonitor());
		
		assertNotNull(downloadRunner.getDestination());
	}
}
