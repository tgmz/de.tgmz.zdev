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

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;

/**
 * Forces unlock of a dataset member.
 */
public class RequestUnlockHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RequestUnlockHandler.class);
	private static final String TITLE = Activator.getDefault().getString("UnlockAction.Title");

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		LOG.debug("Action [{}]", event);

		IStructuredSelection selection = (IStructuredSelection) HandlerUtil.getActiveMenuSelection(event);

		for (Iterator<?> iter = selection.iterator(); iter.hasNext();) {
			Object obj = iter.next();

			if (!(obj instanceof Member)) {
				LOG.warn("Resource not valid");
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
						, TITLE
						, Activator.getDefault().getString("UnlockAction.NotValid"));
					
				return null;
			}
				
			Member m = (Member) obj;
				
			try {
				FileLockClient.getInstance().release(m);
			} catch (FileLockException e) {
				LOG.error("Unlock failed", e);
					
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE
						, Activator.getDefault().getString("UnlockAction.Failed"));
					
				return null;
			}
				
			MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
					, TITLE
					, Activator.getDefault().getString("UnlockAction.Requested", m.getPath()));
		}
		return null;
	}
}
