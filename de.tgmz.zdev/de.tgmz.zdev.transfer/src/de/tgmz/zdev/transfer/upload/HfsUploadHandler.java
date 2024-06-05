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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.HFSFolder;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;
import de.tgmz.zdev.transfer.Activator;
import de.tgmz.zdev.view.HFSSelectionDialog;

/**
 * Copies files and folder from the workspace to HFS.
 */
public class HfsUploadHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(HfsUploadHandler.class);
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
		
		if (selection instanceof IStructuredSelection iss) {
			List<IResource> resourceList = new LinkedList<>();
			
			for (Iterator<?> iterator = iss.iterator(); iterator.hasNext();) {
				resourceList.add((IResource) iterator.next());
			}
			
			IPreferenceStore ps = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore(); 
			
			HFSSelectionDialog hsd = new HFSSelectionDialog(PlatformUI.getWorkbench().getDisplay().getActiveShell()
					, ZdevConnectable.getConnectable()
					, ps.getString(ZdevPreferenceConstants.USS_HOME));
			
			int open = hsd.open();
			
			if (open == Window.OK) {
				HFSFolder destination = (HFSFolder) hsd.getFirstResult();
			
				HfsUploadRunner ur = new HfsUploadRunner(destination, resourceList, hsd.getTransferMode());

				try {
					PlatformUI.getWorkbench().getProgressService().runInUI(
							PlatformUI.getWorkbench().getProgressService(), ur,
							ResourcesPlugin.getWorkspace().getRoot());
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
}
