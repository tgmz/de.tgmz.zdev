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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.local.editors.LocalZdevEditor;

@RunWith(value = Parameterized.class)
public class LocalZdevEditorTest {
	private static IEditorSite site;
	private IEditorInput input;
	private String member;
	
	public LocalZdevEditorTest(String member) {
		super();
		this.member = member;
	}
	
	@BeforeClass
	public static void setupOnce() {
		site = Mockito.mock(IEditorSite.class);
		
		// Mock some methods of the parameters
		IWorkbenchWindow ww = Mockito.mock(IWorkbenchWindow.class);
		IPartService ps = Mockito.mock(IPartService.class);

		Mockito.when(ww.getPartService()).thenReturn(ps);
		Mockito.when(site.getWorkbenchWindow()).thenReturn(ww);
		Mockito.when(site.getId()).thenReturn("");
	}
	@Test
	public void run() throws CoreException {
		LocalZdevEditor editor = new LocalZdevEditor();
		
		input = Mockito.mock(IEditorInput.class);
		Mockito.when(input.getName()).thenReturn(member);
		
		editor.init(site, input);
		editor.setInput(input);
		assertNotNull(editor.getAdapter(IContentOutlinePage.class));
		editor.dispose();
	}
	@Parameters(name = "{index}: Member {0}")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "HELLOW.pli" },
				{ "HELLOW.cbl" },
				{ "HELLOW.asm" },
				{ "HELLOW.sql" },
				{ "HELLOW.jcl" },
				};
		return Arrays.asList(data);
	}
}
