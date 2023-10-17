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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.model.AdaptableList;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.common.util.IOUtils;
import com.ibm.cics.zos.ui.editor.ZEditor;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.outline.view.ZdevContentOutlinePage;
import de.tgmz.zdev.preferences.Language;

public class OutlineTest {
	private static ZdevContentOutlinePage outlinePage;
	private static ZOSObjectEditorInput editorInput;
	
	@BeforeClass
	public static void setupOnce() {
		outlinePage = new ZdevContentOutlinePage(new ZEditor(), Language.PLI);
		editorInput = Mockito.mock(ZOSObjectEditorInput.class);
	}
	
	private Object[] getMarkElements(String pgm) throws IOException {
		String s = IOUtils.readInputStreamAsString(this.getClass().getClassLoader().getResourceAsStream("testresources/" + pgm)
				, true, "Cp1252");
		
		Mockito.when(editorInput.getContents()).thenReturn(s);

		IAdaptable contentOutline = outlinePage.getContentOutline(editorInput);
		
		return ((AdaptableList) contentOutline).getChildren();
	}

	@Test
	public void testHellow() throws IOException {
		assertEquals("MarkElement(PRINTLN: PROCEDURE();)", getMarkElements("HELLOW.pli")[3].toString());
	}
}
