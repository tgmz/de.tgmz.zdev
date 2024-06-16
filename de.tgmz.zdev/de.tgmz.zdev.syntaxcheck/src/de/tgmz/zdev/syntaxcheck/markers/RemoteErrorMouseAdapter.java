/*********************************************************************
* Copyright (c) 10.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.syntaxcheck.markers;

import java.io.FileNotFoundException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.markers.MarkerItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PartitionedDataSet;
import com.ibm.cics.zos.model.PermissionDeniedException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.editor.ZdevEditor;
import de.tgmz.zdev.syntaxcheck.Activator;

/**
 * MouseAdapter. Opens the editor on double click.
 */
public class RemoteErrorMouseAdapter extends MouseAdapter {
	private static final Logger LOG = LoggerFactory.getLogger(RemoteErrorMouseAdapter.class);
	private static final String TITLE = "RemoteErrorMarkersView.Title";
	
	//CHECKSTYLE DISABLE ReturnCount
    @Override
    public void mouseDoubleClick(final MouseEvent me) {
        if (me.widget instanceof Tree) {
        	Object o = ((Tree) me.widget).getSelection()[0].getData();
        	
        	if (o instanceof MarkerItem) {
        		IMarker iMarker = ((MarkerItem) o).getMarker();

				if (ZdevConnectable.getConnectable() == null || !ZdevConnectable.getConnectable().isConnected()) {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
	                		, Activator.getDefault().getString(TITLE)
	                		, Activator.getDefault().getString("RemoteErrorMarkersView.NotConnected")); //$NON-NLS-1$ 
						
					return;
				} 
				
				try {
					int lineNumber = Integer.parseInt((String) iMarker.getAttribute(IMarker.LINE_NUMBER));
					String location = (String) iMarker.getAttribute(IMarker.LOCATION);
					
					Member member;
					try {
						int idx0 = location.indexOf('(');
						int idx1 = location.indexOf(')');
						
						String s0 = location.substring(0, idx0);
						String s1 = location.substring(idx0 + 1, idx1);

						PartitionedDataSet parent = (PartitionedDataSet) ZdevConnectable.getConnectable().getDataSet(s0);
						
						member = ZdevConnectable.getConnectable().getDataSetMember(parent, s1);
					} catch (FileNotFoundException | PermissionDeniedException | ConnectionException e) {
						LOG.warn("Problem accessing file", e);
						
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
		                		, Activator.getDefault().getString(TITLE)
		                		, Activator.getDefault().getString("RemoteErrorMarkersView.NotFound", location)); //$NON-NLS-1$ 
							
						return;
					}
					
					ITextEditor editor = ZdevEditor.findEditor(member, true);
					
					if (editor == null) {
						MessageDialog.openWarning(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
		                		, Activator.getDefault().getString(TITLE)
		                		, Activator.getDefault().getString("RemoteErrorMarkersView.NotOpen")); //$NON-NLS-1$ 
						
						return;
					}
					
					IDocumentProvider provider= editor.getDocumentProvider();
					IDocument document= provider.getDocument(editor.getEditorInput());
					
					try {
						int start = document.getLineOffset(lineNumber);
						editor.selectAndReveal(start, 0);
					} catch (BadLocationException e) {
						LOG.warn("Cannot get offset for line {}", lineNumber, e);
					}

					IWorkbenchPage page= editor.getSite().getPage();
					page.activate(editor);
				} catch (CoreException e1) {
					LOG.error("CoreException", e1);
				}
    		}
        }
    }
	//CHECKSTYLE ENABLE ReturnCount
}
