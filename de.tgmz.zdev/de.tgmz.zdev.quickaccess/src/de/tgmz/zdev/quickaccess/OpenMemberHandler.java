/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/

package de.tgmz.zdev.quickaccess;

import java.io.FileNotFoundException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataSet;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.MigratedDataSet;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.VSAMData;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.editor.ZdevEditor;

/**
 * Handler for opening members.
 */
public class OpenMemberHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(OpenMemberHandler.class);

	private static final String WINDOW_TITLE = Activator.getDefault().getString("Quickaccess.Title");
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MemberSelectionDialog msd = new MemberSelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), true);
	
		int open = msd.open();
		
		if (open == Window.OK) {
			for (Object o : msd.getResult()) {
				if (o instanceof Item) {
					Item item = (Item) o;
					
					String dsn = item.getDsn();
					String m = item.getMember();
					
					Member member;
					try {
						DataSet parent = ZdevConnectable.getConnectable().getDataSet(dsn);
						
						if (parent instanceof MigratedDataSet) {
							MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
			                		, WINDOW_TITLE
			                		, Activator.getDefault().getString("Quickaccess.Migrated", item.getDsn()));
							
							continue;
						}
						
						if (parent instanceof VSAMData) {
							MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
			                		, WINDOW_TITLE
			                		, Activator.getDefault().getString("Quickaccess.VSAM", item.getDsn()));
							
							continue;
						}
						
						member = ZdevConnectable.getConnectable().getDataSetMember((PartitionedDataSet) parent, m);
					} catch (FileNotFoundException e) {
						LOG.warn("Cannot find dataset or member", e);
						
						MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
		                		, WINDOW_TITLE
		                		, Activator.getDefault().getString("Quickaccess.NotFound", item.getFullName()));
						
						// Delete item from database
						DbService.getInstance().inTransaction(x -> x.remove(o));
						
						continue;
					} catch (PermissionDeniedException | ConnectionException e) {
						LOG.error("Cannot get dataset or member", e);
						
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
		                		, WINDOW_TITLE
		                		, Activator.getDefault().getString("Quickaccess.NotAccess", item.getFullName()));
						
						continue;
					}
	
					try {
						ITextEditor editor = ZdevEditor.findEditor(member, true);
						
						if (editor == null) {
							MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
						    		, WINDOW_TITLE
						    		, Activator.getDefault().getString("Quickaccess.NotOpen", item.getFullName()));
							
							continue;
						}
						
						IWorkbenchPage page= editor.getSite().getPage();
						page.activate(editor);
					} catch (PartInitException e) {
						LOG.warn("Cannot open editor", e);
						
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
					    		, WINDOW_TITLE
					    		, Activator.getDefault().getString("Quickaccess.NotOpen", item.getFullName()));
					}
				}
			}
		}
	
		return null;
	}
}
