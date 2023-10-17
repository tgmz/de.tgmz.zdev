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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.core.comm.ConnectionException;
import com.ibm.cics.zos.comm.IZOSConstants.FileType;
import com.ibm.cics.zos.model.Member;
import com.ibm.cics.zos.model.PermissionDeniedException;
import com.ibm.cics.zos.model.SequentialDataSet;
import com.ibm.cics.zos.model.UnsupportedOperationException;

import de.tgmz.zdev.connection.ZdevConnectable;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.plicomp.PlicompConfigurationException;
import de.tgmz.zdev.plicomp.PlicompException;
import de.tgmz.zdev.plicomp.PlicompFactory;
import de.tgmz.zdev.xinfo.generated.MESSAGE;
import de.tgmz.zdev.xinfo.generated.PACKAGE;

/**
 * Syntaxcheck.
 */
public class LocalSyntaxcheckHandler extends AbstractSyntaxcheckHandler {
	private static final Logger LOG = LoggerFactory.getLogger(LocalSyntaxcheckHandler.class);
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ITextEditor editor = (ITextEditor) HandlerUtil.getActiveEditor(event);
		IDocumentProvider documentProvider = editor.getDocumentProvider();
		IEditorInput editorInput = editor.getEditorInput();

		SequentialDataSet errorFeedback = null;
		Member tempSource = null;
		
        try {
        	errorFeedback = TempDataSetFactory.getInstance().createErrorFeedback();

        	String name = FilenameUtils.getName(editorInput.getName());
        	String mbr = FilenameUtils.removeExtension(name);
        	
        	tempSource = TempDataSetFactory.getInstance().createTempSrc(mbr);
        	
        	String content = documentProvider.getDocument(editorInput).get();
        	
    		IEncodingSupport encodingSupport = editor.getAdapter(IEncodingSupport.class);
    		
    		String encoding = encodingSupport == null ? null : encodingSupport.getEncoding();
        	
    		// If encoding == null toInputStream falls back to getBytes()
        	ZdevConnectable.getConnectable().save(tempSource, IOUtils.toInputStream(content, encoding));
        	
        	// Create a temporary item to ease jcl generation
        	Item item = new Item(tempSource.getParentPath(), tempSource.getName());
        	
			String jcl = createJcl(item, errorFeedback.getFullPath());
			
			submit(jcl);
			
			deleteMarkers(editorInput.getName());
			
			deleteAnnotations(editor);
            
			try(ByteArrayOutputStream bos = ZdevConnectable.getConnectable().getContents(errorFeedback, FileType.EBCDIC)) {
				PACKAGE plicomp = PlicompFactory.getInstance().getPlicomp(new ByteArrayInputStream(bos.toByteArray()), editorInput.getName());
           	
				List<MESSAGE> messages = plicomp.getMESSAGE();
			
				for (int i = 0; i < messages.size() && i < Short.MAX_VALUE; ++i) {
					try {
						createMarkerAndAnnotation(editor, messages.get(i), editorInput.getName());
					} catch (CoreException e) {
						LOG.error("Error", e);
					
						MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
								, Activator.getDefault().getString("Syntaxcheck.Title")
								, Activator.getDefault().getString("Syntaxcheck.CreateMarkersFailed"));
					}
				}
			}
		} catch (ConnectionException | PlicompException | PlicompConfigurationException | IOException e) {
			LOG.error("Error", e);
			
			MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell() 
		    		, Activator.getDefault().getString("Syntaxcheck.Title")
		    		, Activator.getDefault().getString("Syntaxcheck.Failed"));

		} finally {
			try {
				TempDataSetFactory.getInstance().delete(errorFeedback);
				
				if (tempSource != null) {
					TempDataSetFactory.getInstance().delete(ZdevConnectable.getConnectable().getDataSet(tempSource.getParentPath()));
				}
			} catch (PermissionDeniedException | UnsupportedOperationException | ConnectionException | FileNotFoundException e) {
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

