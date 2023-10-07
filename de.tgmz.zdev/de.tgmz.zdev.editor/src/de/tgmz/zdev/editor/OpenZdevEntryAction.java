/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.actions.OpenDataEntryAction;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;

/**
 * Open Editor Action.
 */
public class OpenZdevEntryAction extends OpenDataEntryAction {
	private static final Logger LOG = LoggerFactory.getLogger(OpenZdevEntryAction.class);
	
	@Override
	protected ZdevEditor openEditor(IWorkbenchPage aPage) throws PartInitException {
		LOG.debug("Open editor");
		
		Member m = (Member) this.editorInput.getZOSObject();
		
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

