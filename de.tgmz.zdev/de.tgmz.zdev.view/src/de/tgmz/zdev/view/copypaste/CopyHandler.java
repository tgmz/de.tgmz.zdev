/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view.copypaste;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for copy content command.
 */
public class CopyHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CopyHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection) {
			try {
				PlatformUI.getWorkbench().getProgressService().runInUI(PlatformUI.getWorkbench().getProgressService()
						, new CopyRunner((IStructuredSelection) selection)
						, ResourcesPlugin.getWorkspace().getRoot());
	        } catch (InvocationTargetException e) {
				LOG.error("Error executin paste", e);
	        } catch (InterruptedException e) {
	        	LOG.error("Thread got interrupted", e);
			
	        	Thread.currentThread().interrupt();
	        }
		}
		
		return null;
	}
}
