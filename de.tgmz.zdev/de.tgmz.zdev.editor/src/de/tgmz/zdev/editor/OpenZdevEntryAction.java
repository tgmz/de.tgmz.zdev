/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.actions.OpenDataEntryAction;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.database.DbService;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;

/**
 * Open Editor Action.
 */
public class OpenZdevEntryAction extends OpenDataEntryAction {
	private static final Logger LOG = LoggerFactory.getLogger(OpenZdevEntryAction.class);
	
	@Override
	public ZdevEditor openEditor(IWorkbenchPage aPage) throws PartInitException {
		Member m;
		
		if (this.editorInput != null) {
			m = (Member) this.editorInput.getZOSObject();
		} else {
			m = (Member) this.zosLocation;
		}
		
		Session session = DbService.startTx();
		
		try {
			Item item = session.createNamedQuery("byDsnAndMember", Item.class).setParameter("dsn", m.getParentPath()).setParameter("member", m.getName()).getSingleResultOrNull();

			if (item == null) {
				item = new Item(m.getParentPath(), m.getName());
				
				session.persist(item);
			}
		} finally {
			DbService.endTx(session);
		}
		
		try {
			if (LocalHistory.getInstance().getVersions(m.toDisplayName()).isEmpty()) {
				try (ByteArrayOutputStream contents = ZdevConnectable.getConnectable().getContents(m, FileType.EBCDIC)) {
					LocalHistory.getInstance().save(contents.toByteArray(), m.toDisplayName());
				} catch (ConnectionException | IOException | HistoryException e) {
					LOG.error("Cannot write history for {}",  m.toDisplayName(), e);
				}
			}
		} catch (HistoryException e) {
			LOG.error("Cannot read history", e);
		}
		
		return (ZdevEditor) aPage.openEditor(this.editorInput, ZdevEditor.ID);
	}
}

