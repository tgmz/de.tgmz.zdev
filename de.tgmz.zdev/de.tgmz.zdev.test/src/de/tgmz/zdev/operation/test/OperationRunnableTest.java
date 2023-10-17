/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.operation.test;

import static org.junit.Assert.assertSame;
import static org.mockito.ArgumentMatchers.anyString;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.IJob;
import com.ibm.cics.zos.model.IJob.JobCompletion;
import com.ibm.cics.zos.model.IJobDetails;
import com.ibm.cics.zos.model.IZOSConnectable;
import com.ibm.cics.zos.model.JobNotFoundException;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.operation.JCLOperationRunnable;

public class OperationRunnableTest {
	private static IZOSConnectable origin;
	private static IZOSConnectable connectable;
	
	@BeforeClass
	public static void setupOnce() {
		origin = ZdevConnectable.getConnectable();

		connectable = Mockito.mock(IZOSConnectable.class);
		
		ZdevConnectable.setConnectable(connectable);
	}

	@AfterClass
	public static void teardownOnce() {
		ZdevConnectable.setConnectable(origin);
	}
	
	@Test()
	public void run() throws InvocationTargetException, ConnectionException, JobNotFoundException, PermissionDeniedException {
		IJobDetails jobDetails = Mockito.mock(IJobDetails.class);
		Mockito.when(jobDetails.getId()).thenReturn("JOB12345");
		
		IJob job = Mockito.mock(IJob.class);
		Mockito.when(job.getCompletion()).thenReturn(JobCompletion.NORMAL);
		
		Mockito.when(connectable.submitJob(anyString())).thenReturn(jobDetails);
		Mockito.when(connectable.getJob(anyString())).thenReturn(job);
		
		IProgressMonitor pm = new NullProgressMonitor();
		
		JCLOperationRunnable tester;
		
		tester = new JCLOperationRunnable("");
		tester.run(pm);
		assertSame(jobDetails, tester.getJobDetails());
		
		tester = new JCLOperationRunnable(Collections.singletonList(""));
		tester.run(pm);
		assertSame(jobDetails, tester.getJobDetails());
	}
}
