/*********************************************************************
* Copyright (c) 09.10.2023 Thomas Zierer
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package de.tgmz.zdev.outline.view;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.model.AdaptableList;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditor;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ibm.cics.zos.ui.editor.ZOSObjectEditorInput;

import de.tgmz.zdev.outline.IOutlineParser;
import de.tgmz.zdev.outline.JclOutlineParser;
import de.tgmz.zdev.outline.MarkElement;
import de.tgmz.zdev.outline.PliOutlineParser;
import de.tgmz.zdev.preferences.Language;

/**
 * Outline View.
 */
public class ZdevContentOutlinePage extends ContentOutlinePage {
	private static final Logger LOG = LoggerFactory.getLogger(ZdevContentOutlinePage.class);
	private AbstractDecoratedTextEditor input;
	private IOutlineParser parser;
	
	/**
	 * CTR.
	 * @param input Input
	 * @param l Language
	 */
	public ZdevContentOutlinePage(AbstractDecoratedTextEditor input, Language l) {
		super();
		this.input = input;
		
		if (l == Language.JCL) { 
			parser = new JclOutlineParser();
		} else {
			parser = new PliOutlineParser();
		}			
	}
    /**  
     * Creates the control and registers the popup menu for this page
     * Menu id "org.eclipse.ui.examples.readmetool.outline"
     */
    @Override
	public void createControl(Composite parent) {
        super.createControl(parent);

        TreeViewer viewer = getTreeViewer();
        viewer.setContentProvider(new WorkbenchContentProvider());
        viewer.setLabelProvider(new WorkbenchLabelProvider());
        viewer.addSelectionChangedListener(this);
        viewer.setInput(getContentOutline(input.getEditorInput()));
    }

    /**
     * Gets the content outline for a given input element.
     * Returns the outline (a list of MarkElements), or null
     * if the outline could not be generated.
     */
    public IAdaptable getContentOutline(IAdaptable adaptable) {
    	try {
			String s = getContents(adaptable);
			
			LOG.info("Content: {}", s.length());
			
			MarkElement[] mes = parser.parse(adaptable, s);
			
			LOG.info("Number of markers: {}", mes.length);
			
			return new AdaptableList(mes);
		} catch (IOException | CoreException e) {
			LOG.error("Cannot get outline hirarchy", e);
		}
    	
    	return null;
    }
    
	private String getContents(IAdaptable adaptable) throws IOException, CoreException {
		String s = "";
		
    	if (adaptable instanceof ZOSObjectEditorInput zoei) {
    		s = zoei.getContents();

    		int i = 0;
    		
    		// getContents() sometimes delivers an empty string
    		while (s.length() == 0 && ++i < 10) {
        		try {
        			LOG.debug("Content empty, trying again...");
        			
    				Thread.sleep(100);
    				
    				s = zoei.getContents();
    			} catch (InterruptedException e) {
        			LOG.error("Interrupted!", e);

        			Thread.currentThread().interrupt();
    			}
    		}
		} else {
			if (adaptable instanceof IFileEditorInput ffe) {
				IFile f = ffe.getFile();
				
				s = IOUtils.toString(f.getContents(true), f.getCharset());
			}
		}
    	
    	return s;
	}

    /**
     * Forces the page to update its contents.
     */
    public void update() {
        getControl().setRedraw(false);
        getTreeViewer().setInput(getContentOutline(input));
        getTreeViewer().expandAll();
        getControl().setRedraw(true);
    }
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);
		
		ISelection selection= event.getSelection();
		
		if (selection.isEmpty()) {
			input.resetHighlightRange();
		} else {
			MarkElement segment= (MarkElement) ((IStructuredSelection) selection).getFirstElement();
			
			int lineNumber = Math.max(segment.getStart() - 1, 0);
			int length = segment.getLength();
			
			IDocumentProvider provider= input.getDocumentProvider();
			IDocument document= provider.getDocument(input.getEditorInput());
			
			try {
				int start = document.getLineOffset(lineNumber);
				input.setHighlightRange(start, length, true);
				input.selectAndReveal(start, 0);
			} catch (BadLocationException e) {
				LOG.warn("Cannot get offset for line {}", lineNumber, e);
			}
		}

	}
}
