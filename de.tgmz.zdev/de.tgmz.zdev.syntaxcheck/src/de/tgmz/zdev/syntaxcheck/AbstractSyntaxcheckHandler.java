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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.tgmz.zdev.compile.JclFactory;
import de.tgmz.zdev.domain.Item;
import de.tgmz.zdev.editor.annotation.CompilerMessageAnnotation;
import de.tgmz.zdev.operation.JCLOperationRunnable;
import de.tgmz.zdev.preferences.Language;
import de.tgmz.zdev.preferences.ZdevPreferenceConstants;
import de.tgmz.zdev.xinfo.generated.MESSAGE;

/**
 * Syntax check.
 */
public abstract class AbstractSyntaxcheckHandler extends AbstractHandler {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractSyntaxcheckHandler.class);
	
	protected void deleteMarkers(String location) throws ExecutionException {
		try {
			IMarker[] markers = ResourcesPlugin.getWorkspace().getRoot().findMarkers(null, false, IResource.DEPTH_ZERO);
			
			for (IMarker m : markers) {
				if (location.equals(m.getAttribute(IMarker.LOCATION))) {
					m.delete();
				}
			}
		} catch (CoreException e) {
			throw new ExecutionException("Core Exception", e);
		}
	}
	
	protected void deleteAnnotations(ITextEditor editor) {
		IDocumentProvider documentProvider = editor.getDocumentProvider();
		IEditorInput editorInput = editor.getEditorInput();

		IAnnotationModel am = documentProvider.getAnnotationModel(editorInput);
		
		Iterator<Annotation> annotationIter = am.getAnnotationIterator();
				
		while (annotationIter.hasNext()) {
			Annotation a = annotationIter.next();
			
			if (a instanceof CompilerMessageAnnotation) {
				am.removeAnnotation(a);
			}
		}
	}
	
	protected void submit(String jcl) throws ExecutionException {
        JCLOperationRunnable runner = new JCLOperationRunnable(jcl);

        try {
            PlatformUI
               	.getWorkbench()
               	.getProgressService()
               	.runInUI(PlatformUI.getWorkbench().getProgressService(), runner, ResourcesPlugin.getWorkspace().getRoot());
        } catch (InvocationTargetException e) {
        	throw new ExecutionException("Exception during syntaxcheck", e);
        } catch (InterruptedException e) {
        	Thread.currentThread().interrupt();
        	throw new ExecutionException("Thread got interrupted", e);
        }
	}

	protected String createJcl(Item item, String errorFeedback) throws IOException {
		String result = de.tgmz.zdev.preferences.Activator.getDefault().getPreferenceStore().getString(ZdevPreferenceConstants.JOB_CARD);
		
		result += System.lineSeparator();
		
		Language l = Language.fromDatasetName(item.getDsn());
		
		String errorFeedbackDd = l == Language.C || l == Language.CPP ? "SYSEVENT" : "SYSXMLSD"; 

		result += JclFactory.getInstance().createCompileStep(item, "//" + errorFeedbackDd + " DD DISP=SHR,DSN=" + errorFeedback);
		
		return result;
	}

	protected static void createMarkerAndAnnotation(ITextEditor editor, MESSAGE msg, String displayName) throws CoreException {
		IMarker marker = ResourcesPlugin.getWorkspace().getRoot().createMarker(IMarker.PROBLEM);
		marker.setAttribute(IMarker.LINE_NUMBER, msg.getMSGLINE());
		marker.setAttribute(IMarker.LOCATION, displayName);
		marker.setAttribute(IMarker.MESSAGE, msg.getMSGNUMBER() 
							+ " - " 
							+ msg.getMSGTEXT().replace("\r", "").replace("\n", ""));	// Assembler and COBOL add line feeds in their messages 
		
		switch (msg.getMSGNUMBER().charAt(msg.getMSGNUMBER().length() - 1)) {
		case 'I': 
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			break;
		case 'W': 
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			break;
		default: 
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			break;
		}

		marker.setAttribute(IMarker.SOURCE_ID, displayName);

		addCompilerMessageAnnotation(editor, marker);
	}
	
	public static void addCompilerMessageAnnotation(ITextEditor editor, IMarker marker) throws CoreException {
		int lineNumber;
		
		try {
			// Line number of editor start at 0, the compiler messages start at 1.
			lineNumber = Integer.parseInt(String.valueOf(marker.getAttribute(IMarker.LINE_NUMBER))) - 1;
		} catch (NumberFormatException e) {
			LOG.error("NumberFormatException: {}", e.getMessage());
			
			return;
		}
		
		CompilerMessageAnnotation cma = new CompilerMessageAnnotation(lineNumber);

		switch ((int) marker.getAttribute(IMarker.SEVERITY)) {
		case IMarker.SEVERITY_INFO: 
			cma.setType("org.eclipse.jdt.ui.info");
			break;
		case IMarker.SEVERITY_WARNING: 
			cma.setType("org.eclipse.jdt.ui.warning");
			break;
		case IMarker.SEVERITY_ERROR: 
		default: 
			cma.setType("org.eclipse.jdt.ui.error");
			break;
		}

		cma.setText((String) marker.getAttribute(IMarker.MESSAGE));
		
		IDocument d = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		
		if (d.getNumberOfLines() < lineNumber) {
			LOG.warn("Linenumber {} out of range, reduced to {}", lineNumber, d.getNumberOfLines() - 1);
			
			lineNumber = d.getNumberOfLines() - 1;
		}
		
		try {
			Position p = new Position(d.getLineOffset(lineNumber));

			editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput()).addAnnotation(cma, p);
		} catch (BadLocationException e) {
			String s = "Cannot compute position for line number ";
			
			if (lineNumber == -1) {
				LOG.warn(String.format("%s %s. Probably test", s, marker.getAttribute(IMarker.LINE_NUMBER)));	//Don't log stack trace here
			} else { 
				LOG.warn(s, marker.getAttribute(IMarker.LINE_NUMBER), e);
			}
		}
	}
}

