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
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.PartitionedDataSet;

import de.tgmz.zdev.view.ZdevDataSetsExplorer;

/**
 * Handler for paste content command.
 */
public class PasteHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(PasteHandler.class);
	private static final String TARGET_VIEW_ID = "de.tgmz.zdev.view.ZdevDataSetsExplorer";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection iss) {
			Object o = iss.getFirstElement();
			
			if (o instanceof PartitionedDataSet pds) {
				try {
					PlatformUI.getWorkbench().getProgressService().runInUI(PlatformUI.getWorkbench().getProgressService()
							, new PasteRunner(pds)
							, ResourcesPlugin.getWorkspace().getRoot());
		        } catch (InvocationTargetException e) {
					LOG.error("Error executin paste", e);
					
					return null;
		        } catch (InterruptedException e) {
		        	LOG.error("Thread got interrupted", e);
				
		        	Thread.currentThread().interrupt();
		        	
		        	return null;
		        }
			}
			
           	// Refresh ZdevDataSetsExplorer view
       		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
       		ZdevDataSetsExplorer view = (ZdevDataSetsExplorer) page.findView(TARGET_VIEW_ID);
       		if (view == null) {
       			try {
       				page.showView(TARGET_VIEW_ID);
       				view = (ZdevDataSetsExplorer) page.findView(TARGET_VIEW_ID);
       			} catch (PartInitException e) {
       				LOG.error("findDataSetExplorer", e);
       			}
       		}
        		
       		if (view != null) {
       			page.bringToTop(view);
       			view.forceRefresh();
       		} else {
       			LOG.warn("Cannot switch to view {}", TARGET_VIEW_ID);
       		}

		}
		
		return null;
	}
}
