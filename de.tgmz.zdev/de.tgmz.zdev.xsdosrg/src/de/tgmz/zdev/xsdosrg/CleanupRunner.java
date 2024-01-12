/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.xsdosrg;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Removes a HFS folder.
 */
public class CleanupRunner implements IRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(CleanupRunner.class);
	private HFSFolder destination;
	private SubMonitor subMonitor;

	public CleanupRunner(HFSFolder destination) {
		super();
		this.destination = destination;
	}

	@Override
	public void run(final IProgressMonitor pm) {
		subMonitor = SubMonitor.convert(pm, 1);

		subMonitor.subTask(Activator.getDefault().getString("Cleanup.Subtask", destination.getPath()));
		
		try {
			ZdevConnectable.getConnectable().delete(destination);
		} catch (UpdateFailedException e) {
			LOG.error("Cannot remove temp directory", e);
		}
		
		subMonitor.done();
	}
}
