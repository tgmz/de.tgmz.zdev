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

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.transfer.Activator;

/**
 * Utility class for unifying z/FS and HFS downloads.
 */
public abstract class AbstractDownloadHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractDownloadHandler.class);
	private static final String MSG_DIALOG_TITLE = "Download.Title";

	public void run(IDownloadRunnableWithProgress runner) {
		try {
			PlatformUI.getWorkbench().getProgressService().runInUI(PlatformUI.getWorkbench().getProgressService(),
					runner, ResourcesPlugin.getWorkspace().getRoot());
		} catch (InvocationTargetException e) {
			LOG.error("Error while downloading to {}", runner.getDestination(), e);

			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(MSG_DIALOG_TITLE),
					Activator.getDefault().getString("Download.Error"));
		} catch (InterruptedException e) {
			LOG.error("Thread got interrupted", e);

			Thread.currentThread().interrupt();
		}
	}
}
