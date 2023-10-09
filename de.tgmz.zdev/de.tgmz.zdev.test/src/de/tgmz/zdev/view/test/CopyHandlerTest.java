/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.test;

import static org.junit.Assert.assertNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.ISources;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.view.copypaste.CopyHandler;

public class CopyHandlerTest {
	private static IZOSConnectable origin;
	private static Member member;
	
	@BeforeClass
	public static void setupOnce() throws IOException, ConnectionException {
		origin = ZdevConnectable.getConnectable();
		
		member = Mockito.mock(Member.class);

		IZOSConnectable connectable = Mockito.mock(IZOSConnectable.class);
		Mockito.when(connectable.getContents(member, FileType.BINARY)).thenReturn(new ByteArrayOutputStream());
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	

	@Test
	public void run() throws ExecutionException {
		IEvaluationContext context = new EvaluationContext( null, new Object() );
		Map<String, String> parameters = new HashMap<>();
		ExecutionEvent event = new ExecutionEvent( null, parameters, null, context );

		context.addVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME, new StructuredSelection(member));
		
		assertNull(new CopyHandler().execute(event));
	}
}
