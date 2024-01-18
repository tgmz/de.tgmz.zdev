/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.operation;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.IJob.JobCompletion;
import com.ibm.cics.zos.model.IJobDetails;
import com.ibm.cics.zos.model.JobNotFoundException;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Foreground task based on a job running on a mainframe.
 */

public class JCLOperationRunnable implements IRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(JCLOperationRunnable.class);
	/** Wait time. */
	private static final int WAIT_TIME = 1000;
	/** Interval for the progress monitor. */
	private static final int WORK_INTERVAL = 50;
	/** The job control. */
	private final String jcl;
	private IJobDetails jobd;
    /**
     * Constructor.
     * 
     * @param aJclString the JCL
     */
    public JCLOperationRunnable(final String aJclString) {
		super();
		this.jcl = aJclString;
	}
    /**
     * Constructor.
     * 
     * @param aJclList the JCL as a string list
     */
    public JCLOperationRunnable(final List<String> aJclList) {
		super();
		
    	StringBuilder jclBuffer = new StringBuilder();
    	
    	for (String s : aJclList) {
			jclBuffer.append(s);
			jclBuffer.append(System.lineSeparator());
		}
    	
    	jcl = jclBuffer.toString();
    	
	}
	/** {@inheritDoc} */
	@Override
	public void run(final IProgressMonitor pm) throws InvocationTargetException {
    	LOG.debug("run");
    	
    	IProgressMonitor subMonitor = SubMonitor.convert(pm, 100);
    	
        try {
        	subMonitor.beginTask(Activator.getString("JCLOperationRunnable.running"), WORK_INTERVAL * 2);

        	subMonitor.worked(WORK_INTERVAL);

			jobd = ZdevConnectable.getConnectable().submitJob(jcl);

            boolean jobFinished = false;
            
            subMonitor.worked(WORK_INTERVAL);

			while (!jobFinished) {
	            try { 	// Wait one second: Sometimes the ParsingUtility overtakes the JES initiator 
	            	Thread.sleep(WAIT_TIME);
	            } catch (InterruptedException e1) {
	    			LOG.error("InterruptedException", e1);

	    			Thread.currentThread().interrupt();
	            }

				JobCompletion rc = ZdevConnectable.getConnectable().getJob(jobd.getId()).getCompletion();
				
				jobFinished = (rc != JobCompletion.ACTIVE && rc != JobCompletion.INPUT);
	            
	            subMonitor.worked(1);
			}

			subMonitor.worked(WORK_INTERVAL);
   		} catch (ConnectionException | PermissionDeniedException | JobNotFoundException e) {
			LOG.error("Exception", e);
       	} finally {
       		subMonitor.done();
       	}
    }
	public IJobDetails getJobDetails() {
		return jobd;
	}
}

