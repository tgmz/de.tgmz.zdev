/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg.test;

import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.IJob.JobCompletion;
import com.ibm.cics.zos.model.IJobDetails;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.Job;
import com.ibm.cics.zos.model.JobStep;
import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.connection.ZdevConnectable;

public class XsdosrgHandlerTest {
	private static IZOSConnectable origin;
	
	@BeforeClass
	public static void setupOnce() throws IOException, ConnectionException {
		origin = ZdevConnectable.getConnectable();
		
		IZOSConnectable connectable = Mockito.mock(IZOSConnectable.class);
		
		IJobDetails jd = Mockito.mock(IJobDetails.class);
		Mockito.when(jd.getId()).thenReturn("JOB12345");
		
		Job job = Mockito.mock(Job.class);
		Mockito.when(job.getCompletion()).thenReturn(JobCompletion.NORMAL);
		
		JobStep step = Mockito.mock(JobStep.class);
		
		List<JobStep> steps = new LinkedList<>();
		steps.add(null);
		steps.add(null);
		steps.add(null);
		steps.add(step);
		
		Mockito.when(connectable.submitJob(anyString())).thenReturn(jd);
		Mockito.when(connectable.getSteps(job)).thenReturn(steps);
		Mockito.when(connectable.getSpool(step)).thenReturn(new ByteArrayOutputStream());
		Mockito.when(connectable.getJob(anyString())).thenReturn(job);
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}

	@Test
	public void run() throws ExecutionException {
		HFSFile schema = Mockito.mock(HFSFile.class);
		Mockito.when(schema.getParent()).thenReturn(Mockito.mock(HFSFolder.class));
		
		IEvaluationContext context = new EvaluationContext( null, new Object() );
		Map<String, String> parameters = new HashMap<>();
		ExecutionEvent event = new ExecutionEvent( null, parameters, null, context );

		context.addVariable( ISources.ACTIVE_CURRENT_SELECTION_NAME, new StructuredSelection(schema));
        
		assertNull(new TestXsdosrgHandler(Mockito.mock(Member.class)).execute(event));
	}
}
