/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.folding;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.editor.ZdevEditor;

/**
 * Handler for editor -> expand
 */
public class ExpandHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(ExpandHandler.class);

	@Override
	public Object execute(ExecutionEvent event) {
		ZOSObjectEditorInput editorInput = (ZOSObjectEditorInput) HandlerUtil.getActiveEditorInput(event);

       	try {
       		ITextEditor te = ZdevEditor.findEditor((Member) editorInput.getZOSObject(), true);

			if (te instanceof ZdevEditor ze) {
				ze.getProjectionAnnotationModel().expandAll(0, editorInput.getContents().length());
			}
		} catch (PartInitException e) {
			LOG.error("Cannot find editor for {}", editorInput.getName(), e);
		}
		
		return null;
	}
}

