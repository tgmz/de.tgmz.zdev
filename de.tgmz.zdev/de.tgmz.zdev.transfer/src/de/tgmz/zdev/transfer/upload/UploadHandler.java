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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.PartitionedDataSet;

import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.DatasetSelectionDialog;

/**
 * Copies a list of local files to a partitioned dataset.
 */
public class UploadHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(UploadHandler.class);
	private static final String MSG_DIALOG_TITLE = "Upload.Title";
	private static final String MSG_DIALOG_ERROR = "Upload.Error";

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection) {
			DatasetSelectionDialog dsds = new DatasetSelectionDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell());
			
			int open = dsds.open();
			
			if (open == Window.OK) {
				PartitionedDataSet destination = (PartitionedDataSet) dsds.getFirstResult();
			
				List<IFile> fileList = new LinkedList<>();
			
				try {
					for (Iterator<?> iterator = ((IStructuredSelection) selection).iterator(); iterator.hasNext();) {
						fileList.addAll(getFilesRecursively((IResource) iterator.next()));
					}
					
					UploadRunner ur = new UploadRunner(destination, fileList);

					PlatformUI.getWorkbench().getProgressService().runInUI(
							PlatformUI.getWorkbench().getProgressService(), ur,
							ResourcesPlugin.getWorkspace().getRoot());
				} catch (CoreException e) {
					LOG.error("Error getting files, see log for details", e);

					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Activator.getDefault().getString(MSG_DIALOG_TITLE),
							Activator.getDefault().getString(MSG_DIALOG_ERROR));
				} catch (InvocationTargetException e) {
					LOG.error("Error while uploading to {}", destination.toDisplayName(), e);

					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Activator.getDefault().getString(MSG_DIALOG_TITLE),
							Activator.getDefault().getString(MSG_DIALOG_ERROR));
				} catch (InterruptedException e) {
					LOG.error("Thread got interrupted", e);
				
					Thread.currentThread().interrupt();
				}
			}
		}

		return null;
	}
	
	private List<IFile> getFilesRecursively(IResource res) throws CoreException {
		if (res instanceof IFile) {
			return Collections.singletonList((IFile) res);
		} else {
			if (res instanceof IFolder) {
				List<IFile> result = new LinkedList<>();
				
				for (IResource subres : ((IFolder) res).members()) {
					result.addAll(getFilesRecursively(subres));
				}
				
				return result;
			} else {
				return Collections.emptyList();
			}
		}
	}
}
