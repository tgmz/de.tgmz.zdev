/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.test;

import static org.junit.Assert.assertNull;

import org.eclipse.core.runtime.CoreException;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.DataEntry;

import de.tgmz.zdev.editor.ZdevEditor;

public class FindEditorTest {
	@Test
	public void testInit() throws CoreException {
		assertNull(ZdevEditor.findEditor(Mockito.mock(DataEntry.class), true));
	}
}
