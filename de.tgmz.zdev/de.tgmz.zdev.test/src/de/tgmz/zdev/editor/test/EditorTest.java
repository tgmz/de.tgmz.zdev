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

import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchWindow;
import org.junit.BeforeClass;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.editor.ZdevEditor;

public abstract class EditorTest {
	protected static ZdevEditor editor;
	protected static IEditorSite site;
	protected Member member = Mockito.mock(Member.class);

	public EditorTest() {
		super();
	}

	@BeforeClass
	public static void setupOnce() {
		editor = new ZdevEditor();
		
		// The parameters for init()
		site = Mockito.mock(IEditorSite.class);
		
		// Mock some methods of the parameters
		IWorkbenchWindow ww = Mockito.mock(IWorkbenchWindow.class);
		IPartService ps = Mockito.mock(IPartService.class);

		Mockito.when(ww.getPartService()).thenReturn(ps);
		Mockito.when(site.getWorkbenchWindow()).thenReturn(ww);
		Mockito.when(site.getId()).thenReturn("");
	}
}
