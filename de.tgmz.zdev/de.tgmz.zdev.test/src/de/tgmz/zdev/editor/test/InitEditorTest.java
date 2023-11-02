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

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

@RunWith(value = Parameterized.class)
public class InitEditorTest extends EditorTest {
	private String datasetname;

	public InitEditorTest(String datasetname) {
		super();
		this.datasetname = datasetname;
	}
	@Test
	public void testInit() throws CoreException {
		Mockito.when(member.getParentPath()).thenReturn(datasetname);
		
		IActionBars actionBars = Mockito.mock(IActionBars.class);
		IStatusLineManager statusLineManager = Mockito.mock(IStatusLineManager.class);
		
		Mockito.when(actionBars.getStatusLineManager()).thenReturn(statusLineManager);
		Mockito.when(site.getActionBars()).thenReturn(actionBars);
		
		IEditorInput input = new TestZOSObjectEditorInput(member);
		((TestZOSObjectEditorInput) input).setContent("");
		
		editor.init(site, input);
		editor.getDocumentProvider().connect(input);
		editor.setInput(input);
		editor.dispose();
	}

	@Parameters(name = "{index}: Editor for [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ "TEST.PLI"},
				{ "TEST.CBL"},
				{ "TEST.JCL"},
				{ "TEST.SQL"},
				{ "TEST.ASSEMBLE"},
				{ "TEST.REXX"},
				{ "TEST.C"},
				{ "TEST.XX"},
				};
		return Arrays.asList(data);
	}
}
