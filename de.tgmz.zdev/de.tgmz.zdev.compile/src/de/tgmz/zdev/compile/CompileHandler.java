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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.common.util.IOUtils;
import com.ibm.cics.common.util.StringUtil;
import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.IJobDetails;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.editor.ZdevEditor;
import de.tgmz.zdev.preferences.Language;
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
		
		Session session = DbService.startTx();
		
		try {
			item = session.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", m.getParentPath()).setParameter("member", m.getName()).getSingleResultOrNull();

			if (item == null) {
				item = new Item(m.getParentPath(), m.getName());
				
				session.persist(item);
			}
		} finally {
			DbService.endTx(session);
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
			String jcl = createCompileJcl(item);
			
			if (item.getOption().isBind()) {
				jcl += createBindJcl(item);
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
		
		if (selection instanceof IStructuredSelection iss) {
			Object o = iss.getFirstElement();
			
			if (o instanceof Member member) {
				return member;
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
		
    	if (!ZdevEditor.checkPreconditions()) {
            return null;
    	}
		
    	return (Member) editorInput.getZOSObject();
	}
	
	private String createCompileJcl(Item item) throws IOException {
		String res;
		String syslib;
		String comp;
		
		switch (Language.fromDatasetName(item.getDsn())) {
		case COBOL:
			res = "compile/templates/cobol.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_COB);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_COB);
			break;
		case ASSEMBLER:
			res = "compile/templates/assembler.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_ASM);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_ASM);
			break;
		case C:
			res = "compile/templates/c.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_C);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_C);
			break;
		case CPP:
			res = "compile/templates/cpp.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_CPP);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_CPP);
			break;
		case PLI:
		default:	
			res = "compile/templates/pli.txt";
			syslib = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.SYSLIB_PLI);
			comp = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.COMP_PLI);
			break;
		}
		
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream(res)) {
			syslib = SyslibFactory.getInstance().createSyslib(syslib);
			
			String template = IOUtils.readInputStreamAsString(is, true, StandardCharsets.UTF_8.name());
		
			MessageFormat mf = new MessageFormat(template);
			
			Object[] o = new Object[] {
					  de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.JOB_CARD)
					  , item.getMember()
					  , item.getFullName()
					  , syslib
					  , de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.OBJLIB)
					  , item.getOption().isComp() ? "'," + item.getOption().getCompOption() + "'" : comp
					  , item.getOption().isDb2() ? "'," + item.getOption().getDb2Option() + "'" : ""
					  , item.getOption().isCics() ? "'," + item.getOption().getCicsOption() + "'" : ""
					};
			
			return mf.format(o);
		}
	}
	private String createBindJcl(Item item) throws IOException {
		try(InputStream is = this.getClass().getClassLoader().getResourceAsStream("compile/templates/link.txt")) {
			String template = IOUtils.readInputStreamAsString(is, true, StandardCharsets.UTF_8.name());
		
			MessageFormat mf = new MessageFormat(template);
			
			String bind = item.getOption().getBindOption();
			
			Object[] o = new Object[] {item.getMember()
					, de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.OBJLIB)
					, de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.LOADLIB)
					, StringUtil.hasContent(bind) ? "'," + bind + "'" : de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.LINK_OPTIONS)
					};
			
			return mf.format(o);
		}
	}
}
