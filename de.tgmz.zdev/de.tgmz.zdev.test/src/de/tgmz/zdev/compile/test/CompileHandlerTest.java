/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile.test;

import static org.mockito.ArgumentMatchers.anyString;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISources;
import org.hibernate.Session;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Test.None;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.IJobDetails;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.compile.CompileHandler;
import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.domain.Option;

@RunWith(value = Parameterized.class)
public class CompileHandlerTest {
	private static final String MBR = "HELLOW";
	private static IZOSConnectable origin;
	private String dsn;

	public CompileHandlerTest(String dsn) {
		super();
		this.dsn = dsn;
	}

	@BeforeClass
	public static void setupOnce() throws ConnectionException {
		origin = ZdevConnectable.getConnectable();
		
		IZOSConnectable mockConnection = Mockito.mock(IZOSConnectable.class);
		Mockito.when(mockConnection.submitJob(anyString())).thenReturn(Mockito.mock(IJobDetails.class));
		
		ZdevConnectable.setConnectable(mockConnection);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Before
	public void setup() {
		Session session = DbService.startTx();
		
		try {
			Item item = new Item(dsn, MBR);
			item.setLock(true);
			
			Option opt = new Option();
			opt.setBind(true);
			item.setOption(opt);
				
			session.persist(item);
		} finally {
			DbService.endTx(session);
		}
		
	}
	
	@After
	public void tearDown() {
		Session session = DbService.startTx();
		
		try {
			Item item = new Item(dsn, MBR);
				
			session.remove(item);
		} finally {
			DbService.endTx(session);
		}
		
	}
	
	@Test(expected = None.class)
	public void test() {
		new CompileHandler().execute(createSelectionEvent());
		new CompileHandler().execute(createEditorEvent());
		new CompileHandler().execute(new ExecutionEvent(null, new HashMap<>(), null, null)); // Enforce execution on a null member
	}
	
	private ExecutionEvent createSelectionEvent() {
		IEvaluationContext context = new EvaluationContext(null, new Object());
		context.addVariable(ISources.ACTIVE_CURRENT_SELECTION_NAME, new StructuredSelection(createMember()));
		
		return new ExecutionEvent(null, new HashMap<>(), null, context);
	}
	
	private ExecutionEvent createEditorEvent() {
		ZOSObjectEditorInput editorInput = Mockito.mock(ZOSObjectEditorInput.class);
		Member m = createMember();
		Mockito.when(editorInput.getZOSObject()).thenReturn(m);
		
		IEvaluationContext context = new EvaluationContext( null, new Object() );
		context.addVariable( ISources.ACTIVE_EDITOR_INPUT_NAME, editorInput);
		context.addVariable( ISources.ACTIVE_EDITOR_NAME, Mockito.mock(IEditorPart.class));
		
		return new ExecutionEvent(null, new HashMap<>(), null, context); 
	}
	
	private Member createMember() {
		Member m = Mockito.mock(Member.class);
		Mockito.when(m.getParentPath()).thenReturn(dsn);
		Mockito.when(m.getName()).thenReturn(MBR);
		
		return m;
	}

	@Parameters(name = "{index}: {0}")
	public static Collection<Object[]> data() {
		Object[][] data = new Object[][] {
			{ "HLQ.PLI" },
			{ "HLQ.COB" },
			{ "HLQ.ASSEMBLE" },
			{ "HLQ.C" },
			{ "HLQ.CPP" },
			};
		return Arrays.asList(data);
	}
}
