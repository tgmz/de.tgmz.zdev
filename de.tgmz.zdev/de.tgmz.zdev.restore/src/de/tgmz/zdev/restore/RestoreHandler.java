/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.restore;

import java.text.ParseException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.model.Member;

import de.tgmz.zdev.editor.ZdevEditor;
import de.tgmz.zdev.history.HistoryException;
import de.tgmz.zdev.history.LocalHistory;
import de.tgmz.zdev.restore.compare.CompareInput;

/**
 * Restores a member from history.
 */
public class RestoreHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(RestoreHandler.class);
	private static final String TITLE = "Restore.Title";

	/* (non-Javadoc)
	 * @see org.eclipse.core.commands.IHandler#execute(org.eclipse.core.commands.ExecutionEvent)
	 */
	@Override
	public Object execute(ExecutionEvent event) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		
		if (selection instanceof IStructuredSelection iss) {
			Object o = iss.getFirstElement();
			
			if (o instanceof Member member) {
				ITextEditor memberEditor = null;
				try {
					memberEditor = ZdevEditor.findEditor(member, false);
				} catch (PartInitException e) {
					LOG.error("Error while searching editor for {}, reason:", member.toDisplayName(), e);
				}
				
				if (memberEditor != null) {
					if (MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
							, Activator.getDefault().getString(TITLE) 
							, Activator.getDefault().getString("Restore.MustClose"))) {
						memberEditor.close(true);
					} else {					
						return null;
					}
				}
				
				return handleRestore(member);
			}
		}
		
		return null;
	}

	private Object handleRestore(Member member) {
		HistorySelectionDialog cesd = 
				new HistorySelectionDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), member.toDisplayName());
		
		cesd.setInitialPattern("?");
		
		int open = cesd.open();
		
		if (open == Window.OK) {
			String selectedkey = (String) cesd.getFirstResult();

			byte[] history;
			try {
				history = LocalHistory.getInstance().retrieve(HistorySelectionDialog.fromDisplay(selectedkey), member.toDisplayName());
			} catch (HistoryException | ParseException e) {
				LOG.warn("Cannot get history entry {}, reason:", member.toDisplayName(), e);
				
				MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, Activator.getDefault().getString(TITLE) 
						, Activator.getDefault().getString("Restore.Error", (member).getName(), e.getMessage()));

				return null;
			}

			if (history != null) {
				CompareConfiguration cc = new CompareConfiguration();
				cc.setLeftEditable(true);
				cc.setRightEditable(false);
				
				cc.setLeftLabel(Activator.getDefault().getString("Restore.Online") + " " + member.toDisplayName());
				cc.setRightLabel(Activator.getDefault().getString("Restore.History") + " " + selectedkey);
				
				CompareUI.openCompareDialog(new CompareInput(cc, member, history));
			} else {
				MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
						, Activator.getDefault().getString(TITLE) 
						, Activator.getDefault().getString("Restore.NoHistory", (member).getName()));
			}
		}
		
		return null;
	}
}
