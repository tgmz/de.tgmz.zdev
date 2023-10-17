/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.filelock;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.zos.NotConnectedException;

/**
 * Starts the FileLockClient
 */
public class StartClientHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(StartClientHandler.class);
	private static final String TITLE = "StartClient.Title";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.info("Start filelock client requested");
		
		if (!FileLockClient.getInstance().isRunning()) {
			try {
				if (!FileLockClient.getInstance().start()) {
					LOG.error("Failed to start filelock client");
					
					MessageDialog.openError(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
							Activator.getDefault().getString(TITLE),
							Activator.getDefault().getString("StartClient.Failed"));
				}
			} catch (NotConnectedException e) {
				LOG.error("Not connected", e);
				
				MessageDialog.openError(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
						Activator.getDefault().getString(TITLE),
						Activator.getDefault().getString("StartClient.NotConnected"));
			}
		} else {
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
					Activator.getDefault().getString(TITLE),
					Activator.getDefault().getString("StartClient.AlreadyRunning"));
		}
		
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return !FileLockClient.getInstance().isRunning(); 
	}
}
