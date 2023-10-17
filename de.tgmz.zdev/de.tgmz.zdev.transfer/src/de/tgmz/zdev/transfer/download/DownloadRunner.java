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
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.DataEntry;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.preferences.Language;
import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.SkipIgnoreAbortMessageDialog;
import de.tgmz.zdev.view.YesNoAllNoneCancelMessageDialog;

/**
 * Copies one or more members to the workspace.
 */
public class DownloadRunner implements IDownloadRunnableWithProgress {
	private static final Logger LOG = LoggerFactory.getLogger(DownloadRunner.class);
	private IFolder folder;
	private List<DataEntry> dataEntries;
	private SubMonitor subMonitor;
	private int overwriteStatus = IDialogConstants.NO_ID;
	private int errorStatus = IDialogConstants.NO_ID;

	public DownloadRunner(IFolder folder, List<DataEntry> dataEntries) {
		super();
		this.folder = folder;
		this.dataEntries = dataEntries;
	}

	@Override
	public void run(final IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
		subMonitor = SubMonitor.convert(pm, dataEntries.size());

		subMonitor.beginTask("Download", dataEntries.size());

		for (int i = 0; i < dataEntries.size() && overwriteStatus != IDialogConstants.CANCEL_ID && errorStatus != IDialogConstants.ABORT_ID; i++) {
			DataEntry de = dataEntries.get(i);
			
			IFile file = folder.getFile(de.getName() + Language.fromDatasetName(de.getParentPath()).getExtension());

			if (file != null) {
				subMonitor.subTask(Activator.getDefault().getString("Download.Subtask", file.toString()));
			
				Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				
				try (ByteArrayOutputStream bos = ZdevConnectable.getConnectable().getContents(de, FileType.EBCDIC)) {
					try (InputStream is = new ByteArrayInputStream(bos.toByteArray())) {
						if (file.exists()) {
							if (overwriteStatus == IDialogConstants.NO_TO_ALL_ID) {
								subMonitor.worked(1);
								
								continue;
							}
							
							if (overwriteStatus == IDialogConstants.YES_TO_ALL_ID) {
								file.setContents(is, IResource.FORCE | IResource.KEEP_HISTORY, subMonitor.split(1));
							} else {
								overwriteStatus = new YesNoAllNoneCancelMessageDialog(shell, file.getName()).open();
								
								if (overwriteStatus == IDialogConstants.YES_ID
								 || overwriteStatus == IDialogConstants.YES_TO_ALL_ID) {
									file.setContents(is, IResource.FORCE | IResource.KEEP_HISTORY, subMonitor.split(1));
								}
							}
						} else {
							file.create(is, true, subMonitor.split(1));
						}
					} catch (IOException | CoreException e) {
						LOG.error("Cannot write to destination {}", file.getName(), e);
						
						if (errorStatus != IDialogConstants.IGNORE_ID) {
							errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
						}
					}
				} catch (IOException | ConnectionException e) {
					LOG.error("Cannot get contents of {}", de.getName(), e);
					
					if (errorStatus != IDialogConstants.IGNORE_ID) {
						errorStatus = new SkipIgnoreAbortMessageDialog(shell, e.getMessage()).open();
					}
				}
			}
		}
		
		subMonitor.done();
	}

	@Override
	public IFolder getDestination() {
		return folder;
	}
}
