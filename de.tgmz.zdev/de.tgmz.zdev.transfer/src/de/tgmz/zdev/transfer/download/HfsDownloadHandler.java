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
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ISelectionValidator;
import org.eclipse.ui.handlers.HandlerUtil;

import com.ibm.cics.zos.model.HFSEntry;

import de.tgmz.zdev.repo.Repository;
import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.ContainerSelectionDialogWithTransfermode;

/**
 * Copies all files and folders from HFS to the workspace.
 */
public class HfsDownloadHandler extends AbstractDownloadHandler {
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection iss) {
			ContainerSelectionDialogWithTransfermode dialog = new ContainerSelectionDialogWithTransfermode(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					getInitialRoot(),
					Activator.getDefault().getString("Download.Selectdestination"));

			ISelectionValidator sv = o -> {
					if (o instanceof IPath ipath && ipath.segmentCount() > 1) {
						return null;
					} else {
						return Activator.getDefault().getString("Download.Selection.Error");
					}
				};
			
			dialog.setValidator(sv);
			dialog.showClosedProjects(false);

			if (dialog.open() == Window.OK) {
				IFolder destination = ResourcesPlugin.getWorkspace().getRoot().getFolder((IPath) dialog.getResult()[0]);

				List<HFSEntry> entriesToDownload = new LinkedList<>();

				for (Iterator<?> iterator = iss.iterator(); iterator.hasNext();) {
					entriesToDownload.add((HFSEntry) iterator.next());
				}
			
				Repository.storeLastDownloadLocation(destination.getFullPath().toPortableString());
			
				super.run(new HfsDownloadRunner(destination, entriesToDownload, dialog.getTransferMode()));
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
}
