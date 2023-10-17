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

/**
 * Stops the FileLockClient
 */
public class StopClientHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(StopClientHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.info("Stop filelock client requested");
		
		if (FileLockClient.getInstance().isRunning()) {
			FileLockClient.getInstance().stop();
		} else {
			MessageDialog.openInformation(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() ,
					"FileLock",
					Activator.getDefault().getString("StopClient.NotRunning"));
		}
		return null;
	}
	
	@Override
	public boolean isEnabled() {
		return FileLockClient.getInstance().isRunning(); 
	}
}
