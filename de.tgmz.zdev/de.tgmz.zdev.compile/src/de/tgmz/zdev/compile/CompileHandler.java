/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.compile;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.IJobDetails;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;

public class CompileHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CompileHandler.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.
	 * ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		Member m = getMember(event);
		
		if (m == null) {
			// Not applicable or checkPreconditions() returned false
			return null;
		}

		Item item;
		
		try (EntityManager em = DbService.getInstance().getEntityManagerFactory().createEntityManager()) {
			em.getTransaction().begin();
			
			item = em.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", m.getParentPath()).setParameter("member", m.getName()).getSingleResultOrNull();

			if (item == null) {
				item = new Item(m.getParentPath(), m.getName());
				
				em.persist(item);
			}

			em.getTransaction().commit();
		}
		

		if (!item.isLock()) {
			ItemOptionsDialog d = new ItemOptionsDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), item);
			
			if ((item = d.open()) != null) {
				session = DbService.startTx();
					
				try {
					session.merge(item);
				} finally {
					DbService.endTx(session);
				}
			} else {
				return null;
			}
		}

		try {
			String jcl = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.JOB_CARD)
					+ System.lineSeparator()
					+ JclFactory.getInstance().createCompileStep(item);
			
			if (item.getOption().isBind()) {
				jcl += JclFactory.getInstance().createBindStep(item);
			}
			
			IJobDetails jobId = ZdevConnectable.getConnectable().submitJob(jcl);
			
			LOG.info("Job {} submitted", jobId.getId());
		} catch (IOException | ConnectionException e) {
			LOG.error("JCL not submitted", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
					, Activator.getDefault().getString("Compile.Title"), "");
		}
		
		return null;
	}
	private Member getMember(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection) {
			Object o = ((IStructuredSelection) selection).getFirstElement();
			
			if (o instanceof Member) {
				return (Member) o;
			} else {
				LOG.info("No member selected");
				
				return null;
			}
		}
		
		// Check if handler is called from editor
		ZOSObjectEditorInput editorInput = (ZOSObjectEditorInput) HandlerUtil.getActiveEditorInput(event);

		if (editorInput == null || !(editorInput.getZOSObject() instanceof Member)) {
			LOG.error("Compile not applicable");
			return null;
		}
		
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (editor.isDirty()) {
	        final MessageDialog dlg = new MessageDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
	    			Activator.getDefault().getString("ConfirmSaveMember.Title"),
	    			null,
	    			Activator.getDefault().getString("ConfirmSaveMember.Message", ((Member) editorInput.getZOSObject()).toDisplayName()),
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
		
		
    	return (Member) editorInput.getZOSObject();
	}
}
