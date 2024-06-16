/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.folding;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.editor.Activator;
import de.tgmz.zdev.editor.PatternScanner;
import de.tgmz.zdev.editor.ZdevEditor;

/**
 * Handler for editor -> collapse to string
 */
public class CollapseHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(CollapseHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getActiveMenuSelection(event);
		
		if (selection instanceof ITextSelection) {
			Activator.getDefault().setPattern(((ITextSelection) selection).getText());
		
			ZOSObjectEditorInput editorInput = (ZOSObjectEditorInput) HandlerUtil.getActiveEditorInput(event);
			
			try {
				ITextEditor te = ZdevEditor.findEditor((Member) editorInput.getZOSObject(), true);
				
				if (te instanceof ZdevEditor) {
					ZdevEditor zde = (ZdevEditor) te;
					List<Position> positions = PatternScanner.getInstance().calculatePositions(zde.getDocumentProvider().getDocument(editorInput));
				
					zde.updateFoldingStructure(positions);
				
					zde.getProjectionAnnotationModel().collapseAll(0, editorInput.getContents().length());
				}
			} catch (PartInitException e) {
				LOG.error("Cannot find editor for {}", editorInput.getName(), e);
			}
		}
		
		return null;
	}
}
