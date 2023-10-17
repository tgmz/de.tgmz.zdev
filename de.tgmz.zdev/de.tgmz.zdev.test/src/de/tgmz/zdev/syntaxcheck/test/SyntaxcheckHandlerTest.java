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

import static org.junit.Assert.assertNull;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.junit.Test;

import de.tgmz.zdev.syntaxcheck.SyntaxcheckHandler;

public class SyntaxcheckHandlerTest {
	@Test
	public void run() throws ExecutionException {
		IEvaluationContext context = new EvaluationContext(null, new Object());
		Map<String, String> parameters = new HashMap<>();
		ExecutionEvent event = new ExecutionEvent(null, parameters, null, context);

		assertNull(new SyntaxcheckHandler().execute(event));
	}
}
