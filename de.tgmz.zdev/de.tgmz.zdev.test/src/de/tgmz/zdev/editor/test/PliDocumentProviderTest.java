/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.common.util.IOUtils;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.editor.pli.PLIDocumentProvider;

/**
 * PliDocumentProviderTest. 
 */
public class PliDocumentProviderTest {
	@Test
	public void test() throws CoreException, IOException {
		ZOSObjectEditorInput editorInput = Mockito.mock(ZOSObjectEditorInput.class);
		Mockito.when(editorInput.getContents()).thenReturn(IOUtils.readInputStreamAsString(this.getClass().getClassLoader().getResourceAsStream("testresources/HELLOW.pli"), true, "Cp1252"));
		
		assertNotNull(new PLIDocumentProvider().createDocument(editorInput));
	}
}
