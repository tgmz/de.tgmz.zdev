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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.DataPath;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.repo.Repository;
import de.tgmz.zdev.transfer.Activator;

/**
 * Copies one or more members to the workspace.
 */
public class DownloadHandler extends AbstractDownloadHandler {
	private static final Logger LOG = LoggerFactory.getLogger(DownloadHandler.class);
	private static final String MSG_DIALOG_TITLE = "Download.Title";

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
			ContainerSelectionDialog dialog = new ContainerSelectionDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					getInitialRoot(), false,
					Activator.getDefault().getString("Download.Selectdestination"));

			ISelectionValidator sv = o -> {
					if (o instanceof IPath && ((IPath) o).segmentCount() > 1) {
						return null;
					} else {
						return Activator.getDefault().getString("Download.Selection.Error");
					}
				};
			
			dialog.setValidator(sv);
			dialog.showClosedProjects(false);

			if (dialog.open() == Window.OK) {
				IFolder destination = ResourcesPlugin.getWorkspace().getRoot().getFolder((IPath) dialog.getResult()[0]);

				List<DataEntry> dataEntriesToDownload = getDataEntriesToDownload(selection);
			
				Repository.storeLastDownloadLocation(destination.getFullPath().toPortableString());
			
				super.run(new DownloadRunner(destination, dataEntriesToDownload));
			}
		}

		return null;
	}

	private IContainer getInitialRoot() {
		String lastDownloadLocation = Repository.getLastDownloadLocation();
		
		if (!"".equals(lastDownloadLocation)) {
			return ResourcesPlugin.getWorkspace().getRoot().getFolder(Path.fromPortableString(lastDownloadLocation));
		} else {
			return ResourcesPlugin.getWorkspace().getRoot();
		}
	}

	private List<DataEntry> getDataEntriesToDownload(ISelection selection) {
		@SuppressWarnings("unchecked")
		Iterator<IStructuredSelection> selectionIterator = ((IStructuredSelection) selection).iterator();

		List<DataEntry> dataEntriesToDownload = new LinkedList<>();

		while (selectionIterator.hasNext()) {
			Object selectedDataEntry = selectionIterator.next();

			if (selectedDataEntry instanceof Member) {
				dataEntriesToDownload.add((DataEntry) selectedDataEntry);
			}

			if (selectedDataEntry instanceof PartitionedDataSet) {
				PartitionedDataSet pds = (PartitionedDataSet) selectedDataEntry;
				
				DataPath dataPath = new DataPath(pds.getPath());

				try {
					dataEntriesToDownload.addAll(ZdevConnectable.getConnectable().getDataSetEntries(dataPath));
				} catch (PermissionDeniedException | ConnectionException e) {
					LOG.error("Cannot get list of members for {}", pds.getName(), e);

					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Activator.getDefault().getString(MSG_DIALOG_TITLE),
							Activator.getDefault().getString("Download.Error.Memberlist", pds.toDisplayName()));
				}
			}
		}
		
		return dataEntriesToDownload;
	}
}
