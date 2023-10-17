/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.syntaxcheck.test;

import java.util.Arrays;
import java.util.Collection;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import de.tgmz.zdev.editor.test.EditorTest;
import de.tgmz.zdev.editor.test.TestZOSObjectEditorInput;
import de.tgmz.zdev.syntaxcheck.AbstractSyntaxcheckHandler;

@RunWith(value = Parameterized.class)
public class AddAnnotationTest extends EditorTest {
	private int severity;
	private String lineNumber;

	public AddAnnotationTest(int severity, String lineNumber) {
		super();
		this.severity = severity;
		this.lineNumber = lineNumber;
	}

	@Test
	public void testAddAnnotation() throws CoreException {
		Mockito.when(member.getParentPath()).thenReturn("TEST.PLI");
		
		IEditorInput input = new TestZOSObjectEditorInput(member);
		
		editor.init(site, input);
		editor.getDocumentProvider().connect(input);
		editor.setInput(input);
		
		IMarker marker = Mockito.mock(IMarker.class);
		Mockito.when(marker.getAttribute(IMarker.LINE_NUMBER)).thenReturn(lineNumber);	//Yields BadLocationException but doesn't matter
		Mockito.when(marker.getAttribute(IMarker.SEVERITY)).thenReturn(severity);
		Mockito.when(marker.getAttribute(IMarker.MESSAGE)).thenReturn("Info");
		AbstractSyntaxcheckHandler.addCompilerMessageAnnotation(editor, marker);
		
		editor.dispose();
	}
	@Parameters(name = "{index}: Marker for [{0}]")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
				{ IMarker.SEVERITY_ERROR, "-1"},
				{ IMarker.SEVERITY_WARNING, "-1"},
				{ IMarker.SEVERITY_INFO, "-1"},
				{ IMarker.SEVERITY_INFO, "x"},
				};
		return Arrays.asList(data);
	}
}
