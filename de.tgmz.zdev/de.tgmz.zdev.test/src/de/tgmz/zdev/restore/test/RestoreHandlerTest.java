/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore.test;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.restore.RestoreHandler;

public class RestoreHandlerTest {
	private static class Runner implements Runnable {
		private ExecutionEvent event;
		public Runner(ExecutionEvent event) {
			this.event = event;
		}
		@Override
		public void run() {
			new RestoreHandler().execute(event);
		}
	}
	@Test
	public void run() {
		IEvaluationContext context = new EvaluationContext(null, new Object());
		Map<String, String> parameters = new HashMap<>();
		ExecutionEvent event = new ExecutionEvent(null, parameters, null, context);

		context.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, new StructuredSelection(Mockito.mock(Member.class)));
		
		Thread t = new Thread(new Runner(event));
		
		t.start();
	}
}
