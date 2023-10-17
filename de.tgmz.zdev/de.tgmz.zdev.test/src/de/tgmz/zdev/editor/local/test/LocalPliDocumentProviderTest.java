/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.local.test;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.part.FileEditorInput;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.local.editors.LocalPLIDocumentProvider;

/**
 * LocalPliDocumentProviderTest. 
 */
public class LocalPliDocumentProviderTest {
	@Test
	public void test() throws CoreException, IOException {
		FileEditorInput editorInput = Mockito.mock(FileEditorInput.class);
		IFile file = Mockito.mock(IFile.class);
		
		Mockito.when(file.getContents(ArgumentMatchers.anyBoolean())).thenReturn(IOUtils.toInputStream("", StandardCharsets.UTF_8.name()));
		
		Mockito.when(editorInput.getFile()).thenReturn(file);
		
		assertNotNull(new LocalPLIDocumentProvider().createDocument(editorInput));
	}
}
