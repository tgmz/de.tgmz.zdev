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

import java.util.Collections;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;

import de.tgmz.zdev.transfer.upload.HfsUploadRunner;

public class HfsUploadRunnerTest {
	@Test
	public void uploadFile() {
		IFile iFile = Mockito.mock(IFile.class);
		Mockito.when(iFile.getName()).thenReturn("HELLOW.pli");
		
		HFSFolder hfsFolder = Mockito.mock(HFSFolder.class);
		Mockito.when(hfsFolder.createFile(anyString())).thenReturn(Mockito.mock(HFSFile.class));
		
		new HfsUploadRunner(hfsFolder, Collections.singletonList(iFile), FileType.BINARY).run(new NullProgressMonitor());
	}
	@Test
	public void uploadFolder() throws CoreException {
		IFolder iFolder = Mockito.mock(IFolder.class);
		Mockito.when(iFolder.members()).thenReturn(new IResource[] {Mockito.mock(IFile.class)});
		
		HFSFolder hfsFolder = Mockito.mock(HFSFolder.class);
		HFSFolder subFolder = Mockito.mock(HFSFolder.class);
		Mockito.when(subFolder.createFile(any())).thenReturn(Mockito.mock(HFSFile.class));
		Mockito.when(hfsFolder.createChildFolder(any())).thenReturn(subFolder);
		
		new HfsUploadRunner(hfsFolder, Collections.singletonList(iFolder), FileType.BINARY).run(new NullProgressMonitor());
	}
}
