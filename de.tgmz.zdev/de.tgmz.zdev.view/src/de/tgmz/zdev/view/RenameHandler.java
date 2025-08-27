/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.view;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.DataSet;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.UnsupportedOperationException;
import com.ibm.cics.zos.model.UpdateFailedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import jakarta.persistence.EntityManager;

/**
 * Handler for renaming a member.
 */
public class RenameHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RenameHandler.class);
	private static final String TARGET_VIEW_ID = "de.tgmz.zdev.view.ZdevDataSetsExplorer";
	private static final String TITLE = Activator.getDefault().getString("Paste.Title");

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Member oldMember = getMemberFromEvent(event);
		
		if (oldMember != null) {
			Member newMember;
			try {
				DataSet dataSet = ZdevConnectable.getConnectable().getDataSet((oldMember).getParentPath());
						
				newMember = MemberUtility.getInstance().getNewMember((PartitionedDataSet) dataSet, (oldMember).getName());
			} catch (FileNotFoundException e) {
				LOG.warn("An unexpected error has occurred", e);
						
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE 
						, Activator.getDefault().getString("Paste.Error", (oldMember).getName(), e.getMessage()));
						
				return null;
			}
					
			if (newMember == null || (oldMember).getName().equalsIgnoreCase(newMember.getName())) {
				return null;
			}
					
			try {
				ByteArrayOutputStream contents = ZdevConnectable.getConnectable().getContents(oldMember, FileType.BINARY);

				ZdevConnectable.getConnectable().delete(oldMember);
					
				ZdevConnectable.getConnectable().save(newMember, new ByteArrayInputStream(contents.toByteArray()));
					
				updateItem(oldMember.getParentPath(), oldMember.getName(), newMember.getName());
			} catch (PermissionDeniedException  e) {
				LOG.warn("Operation not allowed", e);
					
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE 
						, Activator.getDefault().getString("Paste.NotPermitted", (oldMember).getName(), e.getMessage()));
			} catch (FileNotFoundException | UnsupportedOperationException | UpdateFailedException | ConnectionException e) {
				LOG.warn("An unexpected error has occurred", e);
					
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, TITLE 
						, Activator.getDefault().getString("Paste.Error", (oldMember).getName(), e.getMessage()));
			}
			
           	// Refresh ZdevDataSetsExplorer View
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
	private void updateItem(String dsn, String oldMember, String newMember) {
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			Item item = em.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", dsn).setParameter("member", oldMember).getSingleResultOrNull();

			if (item == null) {
				item = new Item(dsn, newMember);
			} else {
				item.setMember(newMember);
			}
			
			em.merge(item);
		}
	}
	private Member getMemberFromEvent(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			
			if (o instanceof Member) {
				return (Member) o;
			}
		}
		
		return null;
	}
}
