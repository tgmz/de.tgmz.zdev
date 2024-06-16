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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.SkipIgnoreAbortMessageDialog;
import de.tgmz.zdev.view.YesNoAllNoneCancelMessageDialog;

/**
 * Copies files and folder from the workspace to HFS.
 */
public class HfsUploadRunner implements IRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(HfsUploadRunner.class);
	private HFSFolder destination;
	private List<IResource> fileList;
	private IZOSConstants.FileType transferMode;
	private SubMonitor subMonitor;
	private int overwriteStatus = IDialogConstants.NO_ID;
	private int errorStatus = IDialogConstants.NO_ID;

	public HfsUploadRunner(HFSFolder destination, List<IResource> files, IZOSConstants.FileType transferMode) {
		super();
		this.destination = destination;
		this.fileList = files;
		this.transferMode = transferMode;
	}

	@Override
	public void run(final IProgressMonitor pm) {
		try {
			subMonitor = SubMonitor.convert(pm, computeSize(fileList));
		} catch (CoreException e) {
			LOG.warn("Cannot compute size", e);
			
			subMonitor = SubMonitor.convert(pm, 100);
		}

		for (int i = 0; i < fileList.size(); i++) {
			IResource uploadFile = fileList.get(i);

			subMonitor.subTask(Activator.getDefault().getString("Upload.Subtask", uploadFile.getName()));
			
			copy(uploadFile, destination);
		}
		
		subMonitor.done();
	}

	//CHECKSTYLE DISABLE ReturnCount
	private void copy(IResource res, HFSFolder folder) {
		LOG.debug("Copy {} to {}", res, folder);
		
		if (overwriteStatus == IDialogConstants.CANCEL_ID 
		 || errorStatus == IDialogConstants.ABORT_ID) {
			return;
		}
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if (res instanceof IFile) {
			HFSFile hfsFile = folder.createFile(res.getName());

	        subMonitor.subTask(Activator.getDefault().getString("Upload.Subtask", hfsFile.getName()));

			try (InputStream is = ((IFile) res).getContents()) {
				if (ZdevConnectable.getConnectable().exists(hfsFile)) {
					if (overwriteStatus == IDialogConstants.YES_TO_ALL_ID) {
						ZdevConnectable.getConnectable().save(hfsFile, is, transferMode);
					} else {
						if (overwriteStatus == IDialogConstants.NO_TO_ALL_ID) {
							subMonitor.worked(1);
							
							return;
						}
						
						overwriteStatus = new YesNoAllNoneCancelMessageDialog(shell, hfsFile.getName()).open();
						
						if (overwriteStatus == IDialogConstants.YES_ID
						 || overwriteStatus == IDialogConstants.YES_TO_ALL_ID) {
							ZdevConnectable.getConnectable().save(hfsFile, is, transferMode);
						}
					}
				} else {
					ZdevConnectable.getConnectable().createAndRefresh(hfsFile, is, transferMode);
				}
			} catch (CoreException | IOException e) {
				LOG.error("Cannot copy {}", res, e);
				
				if (errorStatus != IDialogConstants.IGNORE_ID) {
					errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
				}
			}
			
	        subMonitor.worked(1);
	        
			return;
		}
		
		if (res instanceof IFolder) {
			try {
				HFSFolder sub = folder.createChildFolder(res.getName());
				
				if (!ZdevConnectable.getConnectable().exists(sub)) {
					ZdevConnectable.getConnectable().create(sub);
				}
				
				for (IResource child : ((IFolder) res).members()) {
					copy(child, sub);
				}
			} catch (UpdateFailedException | CoreException e) {
				LOG.error("Cannot copy {}", res, e);
				
				if (errorStatus != IDialogConstants.IGNORE_ID) {
					errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
				}
			}
		}
	}
	//CHECKSTYLE ENABLE ReturnCount
	private int computeSize(List<IResource> files) throws CoreException {
		int result = 0;
		
		for (IResource res : files) {
			if (res instanceof IFile) {
				++result;
			} else {
				if (res instanceof IFolder) {
					result += (1 + computeSize(Arrays.asList(((IFolder) res).members())));
				}
			}
		}
		
		return result;
	}
}
