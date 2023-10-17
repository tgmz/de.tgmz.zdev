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
import org.junit.Test.None;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.IZOSConnectable;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.preferences.Language;
import de.tgmz.zdev.transfer.download.DownloadRunner;

public class DownloadRunnerTest {
	private static IZOSConnectable origin;
	private static DataEntry member;
	private static IFolder folder;
	
	@BeforeClass
	public static void setupOnce() throws IOException, ConnectionException {
		origin = ZdevConnectable.getConnectable();
		
		member = Mockito.mock(DataEntry.class);
		Mockito.when(member.getParentPath()).thenReturn("HLQ.PLI");
		Mockito.when(member.getName()).thenReturn("HELLOW");

		folder = Mockito.mock(IFolder.class);
		Mockito.when(folder.getFile(member.getName() + Language.fromDatasetName(member.getParentPath()).getExtension())).thenReturn(Mockito.mock(IFile.class));

		IZOSConnectable connectable = Mockito.mock(IZOSConnectable.class);
		Mockito.when(connectable.getContents(member, FileType.EBCDIC)).thenReturn(new ByteArrayOutputStream());
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test(expected = None.class)
	public void run() throws InvocationTargetException, InterruptedException {
		DownloadRunner downloadRunner = new DownloadRunner(folder, Collections.singletonList(member));
		
		downloadRunner.run(new NullProgressMonitor());
	}
}
