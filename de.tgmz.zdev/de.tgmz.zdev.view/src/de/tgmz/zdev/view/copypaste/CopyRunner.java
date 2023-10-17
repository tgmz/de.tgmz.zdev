/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.copypaste;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.SequentialDataSet;

import de.tgmz.zdev.connection.ZdevConnectable;

/**
 * Copies the content of a DataEntry to TransferUtility.
 */
public class CopyRunner implements IRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(CopyRunner.class);
	private IStructuredSelection source;
	
	public CopyRunner(IStructuredSelection source) {
		super();
		this.source = source;
	}
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		// Convert the given monitor into a SubMonitor instance. We shouldn't use the original
		// monitor object again since subMonitor will consume the entire monitor.
		SubMonitor subMonitor = SubMonitor.convert(monitor, 100);

		subMonitor.beginTask("Copy", source.size());
		
		TransferUtility.getInstance().reset();
		
		for (Object o : source.toArray()) {
			if (o instanceof Member || o instanceof SequentialDataSet) {
				DataEntry de = (DataEntry) o;
				
				try (ByteArrayOutputStream contents = ZdevConnectable.getConnectable().getContents(de, FileType.BINARY)) {
					if (contents != null) {
						TransferUtility.getInstance().put(de.getName(), contents.toByteArray());
					} else {
						LOG.error("Content {} is null", de);
					}
				} catch (ConnectionException | IOException e) {
					LOG.error("Error getting contents of {}", de.getName(), e);
					
					return;
				}
			}
			
			subMonitor.worked(1);
		}
		
		subMonitor.done();
	}
}
