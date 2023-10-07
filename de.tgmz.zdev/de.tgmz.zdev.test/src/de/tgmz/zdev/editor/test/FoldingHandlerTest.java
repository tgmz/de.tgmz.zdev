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

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.text.BlockTextSelection;
import org.eclipse.ui.ISources;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.editor.folding.CollapseHandler;
import de.tgmz.zdev.editor.folding.ExpandHandler;

public class FoldingHandlerTest {
	private static ExecutionEvent event;

	@BeforeClass
	public static void setupOnce() {
		IEvaluationContext context = new EvaluationContext(null, new Object());

		context.addVariable(ISources.ACTIVE_MENU_SELECTION_NAME, new BlockTextSelection(new TestDocument("qwertzuiop"), 0, 0, 0, 1, 0));
		context.addVariable(ISources.ACTIVE_EDITOR_INPUT_NAME, new TestZOSObjectEditorInput(Mockito.mock(Member.class)));
		
		Map<String, String> parameters = new HashMap<>();
		event = new ExecutionEvent(null, parameters, null, context);
	}
	@Test(expected = None.class)
	public void testExpand() {
		new ExpandHandler().execute(event);
	}
	@Test(expected = None.class)
	public void testCollapse() {
		new CollapseHandler().execute(event);
	}
}
