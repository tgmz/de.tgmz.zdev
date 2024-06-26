/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.syntaxcheck;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.model.UnsupportedOperationException;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.plicomp.PlicompConfigurationException;
import de.tgmz.zdev.plicomp.PlicompException;
import de.tgmz.zdev.plicomp.PlicompFactory;
import de.tgmz.zdev.xinfo.generated.MESSAGE;
import de.tgmz.zdev.xinfo.generated.PACKAGE;

/**
 * Syntax check.
 */
public class SyntaxcheckHandler extends AbstractSyntaxcheckHandler {
	private static final Logger LOG = LoggerFactory.getLogger(SyntaxcheckHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditor(event);
		
		ZOSObjectEditorInput editorInput = (ZOSObjectEditorInput) HandlerUtil.getActiveEditorInput(event);

		if (editorInput == null || !(editorInput.getZOSObject() instanceof Member)) {
			LOG.error("Syntaxcheck not applicable");
			return null;
		}
		
		Member m = (Member) editorInput.getZOSObject();
		
		if (editor.isDirty()) {
	        final MessageDialog dlg = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
	    			Activator.getDefault().getString("ConfirmSaveMember.Title"),
	    			null,
	    			Activator.getDefault().getString("ConfirmSaveMember.Message", m.toDisplayName()),
	    			org.eclipse.jface.dialogs.MessageDialog.QUESTION,
	    			new String[] { IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL },
	    			0);

	        final int[] intResult = new int[1];
	        
	        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().getDisplay().syncExec(() -> intResult[0] = dlg.open());
	        
	        if (intResult[0] == 0) {
	        	editor.doSave(new NullProgressMonitor());
	        } else {
	        	return null;
	        }
		}
		
		Session session = DbService.startTx();
		
		Item item;
		
		try {
			item = session.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", m.getParentPath()).setParameter("member", m.getName()).getSingleResultOrNull();
		
			if (item == null) {
				item = new Item(m.getParentPath(), m.getName());
			
				session.persist(item);
			}
		} catch (HibernateException e) {
			String s = Activator.getDefault().getString("Database.Error");
			
			LOG.error(s, e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
	                , Activator.getDefault().getString("Database.Title"), s);
			
			return null;
		} finally {
			DbService.endTx(session);
		}

		SequentialDataSet errorFeedback = null;
		
        try {
        	errorFeedback = TempDataSetFactory.getInstance().createErrorFeedback();
        	
			String jcl = createJcl(item, errorFeedback.getFullPath());
			
			submit(jcl);

           	deleteMarkers(m.toDisplayName());
           	
           	deleteAnnotations(editor);
            
			try(ByteArrayOutputStream bos = ZdevConnectable.getConnectable().getContents(errorFeedback, FileType.EBCDIC)) {
				PACKAGE plicomp = PlicompFactory.getInstance().getPlicomp(new ByteArrayInputStream(bos.toByteArray()), item.getFullName());
           	
				List<MESSAGE> messages = plicomp.getMESSAGE();
			
				for (int i = 0; i < messages.size() && i < Short.MAX_VALUE; ++i) {
					try {
						createMarkerAndAnnotation(editor, messages.get(i), m.toDisplayName());
					} catch (CoreException e) {
						LOG.error("Error", e);
					
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
								, Activator.getDefault().getString("Syntaxcheck.Title")
								, Activator.getDefault().getString("Syntaxcheck.CreateMarkersFailed"));
					}
				}
			}
		} catch (ConnectionException | PlicompException | PlicompConfigurationException |IOException e) {
			LOG.error("Error", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
		    		, Activator.getDefault().getString("Syntaxcheck.Title")
		    		, Activator.getDefault().getString("Syntaxcheck.Failed"));

		} finally {
			try {
				TempDataSetFactory.getInstance().delete(errorFeedback);
			} catch (PermissionDeniedException | UnsupportedOperationException | ConnectionException e) {
				if (errorFeedback != null) {
					LOG.error("Deletetion of {} failed", errorFeedback.toDisplayName(), e);
				} else {
					LOG.error("Cannot delete errorFeedback dataset", e);
				}
			}
		}

        // We should bring the RemoteErrorMarkersView to top here but the z/OS console refreshes last and comes to top anyway :-(
		return null;
	}
}

