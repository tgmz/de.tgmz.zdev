/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.transfer.download;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants;
import com.ibm.cics.zos.model.HFSEntry;
import com.ibm.cics.zos.model.HFSFile;
import com.ibm.cics.zos.model.HFSFolder;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.SkipIgnoreAbortMessageDialog;
import de.tgmz.zdev.view.YesNoAllNoneCancelMessageDialog;

/**
 * Copies all files and folders from HFS to the workspace.
 */
public class HfsDownloadRunner implements IDownloadRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(HfsDownloadRunner.class);
	private IFolder folder;
	private List<HFSEntry> hfsEntries;
	private IZOSConstants.FileType transferMode;
	private SubMonitor subMonitor;
	private int overwriteStatus = IDialogConstants.NO_ID;
	private int errorStatus = IDialogConstants.NO_ID;
	
	public HfsDownloadRunner(IFolder folder, List<HFSEntry> hfsEntries, IZOSConstants.FileType transferMode) {
		super();
		this.folder = folder;
		this.hfsEntries = hfsEntries;
		this.transferMode  = transferMode;
	}

	@Override
	public void run(final IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
		int computeSize;
		
		try {
			computeSize = computeSize(hfsEntries);
		} catch (FileNotFoundException | PermissionDeniedException | ConnectionException e) {
			LOG.error("Cannot get sipze of {}. Assume a default", hfsEntries);
			
			computeSize = 100;
		}
		
		subMonitor = SubMonitor.convert(pm, computeSize);	

		subMonitor.beginTask("Download", computeSize);

		for (int i = 0; i < hfsEntries.size(); i++) {
			HFSEntry hfsEntry = hfsEntries.get(i);
			
			copy(hfsEntry, folder);
		}
		
		subMonitor.done();
	}

	//CHECKSTYLE DISABLE ReturnCount
	private void copy(HFSEntry hfsEntry, IFolder res) {
		LOG.debug("Copy {} to {}", hfsEntry, res);
		
		if (overwriteStatus == IDialogConstants.CANCEL_ID
		 || errorStatus == IDialogConstants.ABORT_ID) {
			return;
		}
		
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		
		if (hfsEntry instanceof HFSFile) {
			subMonitor.subTask(Activator.getDefault().getString("Download.Subtask", hfsEntry.getName()));
			
			IFile iFile = res.getFile(hfsEntry.getName());
			
			try (ByteArrayOutputStream contents = ZdevConnectable.getConnectable().getContents((HFSFile) hfsEntry, transferMode);
					InputStream is = new ByteArrayInputStream(contents.toByteArray())) {
				if (iFile.exists()) {
					if (overwriteStatus == IDialogConstants.YES_TO_ALL_ID) {
						iFile.setContents(is, IResource.FORCE | IResource.KEEP_HISTORY, subMonitor.split(1));
					} else {
						if (overwriteStatus == IDialogConstants.NO_TO_ALL_ID) {
							subMonitor.worked(1);
							
							return;
						}
						
						overwriteStatus = new YesNoAllNoneCancelMessageDialog(shell, hfsEntry.getName()).open();
						
						if (overwriteStatus == IDialogConstants.YES_ID
						 || overwriteStatus == IDialogConstants.YES_TO_ALL_ID) {
							iFile.setContents(is, IResource.FORCE | IResource.KEEP_HISTORY, subMonitor.split(1));
						}
					}
				} else {
					iFile.create(is, IResource.FORCE | IResource.KEEP_HISTORY, subMonitor.split(1));
				}
			} catch (IOException | CoreException e) {
				LOG.error("Cannot write to destination {}", iFile, e);
				
				if (errorStatus != IDialogConstants.IGNORE_ID) {
					errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
				}
			}
		}
		
		if (hfsEntry instanceof HFSFolder) {
			IFolder sub = res.getFolder(hfsEntry.getName());
			
			try {
				if (!sub.exists()) {
					sub.create(true, true, subMonitor.split(1));
				}
				
				for (HFSEntry child : ZdevConnectable.getConnectable().getChildren((HFSFolder) hfsEntry, true)) {
					copy(child, sub);
				}
			} catch (FileNotFoundException | PermissionDeniedException | CoreException | ConnectionException e) {
				LOG.error("Cannot copy {}", res, e);
				
				if (errorStatus != IDialogConstants.IGNORE_ID) {
					errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
				}
			}
		}
	}
	//CHECKSTYLE ENABLE ReturnCount

	@Override
	public IFolder getDestination() {
		return folder;
	}
	private static int computeSize(List<HFSEntry> hfsEntries) throws FileNotFoundException, PermissionDeniedException, ConnectionException {
		int size = 0;
		
		for (HFSEntry hfsEntry : hfsEntries) {
			if (hfsEntry instanceof HFSFolder) {
				size += computeSize(ZdevConnectable.getConnectable().getChildren((HFSFolder) hfsEntry, true));
			} else {
				++size;
			}
		}
		
		return size;
	}
}
