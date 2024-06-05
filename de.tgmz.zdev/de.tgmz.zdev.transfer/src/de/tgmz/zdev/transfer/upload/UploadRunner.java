/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.transfer.upload;

import java.io.FileNotFoundException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.SkipIgnoreAbortMessageDialog;
import de.tgmz.zdev.view.YesNoAllNoneCancelMessageDialog;

/**
 * Copies list of local files to a partitioned dataset.
 */
public class UploadRunner implements IRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(UploadRunner.class);
	private PartitionedDataSet destination;
	private List<IFile> files;
	private int overwriteStatus = IDialogConstants.NO_ID;
	private int errorStatus = IDialogConstants.NO_ID;

	public UploadRunner(PartitionedDataSet destination, List<IFile> files) {
		super();
		this.destination = destination;
		this.files = files;
	}

	@Override
	public void run(final IProgressMonitor pm) {
		SubMonitor subMonitor = SubMonitor.convert(pm, files.size());

		subMonitor.beginTask("Upload", files.size());

		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();

		for (int i = 0; i < files.size() && overwriteStatus != IDialogConstants.CANCEL_ID && errorStatus != IDialogConstants.ABORT_ID; i++) {
			IFile de = files.get(i);
			
			Member m;
			
			String name = de.getName();
			String fe = de.getFileExtension();
			
			if (fe != null) {
				name = name.substring(0, name.indexOf(fe) - 1);
			}
			
			subMonitor.subTask(Activator.getDefault().getString("Upload.Subtask", name));
			
			try {
				m = ZdevConnectable.getConnectable().getDataSetMember(destination, name);
				
				// No FileNotFoundException occurred: The member exists
				if (overwriteStatus == IDialogConstants.NO_TO_ALL_ID) {
					subMonitor.worked(1);
					
					continue;
				}
				
				if (overwriteStatus != IDialogConstants.YES_TO_ALL_ID) {
					overwriteStatus = new YesNoAllNoneCancelMessageDialog(shell, m.getName()).open();
					
					if (overwriteStatus == IDialogConstants.NO_ID
					 || overwriteStatus == IDialogConstants.CANCEL_ID) {
						continue;
					}
				}
			} catch (FileNotFoundException e) {
				m = (Member) DataEntry.newFrom(destination.getFullPath() + '(' + name + ')', ZdevConnectable.getConnectable());
			} catch (PermissionDeniedException e) {
				LOG.error("Cannot create member {}", name, e);
				
				if (errorStatus != IDialogConstants.IGNORE_ID) {
					errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
				}
				
				continue;
			} catch (ConnectionException e) {
				String s  = e.getMessage();
				
				if (s != null && s.contains("no active connection")) {
					LOG.error(s);
				} else {
					LOG.error("Error in connection", e);
				}
				
				break;
			}
			
            try {
				ZdevConnectable.getConnectable().save(m, de.getContents());
			} catch (UpdateFailedException | CoreException e) {
				LOG.error("Cannot write member {}", name, e);
				
				if (errorStatus != IDialogConstants.IGNORE_ID) {
					errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
				}
			}
            
            subMonitor.worked(1);
		}
		
		subMonitor.done();
	}
}
