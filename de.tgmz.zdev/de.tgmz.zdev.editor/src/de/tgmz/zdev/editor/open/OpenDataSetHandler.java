/*********************************************************************
* Copyright (c) 06.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.editor.open;

import java.io.FileNotFoundException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.DataEntry;
import com.ibm.cics.zos.model.DataPath;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.MigratedDataSet;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.ui.editor.DataEntryEditorInput;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.editor.Activator;

/**
 * Tries to open a dataset if its name is selected in editor.
 */
//CHECKSTYLE DISABLE ReturnCount
public class OpenDataSetHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(OpenDataSetHandler.class);
	private static final String OPEN_EDITOR_TITLE = "OpenEditor.Title";
	
	/**
	 * @return der markierte Text
	 */
	private String getCurrentTextSelection() {
		String selectedText = null;

		ITextEditor activeEditor = PlatformUI.getWorkbench()
											.getActiveWorkbenchWindow()
											.getActivePage()
											.getActiveEditor()
											.getAdapter(ITextEditor.class); 

		if (activeEditor != null) {
			ISelection selection = activeEditor.getSelectionProvider().getSelection();

			if (selection instanceof TextSelection) {
				selectedText = ((ITextSelection) selection).getText();
			}
		}

		return selectedText;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String selectedText = getCurrentTextSelection();

		DataEntry de;
		
		DataPath dp = new DataPath(selectedText);
		
		try {
			if (dp.memberName != null) {
				PartitionedDataSet pds = (PartitionedDataSet) ZdevConnectable.getConnectable().getDataSet(dp.dataSetName);
				de = ZdevConnectable.getConnectable().getDataSetMember(pds, dp.memberName);
			} else {
				de = ZdevConnectable.getConnectable().getDataSet(dp.getPath());
			}
		} catch (FileNotFoundException e) {
			LOG.warn("File not found", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.FileNotFound", selectedText));
			
			return null;
		} catch (PermissionDeniedException e) {
			LOG.warn("No permission", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.NoPermission", selectedText));
				
			return null;
		} catch (ConnectionException e) {
			LOG.error("ConnectionException", e);
				
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.ConnectionException", selectedText));
		
			return null;
		} catch (ClassCastException e) {
			LOG.warn("Unsupported type", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.WrongType", selectedText));
		
			return null;
		}
		
		if (de instanceof MigratedDataSet) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.Migrated", selectedText));
		
			return null;
		}
		
		if (!((de instanceof SequentialDataSet) || de instanceof Member)) {
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.WrongType", selectedText));
		
			return null;
		}
		
		IEditorInput ei = new DataEntryEditorInput(de);
			
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		
		try {
			return activePage.openEditor(ei, "com.ibm.cics.zos.ui.dataentry.editor");
		} catch (PartInitException e) {
			LOG.error("Cannot open editor", e);
				
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					Activator.getDefault().getString(OPEN_EDITOR_TITLE),
					Activator.getDefault().getString("OpenEditor.Failed"));
		
			return null;
		}
	}
}
