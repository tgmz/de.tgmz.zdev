/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.folding.test;

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.ISources;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.editor.folding.CollapseHandler;
import de.tgmz.zdev.editor.folding.ExpandHandler;

/**
 * Test cases for folding
 */
public class FoldingHandlerTest {
	private static ExecutionEvent event;
	
	@BeforeClass
	public static void setupOnce() {
		IEvaluationContext context = new EvaluationContext( null, new Object() );
		Map<String, String> parameters = new HashMap<>();
		event = new ExecutionEvent( null, parameters, null, context );

		Member m = Mockito.mock(Member.class);
		ZOSObjectEditorInput editorInput = Mockito.mock(ZOSObjectEditorInput.class);
		Mockito.when(editorInput.getZOSObject()).thenReturn(m);
		
		ITextSelection mock = Mockito.mock(ITextSelection.class);
		Mockito.when(mock.getText()).thenReturn("PROC");
		
		context.addVariable( ISources.ACTIVE_EDITOR_INPUT_NAME, editorInput);
		context.addVariable( ISources.ACTIVE_MENU_SELECTION_NAME, mock);
	}
	@Test
	public void testCollapse() {
		assertNull(new CollapseHandler().execute(event));
	}
	@Test
	public void testExpand() {
		assertNull(new ExpandHandler().execute(event));
	}
}
